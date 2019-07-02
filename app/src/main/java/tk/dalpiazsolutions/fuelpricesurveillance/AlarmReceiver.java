package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private MainController mainController;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm", "RECEIVED");
        mainController = new MainController(context);
        mainController.insertPrice();
    }
}
