package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Christoph on 08.06.2018.
 */

public class FuelService extends Service {

    private MainController mainController;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(getApplicationContext(), NotificationService.class);
        notificationIntent.putExtra("title", "Service");
        notificationIntent.putExtra("text", "started");
        startService(notificationIntent);

        mainController = new MainController(this);
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                mainController.getPrice();
            }
        };
        timer.schedule(timerTask, 1000, 3600000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
