package com.hydro.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleNext(Context context) {

        SharedPreferences prefs =
            context.getSharedPreferences("HYDRO", Context.MODE_PRIVATE);

        if (prefs.getBoolean("ride_mode", false)) {
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();

        int day = now.get(Calendar.DAY_OF_WEEK);
        boolean weekend =
            day == Calendar.SATURDAY || day == Calendar.SUNDAY;

        int[][] weekdayTimes = {
            {5,15}, {7,45}, {9,15}, {10,45},
            {12,15}, {13,45}, {15,15}, {16,45},
            {19,30}, {21,0}
        };

        int[][] weekendTimes = {
            {10,0}, {11,30}, {13,0}, {14,30},
            {16,0}, {17,30}, {19,0}, {20,30}
        };

        int[][] times = weekend ? weekendTimes : weekdayTimes;

        boolean found = false;

        for (int i = 0; i < 8; i++) {
            if (weekend && i >= weekendTimes.length) break;
            if (!weekend && i >= weekdayTimes.length) break;

            next.set(Calendar.HOUR_OF_DAY, times[i][0]);
            next.set(Calendar.MINUTE, times[i][1]);
            next.set(Calendar.SECOND, 0);
            next.set(Calendar.MILLISECOND, 0);

            if (next.after(now)) {
                found = true;
                break;
            }
        }

        if (!found) {
            next.add(Calendar.DAY_OF_YEAR, 1);
            next.set(Calendar.HOUR_OF_DAY, weekend ? 10 : 5);
            next.set(Calendar.MINUTE, weekend ? 0 : 15);
            next.set(Calendar.SECOND, 0);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT |
            PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm =
            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarm != null) {
            alarm.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
            );
        }
    }

    public static void cancel(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT |
            PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm =
            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarm != null) {
            alarm.cancel(pendingIntent);
        }
    }
}
