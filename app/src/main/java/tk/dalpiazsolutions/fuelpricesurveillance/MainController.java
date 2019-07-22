package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.StatementEvent;

import tk.dalpiazsolutions.fuelpricesurveillance.dao.ArticleDAO;
import tk.dalpiazsolutions.fuelpricesurveillance.dao.ItemDAO;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Article;
import tk.dalpiazsolutions.fuelpricesurveillance.models.Item;

/**
 * Created by Christoph on 08.06.2018.
 */

public class MainController {

    private TabArticles tabArticles;
    private TabFuel tabFuel;
    private MainModel mainModel;
    private MainActivity mainActivity = new MainActivity();
    private FuelDownloader fuelDownloader;
    private PreferenceManager preferenceManager;
    private Context context;
    private AlarmManager priceManager;
    private PendingIntent priceIntent;
    private Calendar calendar;
    private PriceDatabase priceDB;
    private ArticleDatabase articleDB;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private String tankName = "";
    private boolean notExact = false;
    private boolean state = false;


    public MainController(TabArticles tabArticles)
    {
        this.tabArticles = tabArticles;
        this.context = tabArticles.getContext();
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(tabArticles.getContext());
        priceDB = Room.databaseBuilder(tabArticles.getContext().getApplicationContext(), PriceDatabase.class, "priceDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        articleDB = Room.databaseBuilder(tabArticles.getContext().getApplicationContext(), ArticleDatabase.class, "articleDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        priceManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        priceIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    public MainController(TabFuel tabFuel)
    {
        this.tabFuel = tabFuel;
        this.context = tabFuel.getContext();
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(tabFuel.getContext());
        priceDB = Room.databaseBuilder(tabFuel.getContext().getApplicationContext(), PriceDatabase.class, "priceDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        articleDB = Room.databaseBuilder(tabFuel.getContext().getApplicationContext(), ArticleDatabase.class, "articleDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        priceManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        priceIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    public MainController(Context context)
    {
        this.context = context;
        mainModel = new MainModel(mainActivity);
        preferenceManager = new PreferenceManager(context);
        priceDB = Room.databaseBuilder(context.getApplicationContext(), PriceDatabase.class, "priceDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        articleDB = Room.databaseBuilder(context.getApplicationContext(), ArticleDatabase.class, "articleDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        priceManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        priceIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    public void insertPrice(float price)
    {
        if(price > 0)
        {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();

            ItemDAO itemDAO = priceDB.getItemDAO();
            Item item = new Item();
            item.setPrice(price);
            Log.i("year", Integer.toString(date.getYear()));
            item.setDate(String.format(Locale.getDefault(), context.getString(R.string.dateString), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
            item.setTime(dateFormat.format(date));
            item.setTankName(tankName);
            item.setNotExact(notExact);
            itemDAO.insert(item);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (price < 1.23) {
                    Intent notificationIntent = new Intent(context, FuelService.class);
                    notificationIntent.putExtra("title", context.getString(R.string.pricealert));
                    notificationIntent.putExtra("text", String.format(Locale.getDefault(), context.getString(R.string.priceValue), price));
                    context.startForegroundService(notificationIntent);
                } else {
                    Random random = new Random();
                    int n = random.nextInt(4) + 1;

                    if (n == 5) {
                        Intent notificationIntent = new Intent(context, FuelService.class);
                        notificationIntent.putExtra("title", context.getString(R.string.actualprice));
                        notificationIntent.putExtra("text", String.format(Locale.getDefault(), context.getString(R.string.priceValue), price));
                        context.startForegroundService(notificationIntent);
                    }
                }
            }

            if(getPrices().size() >= 48)
            {
                if(uploadDB())
                {
                    nukeTable();
                }
            }
        }
    }

    public boolean uploadDB()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.i("ONE", "ONE");
        if (wifiMgr.isWifiEnabled())
        {
            Log.i("TWO", "TWO");
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() != -1)
            {
                String ssid = info.getExtraInfo().substring(1, info.getExtraInfo().length() - 1);
                Log.i("SSID", ssid);
                Log.i("SSID", context.getString(R.string.SSID));

                if(ssid.equals(context.getString(R.string.SSID)))
                {
                    Log.i("equal", "equal");

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Thread", "started");
                            DBUploader dbUploader = new DBUploader(context);
                            changeState(dbUploader.uploadDB());
                            Log.i("state", Boolean.toString(state));
                        }
                    });
                    thread.start();

                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return state;
                }
            }
        }
        return false;
    }

    public void changeState(boolean state)
    {
        this.state = state;
    }

    public void nukeTable()
    {
        ItemDAO itemDAO = priceDB.getItemDAO();
        itemDAO.nukeTable();
        listPrices();

        ArticleDAO articleDAO = articleDB.getArticleDAO();
        articleDAO.nukeTable();
    }

    public void startMonitoring()
    {
        Log.i("STARTEDM", "started");
        Calendar now = Calendar.getInstance();
        priceManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis() - 60000,60*60*1000, priceIntent);
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
            if(prices.get(i).isNotExact())
            {
                textPrices.add(String.format(Locale.getDefault(), context.getString(R.string.dataStringNotExact), prices.get(i).getPrice(), prices.get(i).getTime(), prices.get(i).getDate(), prices.get(i).getTankName()));
            }

            else {
                textPrices.add(String.format(Locale.getDefault(), context.getString(R.string.dataStringExact), prices.get(i).getPrice(), prices.get(i).getTime(), prices.get(i).getDate(), prices.get(i).getTankName()));
            }
        }

        if(tabFuel != null) {
            tabFuel.setArrayAdapter(new ArrayAdapter(tabFuel.getContext(), android.R.layout.simple_list_item_1, textPrices));
        }
    }

    public float getPrice(boolean cheapest)
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
            if(cheapest == false)
            {
                tankName = context.getString(R.string.omv);
            }

            else
            {
                mainModel.setCompleteSite(fuelDownloader.execute("https://tankbillig.in/index.php?long=14.422061499999998&lat=48.326499&show=0&treibstoff=super95&switch").get());
                tankName = mainModel.getCompleteSite().split("<span id=\"gasStationNameSpan\">")[1];
                tankName = tankName.split("</span>")[0];
            }
            fuelDownloader = new FuelDownloader();
            mainModel.setCounter(mainModel.getCounter() + 1);
            mainModel.setCompleteSite(fuelDownloader.execute("https://tankbillig.in/index.php?long=14.422061499999998&lat=48.326499&show=0&treibstoff=super95&switch", tankName).get());
            Log.i("completeSite", mainModel.getCompleteSite());
            trimToPrice();
            //savePrice(mainModel.getPrice(), mainModel.getCounter());
            //calcAndSaveTime();
            preferenceManager.saveCounter(mainModel.getCounter());
            Log.i("counter", Integer.toString(mainModel.getCounter()));

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

        if(price.contains("no-exact-price"))
        {
            Log.i("Pricetext", price);
            notExact = true;
        }

        else
        {
            notExact = false;
        }

        int i = 0;

        while(i < price.length() && price.charAt(i) != '€')
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

    public void getArticles()
    {
        boolean relevant = false;
        int x = 0;
        fuelDownloader = new FuelDownloader();
        try {
            String site = fuelDownloader.execute(context.getString(R.string.newsURL), "articles").get();
            Log.i("SITE", site);
            Pattern pattern = Pattern.compile("aria-expanded=\"false\">(.*?)</a>");
            Matcher matcher = pattern.matcher(site);

            String formattedArticle;
            LinkedList<String> articles = new LinkedList<>();
            while (matcher.find())
            {
                formattedArticle = matcher.group(1);
                int lowerBorder = 0;
                while (formattedArticle.charAt(lowerBorder) == ' ')
                {
                    lowerBorder++;
                }
                int upperBoarder = formattedArticle.length() - 1;
                while (formattedArticle.charAt(upperBoarder) == ' ')
                {
                    upperBoarder--;
                }

                formattedArticle = formattedArticle.substring(lowerBorder, upperBoarder + 1);

                articles.add(formattedArticle);
            }

            boolean alreadySaved = false;
            for(int i = 0; i < articles.size(); i++)
            {
                Log.i("article", articles.get(i));
                String[] parts = context.getString(R.string.keywords).split(",");
                relevant = false;
                x = 0;

                while(x < parts.length && !relevant)
                {
                    Log.i("PARTS", parts[x]);
                    if(articles.get(i).toLowerCase().contains(parts[x]))
                    {
                        relevant = true;
                    }
                    x++;
                }
                if(relevant)
                {
                    Calendar calendar = Calendar.getInstance();
                    String date = String.format(Locale.getDefault(), "%d:%d:%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
                    Article article = new Article();
                    article.setText(articles.get(i));
                    article.setDate(date);
                    ArticleDAO articleDAO = articleDB.getArticleDAO();

                    List<Article> savedArticles = articleDAO.getArticles();

                    for(int y = 0; y < savedArticles.size(); y++)
                    {
                        if(savedArticles.get(y).getText().equals(article.getText()))
                        {
                            alreadySaved = true;
                        }
                    }

                    if(!alreadySaved)
                    {
                        Notifier notifier = new Notifier(context);
                        notifier.throwNotification("News", String.format(Locale.getDefault(), context.getString(R.string.news), articles.get(i)));
                        articleDAO.insert(article);
                        alreadySaved = false;
                    }

                    if(!preferenceManager.getNotified())
                    {
                        Notifier notifier = new Notifier(context);
                        notifier.throwNotification("News", String.format(Locale.getDefault(), context.getString(R.string.news), articles.get(i)));
                        preferenceManager.setNotified();
                    }

                    if(tabArticles != null)
                    {
                        tabArticles.listArticle(article.getText());
                    }
                }
            }
            if(tabArticles != null) {
                tabArticles.setArrayAdapter(new ArrayAdapter(tabArticles.getContext(), android.R.layout.simple_list_item_1, articles));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getRelevantArticles()
    {
        ArticleDAO articleDAO = articleDB.getArticleDAO();
        List<Article> articles = articleDAO.getArticles();
        LinkedList<String> textArticles = new LinkedList<>();

        for(int i = 0; i < articles.size(); i++)
        {
            textArticles.add(String.format(Locale.getDefault(), "%s, %s", articles.get(i).getText(), articles.get(i).getDate()));
        }

        if(tabArticles != null)
        {
            tabArticles.setArrayAdapter(new ArrayAdapter(tabArticles.getContext(), android.R.layout.simple_list_item_1, textArticles));
        }
    }

    public void listArticles()
    {
        ArticleDAO articleDAO = articleDB.getArticleDAO();
        List<Article> articles = articleDAO.getArticles();
        //List<String> textArticles = new LinkedList<>();
        for(int i = 0; i < articles.size(); i++)
        {
            Log.i("ARTICLE:", articles.get(i).getText());
        }
    }

    public boolean isNotExact() {
        return notExact;
    }
}
