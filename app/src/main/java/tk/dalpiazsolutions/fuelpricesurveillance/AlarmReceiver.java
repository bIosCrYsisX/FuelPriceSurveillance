package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    private MainController mainController;

    @Override
    public void onReceive(Context context, Intent intent) {
        mainController = new MainController(context);
        mainController.insertPrice();
    }
}
