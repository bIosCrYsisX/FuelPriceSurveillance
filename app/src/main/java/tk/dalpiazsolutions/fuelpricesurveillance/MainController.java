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
    private MailController mailController;
    private Calendar calendar;

    public MainController(FuelService fuelService)
    {
        this.fuelService = fuelService;
        this.mailController = new MailController(fuelService);
        mainModel = new MainModel(fuelService);
        preferenceManager = new PreferenceManager(fuelService);
    }

    public MainController(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.mailController = new MailController(mainActivity);
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(mainActivity);
    }

    public float getPrice()
    {
        calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        Log.i("hours", Integer.toString(hour));
        Log.i("minutes", Integer.toString(minutes));

        if(hour == 12 && (minutes >= 0 && minutes <= 10))
        {
            return -2;
        }

        fuelDownloader = new FuelDownloader();

        mainModel.setCounter(preferenceManager.getCounter());
        try {
            mainModel.setCounter(mainModel.getCounter() + 1);
            mainModel.setCompleteSite(fuelDownloader.execute("https://tankbillig.in/index.php?long=14.422061499999998&lat=48.326499&show=0&treibstoff=super95&switch").get());
            Log.i("completeSite", mainModel.getCompleteSite());
            trimToPrice();
            savePrice(mainModel.getPrice(), mainModel.getCounter());
            calcAndSaveTime();
            preferenceManager.saveCounter(mainModel.getCounter());
            Log.i("counter", Integer.toString(mainModel.getCounter()));

            //if(mainActivity != null)
            //{
                checkNotification();
            //}

            if(mainModel.getCounter() == 24)
            {
                mailController.sendValuesMail(mailController.getValues(mainModel.getCounter()));
                mainModel.setCounter(0);
                preferenceManager.saveCounter(mainModel.getCounter());
            }
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

    public void calcAndSaveTime()
    {
        calendar = new GregorianCalendar();

        String time = "";

        time = time + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE));

        Log.i("time", time);

        preferenceManager.saveTime(time, preferenceManager.getCounter() + 100);
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

            else
            {
                Intent notificationIntent = new Intent(fuelService.getApplicationContext(), NotificationService.class);
                notificationIntent.putExtra("title", fuelService.getString(R.string.actualprice));
                notificationIntent.putExtra("text", Float.toString(mainModel.getPrice()) + "€");
                fuelService.startService(notificationIntent);
            }
        }
    }
}
