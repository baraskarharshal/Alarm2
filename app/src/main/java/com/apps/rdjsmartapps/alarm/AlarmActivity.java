package com.apps.rdjsmartapps.alarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class AlarmActivity extends AppCompatActivity {

    TextView note, time;
    Button stopButton;
    String alarm_note, alarm_time, ampm, vibrate, hour, min, alarm_id, al_time;
    Vibrator vibe;
    String[] timeArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // display full screen activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm);

        note = (TextView) findViewById(R.id.note);
        time = (TextView) findViewById(R.id.time);
        stopButton = (Button) findViewById(R.id.stopButton);

        // Fetch bundle variables values
        Bundle inBundle = getIntent().getExtras();
        alarm_id =inBundle.get("alarm_id").toString();
        alarm_note = inBundle.get("note").toString();
        alarm_time = inBundle.get("time").toString();
        al_time = alarm_time;
        ampm = inBundle.get("ampm").toString();
        vibrate = inBundle.get("vibrate").toString();

        // format time in 00:00 format
        timeArr = alarm_time.split(":");
        hour = timeArr[0];
        min = timeArr[1];
        if(hour.length() == 1){
            hour = '0' + hour;
        }
        if(min.length() == 1){
            min = '0' + min;
        }
        alarm_time = hour + ":" + min;

        note.setText(alarm_note);
        time.setText(alarm_time + " " + ampm);

        if(vibrate.equals("true")){
            vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] mVibratePattern = new long[]{0, 400, 200, 400};
            // -1 : Do not repeat this pattern
            // pass 0 if you want to repeat this pattern from 0th index
            vibe.vibrate(mVibratePattern, 2);
        }


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vibe != null) {
                    vibe.cancel();
                }
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
}
