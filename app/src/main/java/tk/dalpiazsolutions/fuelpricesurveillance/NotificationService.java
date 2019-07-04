package tk.dalpiazsolutions.fuelpricesurveillance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Created by Christoph on 09.06.2018.
 */

public class NotificationService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            notify(title, text, intent);

            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void notify(String title, String text, Intent intent) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            String channelId = "channel1";

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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
                Notification n = new NotificationCompat.Builder(this, "channel1")
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
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
                notificationManager.notify(0, n);
            }
        }
}


