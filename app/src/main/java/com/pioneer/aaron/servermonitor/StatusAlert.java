package com.pioneer.aaron.servermonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Aaron on 6/25/15.
 */
public class StatusAlert {
    Context context;
    public StatusAlert(Context context) {
        this.context = context;
    }

    public void showNotification(int notification_ID, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("notification")
                .setContentTitle("Server Alert")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notification_ID, notification);
    }
}
