package com.apps.rdjsmartapps.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmInfoActivity extends AppCompatActivity {

    LinearLayout noteLayout, repeatLayout, ringtoneLayout;
    Button okButton, cancelButton, deleteButton;
    TextView alarmNote, alarmRepeat, ringtoneValue, timeToAlarm ;
    private MediaPlayer mediaPlayer;
    String queryString, id, time, ampm, repeatType, repeatDays, note, ringtone, vibrate_switch;
    Switch vibrateSwitch;
    Vibrator vibe;
    public DBSingleton dbInstance;
    public SQLiteDatabase db;
    public Cursor c;
    public StringBuffer alarmBuffer;
    String mode;
    private TimePicker timePicker;
    int hour, min;
    private Calendar calendar;
    public SimpleDateFormat dateFormat12, dateFormat24;
    public Date alarmDateTime, currentDateTime;
    public String timeArr[];
    private static final String TAG = "AlarmInfoActivity";
    Long timeToAlarmMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_info);

        // Back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initializing db object
        dbInstance = DBSingleton.getInstance(getApplicationContext());
        db = dbInstance.getWritableDatabase();

        // Initializing media player
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.alarm_tone);

        //Initializing view objects
        noteLayout = (LinearLayout) findViewById(R.id.noteItem);
        repeatLayout = (LinearLayout) findViewById(R.id.repeatItem);
        ringtoneLayout = (LinearLayout) findViewById(R.id.ringtoneItem);
        alarmNote = (TextView) findViewById(R.id.alarmNote);
        alarmRepeat = (TextView) findViewById(R.id.repeatValue);
        ringtoneValue = (TextView) findViewById(R.id.ringtoneValue);
        vibrateSwitch = (Switch) findViewById(R.id.vibrateValue);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        timeToAlarm = (TextView) findViewById(R.id.timeLabel);

        // time objects
        dateFormat12 = new SimpleDateFormat("hh:mm a");
        dateFormat24 = new SimpleDateFormat("HH:mm");

        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        hour = timePicker.getCurrentHour();
        min = timePicker.getCurrentMinute();
        calendar = Calendar.getInstance();


        // Check the current mode (Add/Update) and perform actions accordingly
        Bundle inBundle = getIntent().getExtras();
        mode = inBundle.get("mode").toString();
        String fromActivity = inBundle.get("from_activity").toString();
        if(mode.equals("add")){
            // If called by main activity then initialize alarm by default values.
            // If called by other than main activity then assign bundle values to alarm.
            if(fromActivity.equals("MainActivity")) {
                // Setting default alarm values
                repeatType = "Once";
                alarmRepeat.setText(repeatType);
                ringtoneValue.setText("alarm_tone");
                vibrateSwitch.setChecked(true);
                alarmNote.setText("Alarm Label");

                time = "7:00";
                timePicker.setCurrentHour(7);
                timePicker.setCurrentMinute(0);
                setTimeForAlarm(7, 0);

                ampm = "AM";
                repeatDays = "Mon";
                note = "Alarm Label";
                ringtone = "alarm_tone";
                vibrate_switch = "true";

            }
            else{
                time = inBundle.get("time").toString();
                ampm = inBundle.get("ampm").toString();
                repeatType = inBundle.get("repeatType").toString();
                repeatDays = inBundle.get("repeatDays").toString();
                note = inBundle.get("note").toString();
                ringtone = inBundle.get("ringtone").toString();
                vibrate_switch = inBundle.get("vibrate_switch").toString();

                alarmRepeat.setText(repeatType);
                ringtoneValue.setText(ringtone);
                vibrateSwitch.setChecked(Boolean.valueOf(vibrate_switch));
                alarmNote.setText(note);

                // Convert 12hr format to 24hr format and then display time in timepicker
                try {
                    alarmDateTime = dateFormat12.parse(time + " " + ampm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timeTemp;
                timeTemp = dateFormat24.format(alarmDateTime);
                timeArr = timeTemp.split(":");
                hour = Integer.valueOf(timeArr[0]);
                min = Integer.valueOf(timeArr[1]);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(min);

                alarmNote.setText(note);
                setTimeForAlarm(hour, min);

            }

        }

        if(mode.equals("update")){

            // update mode
            id = inBundle.get("id").toString();

            // Fetch Alarm data from database
            queryString = "SELECT * FROM alarm_tbl WHERE id = '"+id+"'";
            c = db.rawQuery(queryString, null);
            if(c.getCount()==0)
            {
                dbInstance.msg(this, "Alarm not found!");
            }
            alarmBuffer = new StringBuffer();

            while(c.moveToNext())
            {
                // get alarm values
                if(fromActivity.equals("MainActivity")){
                    time = c.getString(1);
                    ampm = c.getString(2);
                    repeatType = c.getString(3);
                    repeatDays = c.getString(4);
                    note = c.getString(5);
                    ringtone = c.getString(7);
                    Log.d("AlarmInfoActivity", "ringtone while fetching : " +ringtone);
                    vibrate_switch = c.getString(8);
                }
                else{
                    time = inBundle.get("time").toString();
                    ampm = inBundle.get("ampm").toString();
                    repeatType = inBundle.get("repeatType").toString();
                    repeatDays = inBundle.get("repeatDays").toString();
                    note = inBundle.get("note").toString();
                    ringtone = inBundle.get("ringtone").toString();
                    vibrate_switch = inBundle.get("vibrate_switch").toString();
                }


                // Set alarm values

                // Convert 12hr format to 24hr format and then display time in timepicker
                try {
                    alarmDateTime = dateFormat12.parse(time + " " + ampm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timeTemp;
                timeTemp = dateFormat24.format(alarmDateTime);
                timeArr = timeTemp.split(":");
                hour = Integer.valueOf(timeArr[0]);
                min = Integer.valueOf(timeArr[1]);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(min);

                alarmNote.setText(note);


                // calculate time for alarm and set in timetoalarm textview
                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                timeToAlarmMillis =  setTimeForAlarm(hour, min);

                long toHours = timeToAlarmMillis / hoursInMilli;
                timeToAlarmMillis = timeToAlarmMillis % hoursInMilli;
                long toMinutes = timeToAlarmMillis / minutesInMilli;
                timeToAlarm.setText("Alarm in "+toHours+" hours and "+toMinutes+" minutes");
                // End - calculate time for alarm and set in timetoalarm textview

                if(repeatType.equals("Custom")){
                    alarmRepeat.setText(repeatDays.trim());
                }
                else {
                    alarmRepeat.setText(repeatType);
                }
                ringtoneValue.setText(ringtone);
                vibrateSwitch.setChecked(Boolean.valueOf(vibrate_switch));
            }
        }



        // setting on click listeners for alarm ok, cancel and delete buttons

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hourOfDay, minOfDay;
                // get selected time
                hour = timePicker.getCurrentHour();
                hourOfDay = hour;
                min = timePicker.getCurrentMinute();
                minOfDay = min;

                if (hour == 0) {
                    hour += 12;
                    ampm = "AM";
                } else if (hour == 12) {
                    ampm = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    ampm = "PM";
                } else {
                    ampm = "AM";
                }

                time = String.valueOf(hour);
                time = time + ":" + String.valueOf(min);


                if(repeatType.equals("Custom")){}
                else{
                    repeatDays = "Non";
                }
                note = alarmNote.getText().toString();
                ringtone = ringtoneValue.getText().toString();
                vibrate_switch = String.valueOf(vibrateSwitch.isChecked());

                // insert 1 row
                ContentValues initialValues = new ContentValues();
                initialValues.put("time",time);
                initialValues.put("ampm",ampm);
                initialValues.put("repeat_type",repeatType);
                initialValues.put("repeat_days",repeatDays);
                initialValues.put("note",note);
                initialValues.put("alarm_switch","true");
                initialValues.put("ringtone",ringtone);
                Log.d("AlarmInfoActivity", "ringtone while saving : " +ringtone);
                initialValues.put("vibrate_switch",vibrate_switch);

                if(mode.equals("add")) {
                    // Add new alarm
                    initialValues.put("request_code","-1");
                    Long result = db.insert("alarm_tbl", null, initialValues);
                    id = result.toString();
                    dbInstance.msg(getApplicationContext(), "New alarm is set!");
                }
                else{
                    // Update alarm
                    int result =  db.update("alarm_tbl", initialValues, "id=" + id, null);
                    dbInstance.msg(getApplicationContext(), "Alarm updated!");
                }

                // Set alarm
                SetAlarm setAlarm = new SetAlarm();
                setAlarm.alarmSetter(getApplicationContext(), id);

                Intent mainIntent = new Intent(AlarmInfoActivity.this, MainActivity.class);
                startActivity(mainIntent);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on cancel go back to alarm list page
                Intent mainIntent = new Intent(AlarmInfoActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete alarm and go back to alarm list page
                if(mode.equals("update")) {
                    db.execSQL("DELETE FROM alarm_tbl WHERE id = '" + id + "'");
                }
                Intent mainIntent = new Intent(AlarmInfoActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


        // Setting on click listeners for alarm note item

        noteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curNote = alarmNote.getText().toString();

                Intent noteIntent = new Intent(AlarmInfoActivity.this, NoteActivity.class);
                noteIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    noteIntent.putExtra("id", id);
                }
                noteIntent.putExtra("time",time);
                noteIntent.putExtra("ampm",ampm);
                noteIntent.putExtra("repeatType",repeatType);
                noteIntent.putExtra("repeatDays",repeatDays);
                noteIntent.putExtra("note",note);
                noteIntent.putExtra("ringtone",ringtone);
                noteIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(noteIntent);
            }
        });


        // Setting on click listeners for alarm repeat item

        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent repeatIntent = new Intent(AlarmInfoActivity.this, RepeatActivity.class);
                repeatIntent.putExtra("mode",mode);
                if(mode.equals("update")){
                    repeatIntent.putExtra("id",id);
                }
                repeatIntent.putExtra("time",time);
                repeatIntent.putExtra("ampm",ampm);
                repeatIntent.putExtra("repeatType",repeatType);
                repeatIntent.putExtra("repeatDays",repeatDays);
                repeatIntent.putExtra("note",note);
                repeatIntent.putExtra("ringtone",ringtone);
                repeatIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(repeatIntent);
            }
        });


        // Setting on click listeners for alarm ringtone item

        ringtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ringtoneIntent = new Intent(AlarmInfoActivity.this, RingtoneActivity.class);
                ringtoneIntent.putExtra("mode",mode);
                if(mode.equals("update")){
                    ringtoneIntent.putExtra("id",id);
                }
                ringtoneIntent.putExtra("time",time);
                ringtoneIntent.putExtra("ampm",ampm);
                ringtoneIntent.putExtra("repeatType",repeatType);
                ringtoneIntent.putExtra("repeatDays",repeatDays);
                ringtoneIntent.putExtra("note",note);
                ringtoneIntent.putExtra("ringtone",ringtone);
                ringtoneIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(ringtoneIntent);
            }
        });


        // Setting onclick listener for vibrate switch

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    vibe.vibrate(400);
                }
                else{
                    vibe.cancel();
                }

            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
                // calculate time for alarm
                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                Long timeToAlarmMillis = setTimeForAlarm(hour, min);

                long toHours = timeToAlarmMillis / hoursInMilli;
                timeToAlarmMillis = timeToAlarmMillis % hoursInMilli;
                long toMinutes = timeToAlarmMillis / minutesInMilli;
                timeToAlarm.setText("Alarm in "+toHours+" hours and "+toMinutes+" minutes");

            }
        });


    } // End of oncreate


    Long setTimeForAlarm(int hour, int min){
        // Method takes hour and min as input
        // Calculates time for alarm
        // Sets time for alarm string

        dateFormat12 = new SimpleDateFormat("hh:mm a");
        dateFormat24 = new SimpleDateFormat("HH:mm");

        if (hour == 0) {
            hour += 12;
            ampm = "AM";
        } else if (hour == 12) {
            ampm = "PM";
        } else if (hour > 12) {
            hour -= 12;
            ampm = "PM";
        } else {
            ampm = "AM";
        }

        // Convert 12hr format to 24hr format
        try {
            alarmDateTime = dateFormat12.parse(hour+ ":" + min + " " + ampm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        time = dateFormat24.format(alarmDateTime);
        timeArr = time.split(":");
        hour = Integer.valueOf(timeArr[0]);
        min = Integer.valueOf(timeArr[1]);

        // calculate time for alarm
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        Calendar currentDate = Calendar.getInstance();
        int curHour  = currentDate.get(Calendar.HOUR_OF_DAY);
        int curMin = currentDate.get(Calendar.MINUTE);
        String curAmpm="";

        if (curHour == 0) {
            curHour += 12;
            curAmpm = "AM";
        } else if (curHour == 12) {
            curAmpm = "PM";
        } else if (curHour > 12) {
            curHour -= 12;
            curAmpm = "PM";
        } else {
            curAmpm = "AM";
        }

        try {
            currentDateTime = dateFormat12.parse(curHour+ ":" + curMin + " " + curAmpm);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = alarmDateTime.getTime() - currentDateTime.getTime();
        long toHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;
        long toMinutes = difference / minutesInMilli;

        if(toHours <0 || toMinutes < 0){
            long hoursInMilliSec12 = 1000 * 60 * 60 * 24;
            long tempMilliSec = toHours * 60 * 60 * 1000 + toMinutes * 60 * 1000;
            difference = hoursInMilliSec12 + tempMilliSec; // here tempMilliSec is negative
            toHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;
            toMinutes = difference / minutesInMilli;
        }

        timeToAlarmMillis = toHours * 60 * 60 * 1000 + toMinutes * 60 * 1000;
        return timeToAlarmMillis;

    } //  End of timeForAlarm method

    String getTimeForAlarm(String time, String ampm){
        // Input: time in 12 hr format
        //output: String having time to alarm

        // convert 12 hr format to 24 hr format
        SetAlarm setAlarm = new SetAlarm();
        Date d = setAlarm.getDateIn24HrFormat(time, ampm);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        // calculate time for alarm
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        timeToAlarmMillis = setTimeForAlarm(hour, min);

        long toHours = timeToAlarmMillis / hoursInMilli;
        timeToAlarmMillis = timeToAlarmMillis % hoursInMilli;
        long toMinutes = timeToAlarmMillis / minutesInMilli;
        String result = "Alarm in "+toHours+" Hr and "+toMinutes+" Min";

        return result;
    }

    void cancelAlarm(Context context, int alarm_request_code){
        // cancel specific alarm
        Log.d("AlarmInfoActivity", "cancel alarm: " + alarm_request_code);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcaster.class);    //OneShotAlarm is the broadcast receiver you use for alarm
        PendingIntent sender = PendingIntent.getBroadcast(context, alarm_request_code, intent, 0);
        am.cancel(sender);
    }

} // end of class
