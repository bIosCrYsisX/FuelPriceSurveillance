package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Christoph on 08.06.2018.
 */

public class PreferenceManager {

    private Context context;
    private SharedPreferences prefsPrice;

    public PreferenceManager(Context context)
    {
        this.context = context;
        prefsPrice = context.getSharedPreferences("prices", Context.MODE_PRIVATE);
    }

    public void addValue(float value, int counter)
    {
        SharedPreferences.Editor editor = prefsPrice.edit();
        editor.putFloat(Integer.toString(counter), value);
    }
}
