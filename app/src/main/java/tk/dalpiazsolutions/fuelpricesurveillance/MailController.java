package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Christoph on 10.06.2018.
 */

public class MailController {

    private Context context;
    private PreferenceManager preferenceManager;

    public MailController(Context context)
    {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }

    public void serviceStartedMail()
    {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL  , "chrisida@gmx.at");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Service running");
        mailIntent.putExtra(Intent.EXTRA_TEXT   , "Your watchdog is running now!");
        mailIntent.setData(Uri.parse("mailto: chrisida@gmx.at"));
        context.startActivity(Intent.createChooser(mailIntent, "Sende Mail..."));
    }

    public void sendValuesMail(String values)
    {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL  , "chrisida@gmx.at");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Latest prices");
        mailIntent.putExtra(Intent.EXTRA_TEXT   , values);
        mailIntent.setData(Uri.parse("mailto: chrisida@gmx.at"));
        context.startActivity(Intent.createChooser(mailIntent, "Sende Mail..."));
    }

    public String getValues(int counter)
    {
        String values = "";
        for(int i = 1; i <= counter; i++)
        {
            values = values + System.getProperty("line.separator") +  Float.toString(preferenceManager.getValue(i)) + "   " + preferenceManager.getTime(i + 100);
        }

        return values;
    }
}
