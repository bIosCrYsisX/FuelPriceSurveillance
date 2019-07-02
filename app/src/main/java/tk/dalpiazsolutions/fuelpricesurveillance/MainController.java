package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.room.Room;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import tk.dalpiazsolutions.fuelpricesurveillance.dao.ItemDAO;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Item;

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
    private Context context;
    private AlarmManager priceManager;
    private PendingIntent priceIntent;
    private Calendar calendar;
    private PriceDatabase priceDB;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private String time;

    public MainController(FuelService fuelService)
    {
        this.fuelService = fuelService;
        this.context = fuelService;
        this.mailController = new MailController(fuelService);
        mainModel = new MainModel(fuelService);
        preferenceManager = new PreferenceManager(fuelService);
    }

    public MainController(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.context = mainActivity;
        this.mailController = new MailController(mainActivity);
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(mainActivity);
        priceDB = Room.databaseBuilder(mainActivity.getApplicationContext(), PriceDatabase.class, "priceDB")
                .allowMainThreadQueries()
                .build();
        priceManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        priceIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    public MainController(Context context)
    {
        this.context = context;
        this.mailController = new MailController(context);
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(context);
        priceDB = Room.databaseBuilder(context.getApplicationContext(), PriceDatabase.class, "priceDB")
                .allowMainThreadQueries()
                .build();
        priceManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        priceIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    public void insertPrice()
    {
        float price = getPrice();

        if(price > 0)
        {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();

            ItemDAO itemDAO = priceDB.getItemDAO();
            Item item = new Item();
            item.setPrice(getPrice());
            Log.i("year", Integer.toString(date.getYear()));
            item.setDate(String.format(Locale.getDefault(), context.getString(R.string.dateString), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
            item.setTime(dateFormat.format(date));
            itemDAO.insert(item);
        }
    }

    public void nukeTable()
    {
        ItemDAO itemDAO = priceDB.getItemDAO();
        itemDAO.nukeTable();
        listPrices();
    }

    public void startMonitoring()
    {
        Log.i("STARTEDM", "started");
        Calendar now = Calendar.getInstance();
        priceManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis() - 60000,3*60*60*1000, priceIntent);
        listPrices();
    }

    public List<Item> getPrices()
    {
        ItemDAO itemDAO = priceDB.getItemDAO();
        return itemDAO.getItems();
    }

    public void listPrices()
    {
        Log.i("there", "there");
        ItemDAO itemDAO = priceDB.getItemDAO();
        List<Item> prices = itemDAO.getItems();
        List<String> textPrices = new LinkedList<>();
        Log.i("here", "here");
        for(int i = 0; i < prices.size(); i++)
        {
            Log.i("price", prices.get(i).toString());
            textPrices.add(String.format(Locale.getDefault(), context.getString(R.string.dataString), prices.get(i).getPrice(), prices.get(i).getTime(), prices.get(i).getDate()));
        }

        mainActivity.setArrayAdapter(new ArrayAdapter(mainActivity, android.R.layout.simple_list_item_1, textPrices));
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
            //Log.i("completeSite", mainModel.getCompleteSite());
            trimToPrice();
            savePrice(mainModel.getPrice(), mainModel.getCounter());
            calcAndSaveTime();
            preferenceManager.saveCounter(mainModel.getCounter());
            Log.i("counter", Integer.toString(mainModel.getCounter()));

            //if(mainActivity != null)
            //{
                //checkNotification();
            //}

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

        while(price.charAt(i) != '€' && i < price.length())
        {
            i++;
        }

        if(i == price.length())
        {
            mainModel.setPrice(-3);
        }

        else
        {
            priceCorrect[0] = price.charAt(i - 6);
            priceCorrect[1] = '.';
            priceCorrect[2] = price.charAt(i - 4);
            priceCorrect[3] = price.charAt(i - 3);
            priceCorrect[4] = price.charAt(i - 2);

            Log.i("price", new String(priceCorrect));
            mainModel.setPrice(Float.parseFloat(new String(priceCorrect)));
        }
    }

    public void savePrice(float price, int counter)
    {
        preferenceManager.addValue(price, counter);
    }

    public void calcAndSaveTime()
    {
        calendar = new GregorianCalendar();

        time = "";

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
                notificationIntent.putExtra("text", Float.toString(mainModel.getPrice()) + "€" + ", " + Integer.toString(mainModel.getCounter()) + ", " + time);
                fuelService.startService(notificationIntent);
            }

            else
            {
                Intent notificationIntent = new Intent(fuelService.getApplicationContext(), NotificationService.class);
                notificationIntent.putExtra("title", fuelService.getString(R.string.actualprice));
                notificationIntent.putExtra("text", Float.toString(mainModel.getPrice()) + "€" + ", " + Integer.toString(mainModel.getCounter()) + ", " + time);
                fuelService.startService(notificationIntent);
            }
        }
    }

}
