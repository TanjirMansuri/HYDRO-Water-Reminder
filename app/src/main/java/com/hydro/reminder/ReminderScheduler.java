package com.hydro.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

public class ReminderScheduler {

    private static final int REQUEST_CODE = 1001;

    public static void scheduleNext(Context context) {

        SharedPreferences prefs =
            context.getSharedPreferences("HYDRO", Context.MODE_PRIVATE);

        if (prefs.getBoolean("ride_mode", false)) {
            cancel(context);
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();

        int[][] weekdayTimes = {
            {5,15}, {7,45}, {9,15}, {10,45},
            {12,15}, {13,45}, {15,15}, {16,45},
            {19,30}, {21,0}
        };

        int[][] weekendTimes = {
            {10,0}, {11,30}, {13,0}, {14,30},
            {16,0}, {17,30}, {19,0}, {20,30}
        };

        boolean found = false;

        for (int dayOffset = 0; dayOffset < 8 && !found; dayOffset++) {

            Calendar checkDay = (Calendar) now.clone();
            checkDay.add(Calendar.DAY_OF_YEAR, dayOffset);

            int day = checkDay.get(Calendar.DAY_OF_WEEK);

            boolean weekend =
                day == Calendar.SATURDAY ||
                day == Calendar.SUNDAY;

            int[][] times = weekend ? weekendTimes : weekdayTimes;

            for (int[] time : times) {

                next = (Calendar) checkDay.clone();

                next.set(Calendar.HOUR_OF_DAY, time[0]);
                next.set(Calendar.MINUTE, time[1]);
                next.set(Calendar.SECOND, 0);
                next.set(Calendar.MILLISECOND, 0);

                if (next.after(now)) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) return;

        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT |
            PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarm =
            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarm == null) return;

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                alarm.canScheduleExactAlarms()) {

                alarm.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    next.getTimeInMillis(),
                    pendingIntent
                );

            } else {

                alarm.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    next.getTimeInMillis(),
                    pendingIntent
                );
            }

        } catch (SecurityException e) {

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
            REQUEST_CODE,
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
