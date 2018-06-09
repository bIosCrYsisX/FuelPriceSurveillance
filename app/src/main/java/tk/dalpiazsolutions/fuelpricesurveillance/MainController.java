package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Christoph on 08.06.2018.
 */

public class MainController {

    private FuelService fuelService;
    private MainActivity mainActivity;
    private MainModel mainModel;
    private FuelDownloader fuelDownloader;
    private PreferenceManager preferenceManager;

    public MainController(FuelService fuelService)
    {
        this.fuelService = fuelService;
        mainModel = new MainModel(fuelService);
        preferenceManager = new PreferenceManager(fuelService);
    }

    public MainController(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(mainActivity);
    }

    public float getPrice()
    {
        Calendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        Log.i("hours", Integer.toString(hour));
        Log.i("minutes", Integer.toString(minutes));

        if(hour == 12 && (minutes >= 0 && minutes <= 10))
        {
            return -2;
        }

        fuelDownloader = new FuelDownloader();
        try {
            mainModel.setCompleteSite(fuelDownloader.execute("https://tankbillig.in/index.php?long=14.425511499999999&lat=48.331878999999994&show=0&treibstoff=super95&switch").get());
            Log.i("completeSite", mainModel.getCompleteSite());
            trimToPrice();
            savePrice(mainModel.getPrice(), mainModel.getCounter());
            mainModel.setCounter(mainModel.getCounter() + 1);
            checkNotification();
            return mainModel.getPrice();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void trimToPrice()
    {

        String price = mainModel.getCompleteSite();
        char[] priceCorrect = new char[5];

        int i = 0;

        while(price.charAt(i) != '€')
        {
            i++;
        }
        priceCorrect[0] = price.charAt(i - 6);
        priceCorrect[1] = '.';
        priceCorrect[2] = price.charAt(i - 4);
        priceCorrect[3] = price.charAt(i - 3);
        priceCorrect[4] = price.charAt(i - 2);

        Log.i("match", "match");

        Log.i("price", new String(priceCorrect));
        mainModel.setPrice(Float.parseFloat(new String(priceCorrect)));
    }

    public void savePrice(float price, int counter)
    {
        preferenceManager.addValue(price, counter);
    }

    public void checkNotification()
    {
        if(fuelService != null)
        {
            if (mainModel.getPrice() <= 1.27)
            {
                Intent notificationIntent = new Intent(fuelService.getApplicationContext(), NotificationService.class);
                notificationIntent.putExtra("title", fuelService.getString(R.string.pricealert));
                notificationIntent.putExtra("text", Float.toString(mainModel.getPrice()) + "€");
                fuelService.startService(notificationIntent);
            }

            else if ((mainModel.getCounter() % 3) == 0)
            {
                Intent notificationIntent = new Intent(fuelService.getApplicationContext(), NotificationService.class);
                notificationIntent.putExtra("title", fuelService.getString(R.string.actualprice));
                notificationIntent.putExtra("text", Float.toString(mainModel.getPrice()) + "€");
                fuelService.startService(notificationIntent);
            }
        }
    }
}
