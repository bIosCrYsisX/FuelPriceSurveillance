package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;


public class Notifier {

    private Context context;
    private String channelId = "alertChannel";

    public Notifier(Context context)
    {
        this.context = context;
    }

    public void throwNotification(String title, String text)
    {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null){
                NotificationChannel notificationChannel =
                        notificationManager.getNotificationChannel(channelId);

                if(notificationChannel == null){
                    notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
        }

        if(notificationManager != null) {
            Notification n = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setLights(Color.BLUE, 1000, 3000)
                    .setVibrate(new long[]{0, 300, 300, 300})
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(1, n);
        }
    }
}