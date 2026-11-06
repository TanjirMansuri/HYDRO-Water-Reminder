package com.hydro.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "hydro_water";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                "Water Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setSound(null, null);
            manager.createNotificationChannel(channel);
        }

        Notification.Builder builder =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? new Notification.Builder(context, channelId)
            : new Notification.Builder(context);

        builder.setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("💧 HYDRO")
            .setContentText("पानी पीने का समय हो गया है!")
            .setAutoCancel(true);

        manager.notify(1001, builder.build());
    }
}
