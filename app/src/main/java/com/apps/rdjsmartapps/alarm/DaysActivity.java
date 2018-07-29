package com.apps.rdjsmartapps.alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class DaysActivity extends AppCompatActivity {

    Button daysOkButton, daysCancelButton;
    CheckBox mon, tue, wed, thu, fri, sat, sun;
    String mode, id, note, time, ampm, repeatType, repeatDays, ringtone, vibrate_switch;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        // Setting activity dialog window size
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        // Fetch bundle variables values
        Bundle inBundle = getIntent().getExtras();
        mode = inBundle.get("mode").toString();
        time = inBundle.get("time").toString();
        ampm = inBundle.get("ampm").toString();
        repeatType = inBundle.get("repeatType").toString();
        repeatDays = inBundle.get("repeatDays").toString();
        note = inBundle.get("note").toString();
        ringtone = inBundle.get("ringtone").toString();
        vibrate_switch = inBundle.get("vibrate_switch").toString();

        if(mode.equals("update")) {
            id = inBundle.get("id").toString();

            repeatDays = repeatDays.trim();
            Log.d("AlarmInfoActivity","day:"+repeatDays, null);
            String[] days = repeatDays.split(" ");
            int j;
            String checkBox;
            CheckBox checkDay;
            for(j = 0; j < days.length; j++){
                checkBox = days[j];
                Log.d("AlarmInfoActivity","day:"+checkBox, null);
                checkDay = (CheckBox) findViewById(getResources().getIdentifier(checkBox, "id", getPackageName()));
                if(checkDay != null) {
                    checkDay.setChecked(true);
                }
            }
        }


        // Initializing view objects
        daysOkButton = (Button) findViewById(R.id.okButton);
        daysCancelButton = (Button) findViewById(R.id.cancelButton);

        // Setting onClickListeners

        daysOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String days ="";
                mon = (CheckBox) findViewById(R.id.Mon);
                tue = (CheckBox) findViewById(R.id.Tue);
                wed = (CheckBox) findViewById(R.id.Wed);
                thu = (CheckBox) findViewById(R.id.Thu);
                fri = (CheckBox) findViewById(R.id.Fri);
                sat = (CheckBox) findViewById(R.id.Sat);
                sun = (CheckBox) findViewById(R.id.Sun);

                if(mon.isChecked()){
                    days = "Mon";
                }
                if(tue.isChecked()){
                    days = days + " " + "Tue";
                }
                if(wed.isChecked()){
                    days = days + " " + "Wed";
                }
                if(thu.isChecked()){
                    days = days + " " + "Thu";
                }
                if(fri.isChecked()){
                    days = days + " " + "Fri";
                }
                if(sat.isChecked()){
                    days = days + " " + "Sat";
                }
                if(sun.isChecked()){
                    days = days + " " + "Sun";
                }

                //alarmRepeat.setText(days.trim());
                repeatType = "Custom";
                repeatDays = days.trim();

                //Go back to RepeatActivity with selected ringtone
                Intent RepeatIntent = new Intent(DaysActivity.this, RepeatActivity.class);
                RepeatIntent.putExtra("from_activity","DaysActivity");
                RepeatIntent.putExtra("result","ok");
                RepeatIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    RepeatIntent.putExtra("id", id);
                }
                RepeatIntent.putExtra("note",note);
                RepeatIntent.putExtra("time",time);
                RepeatIntent.putExtra("ampm",ampm);
                RepeatIntent.putExtra("repeatType",repeatType);
                RepeatIntent.putExtra("repeatDays",repeatDays);
                RepeatIntent.putExtra("ringtone",ringtone);
                RepeatIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(RepeatIntent);
            }
        });

        daysCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go back to RepeatActivity with selected ringtone
                Intent RepeatIntent = new Intent(DaysActivity.this, RepeatActivity.class);
                RepeatIntent.putExtra("from_activity","DaysActivity");
                RepeatIntent.putExtra("result","cancel");
                RepeatIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    RepeatIntent.putExtra("id", id);
                }
                RepeatIntent.putExtra("note",note);
                RepeatIntent.putExtra("time",time);
                RepeatIntent.putExtra("ampm",ampm);
                RepeatIntent.putExtra("repeatType",repeatType);
                RepeatIntent.putExtra("repeatDays",repeatDays);
                RepeatIntent.putExtra("ringtone",ringtone);
                RepeatIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(RepeatIntent);

            }
        });


    }// End on onCreate
}
