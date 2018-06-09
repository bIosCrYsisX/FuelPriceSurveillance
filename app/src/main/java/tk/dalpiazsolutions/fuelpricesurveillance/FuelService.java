package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Christoph on 08.06.2018.
 */

public class FuelService extends Service {

    private MainController mainController;
    private final Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mainController = new MainController(this);
        handler.postDelayed(runnable, 1000);
        runnable = new Runnable() {
            @Override
            public void run() {
                mainController.getPrice();
                handler.postDelayed(runnable, 3600000);
            }
        };

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
