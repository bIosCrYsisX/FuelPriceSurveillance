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
        editor.apply();
    }

    public float getValue(int position)
    {
        return prefsPrice.getFloat(Integer.toString(position), 0);
    }

    public void saveCounter(int counter)
    {
        SharedPreferences.Editor editor = prefsPrice.edit();
        editor.putInt("counter", counter);
        editor.apply();
    }

    public int getCounter()
    {
        return prefsPrice.getInt("counter", 0);
    }

    public void saveTime(String time, int index)
    {
        SharedPreferences.Editor editor = prefsPrice.edit();
        editor.putString(Integer.toString(index), time);
        editor.apply();
    }

    public String getTime(int index)
    {
        return prefsPrice.getString(Integer.toString(index), "notime");
    }
}
