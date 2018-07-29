package com.apps.rdjsmartapps.alarm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RepeatActivity extends AppCompatActivity {

    Button repeatOkButton, repeatCancelButton;
    RadioGroup repeatGroup;
    RadioButton customRepeat;
    String mode, id, queryString, note, time, ampm, repeatType, repeatDays, ringtone, vibrate_switch;
    Cursor c;
    DBSingleton dbInstance;
    SQLiteDatabase db;
    StringBuffer alarmBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        // Setting activity dialog window size
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        // Initializing view objects
        repeatGroup = (RadioGroup) findViewById(R.id.repeatOptions);
        customRepeat = (RadioButton) findViewById(R.id.custom);
        repeatOkButton = (Button) findViewById(R.id.okButton);
        repeatCancelButton = (Button) findViewById(R.id.cancelButton);


        // Initializing db object
        dbInstance = DBSingleton.getInstance(getApplicationContext());
        db = dbInstance.getWritableDatabase();

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

            switch (repeatType) {
                case "Everyday":
                    repeatGroup.check(repeatGroup.getChildAt(0).getId());
                    break;
                case "Once":
                    repeatGroup.check(repeatGroup.getChildAt(1).getId());
                    break;
                case "Monday to Friday":
                    repeatGroup.check(repeatGroup.getChildAt(2).getId());
                    break;
                case "Custom":
                    repeatGroup.check(repeatGroup.getChildAt(3).getId());
                    break;
            }

        }


        // Setting on click listeners

        customRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call days activity
                Intent daysIntent = new Intent(RepeatActivity.this, DaysActivity.class);
                daysIntent.putExtra("mode",mode);
                if(mode.equals("update")){
                    daysIntent.putExtra("id",id);
                }

                daysIntent.putExtra("time",time);
                daysIntent.putExtra("ampm",ampm);
                daysIntent.putExtra("repeatType",repeatType);
                daysIntent.putExtra("repeatDays",repeatDays);
                daysIntent.putExtra("note",note);
                daysIntent.putExtra("ringtone",ringtone);
                daysIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(daysIntent);
            }
        });

        repeatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.everyday:
                        repeatType = "Everyday";
                        break;
                    case R.id.once:
                        repeatType = "Once";
                        break;
                    case R.id.mtof:
                        repeatType = "Monday to Friday";
                        break;
                    case R.id.custom:
                        // call days activity
                        Intent daysIntent = new Intent(RepeatActivity.this, DaysActivity.class);
                        daysIntent.putExtra("mode",mode);
                        if(mode.equals("update")){
                            daysIntent.putExtra("id",id);
                        }

                        daysIntent.putExtra("time",time);
                        daysIntent.putExtra("ampm",ampm);
                        daysIntent.putExtra("repeatType",repeatType);
                        daysIntent.putExtra("repeatDays",repeatDays);
                        daysIntent.putExtra("note",note);
                        daysIntent.putExtra("ringtone",ringtone);
                        daysIntent.putExtra("vibrate_switch",vibrate_switch);

                        startActivity(daysIntent);
                        break;
                }
            }
        });



        repeatOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = repeatGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                String repeatTypeTemp = radioButton.getText().toString();
                if(repeatTypeTemp.equals("Only selected days")) {}
                else{
                    repeatType = radioButton.getText().toString();
                }

                //Go back to AlarmInfoActivity with selected ringtone
                Intent AlarmInfoIntent = new Intent(RepeatActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","RepeatActivity");
                AlarmInfoIntent.putExtra("result","ok");
                AlarmInfoIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    AlarmInfoIntent.putExtra("id", id);
                }
                AlarmInfoIntent.putExtra("note",note);
                AlarmInfoIntent.putExtra("time",time);
                AlarmInfoIntent.putExtra("ampm",ampm);
                AlarmInfoIntent.putExtra("repeatType",repeatType);
                AlarmInfoIntent.putExtra("repeatDays",repeatDays);
                AlarmInfoIntent.putExtra("ringtone",ringtone);
                AlarmInfoIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(AlarmInfoIntent);

            }
        });

        repeatCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go back to AlarmInfoActivity with selected ringtone
                Intent AlarmInfoIntent = new Intent(RepeatActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","RepeatActivity");
                AlarmInfoIntent.putExtra("result","ok");
                AlarmInfoIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    AlarmInfoIntent.putExtra("id", id);
                }
                AlarmInfoIntent.putExtra("note",note);
                AlarmInfoIntent.putExtra("time",time);
                AlarmInfoIntent.putExtra("ampm",ampm);
                AlarmInfoIntent.putExtra("repeatType",repeatType);
                AlarmInfoIntent.putExtra("repeatDays",repeatDays);
                AlarmInfoIntent.putExtra("ringtone",ringtone);
                AlarmInfoIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(AlarmInfoIntent);
            }
        });



    }// End of onCreate

}
