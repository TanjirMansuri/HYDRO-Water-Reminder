package com.hydro.reminder;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {

    int water = 0;
    int target = 2200;

    TextView progress, amount;
    LinearLayout layout;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(
                new String[]{"android.permission.POST_NOTIFICATIONS"}, 101
            );
        }

        showHome();
    }

    void showHome() {
        layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setPadding(25,30,25,25);
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

        Button reset = new Button(this);
        reset.setText("Reset Today");
        reset.setOnClickListener(v -> {
            water = 0;
            update();
        });
        layout.addView(reset);

        setContentView(layout);
        update();
    }

    void addButton(String name,int ml) {
        Button btn = new Button(this);
        btn.setText(name);
        btn.setOnClickListener(v -> {
            water += ml;
            update();
        });
        layout.addView(btn);
    }

    void update() {
        progress.setText("Today's Goal: "+target+" ml");
        amount.setText("Drank: "+water+" ml\nRemaining: "+Math.max(0,target-water)+" ml");
    }
}
