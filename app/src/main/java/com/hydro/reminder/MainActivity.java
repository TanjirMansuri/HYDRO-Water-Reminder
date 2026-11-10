package com.hydro.reminder;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    int water = 0;
    final int target = 2200;

    TextView progress, amount, rideStatus;
    LinearLayout layout;
    Button rideButton;

    SharedPreferences prefs;

    String today() {
        return new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(new Date());
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("HYDRO", MODE_PRIVATE);

        loadToday();

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission("android.permission.POST_NOTIFICATIONS")
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{"android.permission.POST_NOTIFICATIONS"}, 101
            );
        }

        ReminderScheduler.scheduleNext(this);

        showHome();
    }

    void loadToday() {

        String savedDate = prefs.getString("date", "");

        if (!today().equals(savedDate)) {

            water = 0;

            prefs.edit()
                .putInt("water", 0)
                .putString("date", today())
                .apply();

        } else {

            water = prefs.getInt("water", 0);
        }
    }

    void saveWater() {

        prefs.edit()
            .putInt("water", water)
            .putString("date", today())
            .apply();
    }

    void showHome() {

        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(25, 30, 25, 25);
        layout.setBackgroundColor(Color.rgb(235,247,255));

        TextView title = new TextView(this);
        title.setText("💧 HYDRO");
        title.setTextSize(32);
        title.setTextColor(Color.rgb(0,100,180));
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Drink Smart. Stay Hydrated.");
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextSize(15);
        layout.addView(subtitle);

        progress = new TextView(this);
        progress.setTextSize(24);
        progress.setGravity(Gravity.CENTER);
        progress.setPadding(10,35,10,20);
        layout.addView(progress);

        amount = new TextView(this);
        amount.setTextSize(19);
        amount.setGravity(Gravity.CENTER);
        layout.addView(amount);

        addButton("💧 100 ml",100);
        addButton("💧 150 ml",150);
        addButton("💧 250 ml",250);
        addButton("💧 500 ml",500);

        rideStatus = new TextView(this);
        rideStatus.setTextSize(17);
        rideStatus.setGravity(Gravity.CENTER);
        rideStatus.setPadding(10,20,10,5);
        layout.addView(rideStatus);

        rideButton = new Button(this);
        rideButton.setOnClickListener(v -> toggleRide());
        layout.addView(rideButton);

        Button reset = new Button(this);
        reset.setText("Reset Today");
        reset.setOnClickListener(v -> {
            water = 0;
            saveWater();
            update();
        });
        layout.addView(reset);

        setContentView(layout);

        update();
        updateRide();
    }

    void addButton(String name, int ml) {

        Button btn = new Button(this);
        btn.setText(name);

        btn.setOnClickListener(v -> {

            loadToday();

            water += ml;

            saveWater();

            update();
        });

        layout.addView(btn);
    }

    void toggleRide() {

        boolean current = prefs.getBoolean("ride_mode", false);

        prefs.edit()
            .putBoolean("ride_mode", !current)
            .apply();

        if (!current) {
            ReminderScheduler.cancel(this);
        } else {
            ReminderScheduler.scheduleNext(this);
        }

        updateRide();
    }

    void updateRide() {

        boolean ride = prefs.getBoolean("ride_mode", false);

        if (ride) {
            rideStatus.setText("🚗 Ride Mode is ON");
            rideButton.setText("Turn Ride Mode OFF");
        } else {
            rideStatus.setText("💧 Ride Mode is OFF");
            rideButton.setText("Turn Ride Mode ON");
        }
    }

    void update() {

        progress.setText("Today's Goal: " + target + " ml");

        amount.setText(
            "Drank: " + water + " ml\n" +
            "Remaining: " + Math.max(0,target-water) + " ml"
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs != null && progress != null) {
            loadToday();
            update();
            updateRide();
        }
    }
}
