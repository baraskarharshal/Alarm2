package com.apps.rdjsmartapps.alarm;

import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Harshal on 5/19/2018.
 */

public class NotificationTrigger extends IntentService {

    private static final String ALARM = "com.apps.rdjsmartapps.alarm.ALARM";
    public DBSingleton dbInstance;
    public SQLiteDatabase db;
    public MediaPlayer mediaPlayer;


    public NotificationTrigger() {
        super("NotificationTrigger");
    }

    public static void starNotification(Context context, String alarm_id) {
        Intent intent = new Intent(context, NotificationTrigger.class);
        intent.setAction(ALARM);
        intent.putExtra("alarm_id", alarm_id);
        Log.d("NotificationTrigger", "Alarm id:"+alarm_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String id = intent.getStringExtra("alarm_id");

            if (ALARM.equals(action)) {
                handleAlarm(id);
            }

        }
    }

    private void handleAlarm(String alarm_id) {
        Log.d("AlarmBroadcaster", "handleAlarm starts.");
        String time = "", ringtone = "", path = "", repeatType="", repeatDays="", today="", ampm="", note="";
        String timeTemp="", hour="", min="", vibrate="";
        Date alarmDateTime = new Date();
        Date currentDateTime = new Date();
        String[] days;
        int j = 0, today_int = 1, hourInt=0, minInt=0;
        SimpleDateFormat dateFormat12, dateFormat24;
        String[] timeArr;
        Boolean onBootFlag = false;
        // time objects
        dateFormat12 = new SimpleDateFormat("hh:mm a");
        dateFormat24 = new SimpleDateFormat("HH:mm");

        // Initializing db object
        dbInstance = DBSingleton.getInstance(getApplicationContext());
        db = dbInstance.getReadableDatabase();
        Cursor c;

        // Get day of the week in number and string
        Calendar cal = Calendar.getInstance();
        today_int = cal.get(Calendar.DAY_OF_WEEK);

        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
        today = simpleDateformat.format(now);


        // Fetch Alarm data from database
        //String queryString = "SELECT * FROM alarm_tbl WHERE alarm_switch = 'true'";
        String queryString = "SELECT * FROM alarm_tbl WHERE id = '"+alarm_id+"'";
        c = db.rawQuery(queryString, null);
        if(c.getCount()==0)
        {
            //dbInstance.msg(this, "Alarm not found!");
            Log.d("NotificationTrigger", "Alarm not found!: " +alarm_id);

            // check for active alarms
            queryString = "SELECT * FROM alarm_tbl WHERE alarm_switch = 'true'";
            c = db.rawQuery(queryString, null);
            onBootFlag = true;
            if(c.getCount()==0){
                Log.d("NotificationTrigger", "OnBootAlarm not found");
                return;
            }
        }


        StringBuffer alarmBuffer = new StringBuffer();

        if(onBootFlag == true) {
            Log.d("AlarmBroadcaster", "Boot flag is true.");
            // For on boot receive alarm
            int alHour, alMin, CurHour, CurMin;
            SetAlarm setAlarm = new SetAlarm();

            Calendar cal_now = Calendar.getInstance();// getting current hour and min values.
            CurHour = cal_now.get(Calendar.HOUR_OF_DAY);
            
            CurMin = cal_now.get(Calendar.MINUTE);
            Log.d("AlarmBroadcaster", "current hour and min:"+ CurHour +":"+CurMin);

            while (c.moveToNext()) {
                Log.d("AlarmBroadcaster", "while loop starts.");
                time = c.getString(1);
                ampm = c.getString(2);

                // verify valid alarm by matching time
                Date d = setAlarm.getDateIn24HrFormat(time, ampm);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(d);
                alHour = cal1.get(Calendar.HOUR_OF_DAY);
                alMin = cal1.get(Calendar.MINUTE);
                Log.d("AlarmBroadcaster", "alarm hour and min:"+ alHour +":"+alMin);

                if(alHour == CurHour && alMin == CurMin) {

                    alarm_id = c.getString(0);
                    Log.d("AlarmBroadcaster", "alarm found. alarm is id:"+alarm_id);
                    note = c.getString(5);
                    Log.d("NotificationTrigger", "Note: " + note);
                    ringtone = c.getString(7);
                    Log.d("NotificationTrigger", "ringtone while notify : " + ringtone);
                    repeatType = c.getString(3);
                    repeatDays = c.getString(4);
                    vibrate = c.getString(8);
                    break;
                }
                else{
                    Log.d("AlarmBroadcaster", "While continue 1");
                    continue;
                }
            }
        }
        else{
            Log.d("AlarmBroadcaster", "This is normal alarm.");
            // For normal alarm
            while(c.moveToNext()) {
                Log.d("AlarmBroadcaster", "while loop starts.");
                time = c.getString(1);
                ampm = c.getString(2);
                note = c.getString(5);
                Log.d("NotificationTrigger", "Note: " +note);
                ringtone = c.getString(7);
                Log.d("NotificationTrigger", "ringtone while notify : " +ringtone);
                repeatType = c.getString(3);
                repeatDays = c.getString(4);
                vibrate = c.getString(8);
            }

        }

        // validate the day for custom and mon to friday repeat type. Return if day is invalid
        if(repeatType.equals("Monday to Friday")){
            if(today_int==1 || today_int==7){
                // skip alarm if its saturday or sunday.
                return;
            }
        }

        if(repeatType.equals("Custom")){
            days = repeatDays.split(" ");
            boolean flag = false;
            for(j = 0; j < days.length; j++){
                if(days[j].equals(today)) {
                    flag = true;
                }
            }

            if(flag == false){
                // skip alarm if day is invalid for custom repeat.
                return;
            }

        }

        // skip alarm if alarm time is less than current time
        // Convert 12hr format to 24hr format
        try {
            alarmDateTime = dateFormat12.parse(time + " " + ampm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeTemp = dateFormat24.format(alarmDateTime);
        timeArr = timeTemp.split(":");
        hourInt = Integer.valueOf(timeArr[0]);
        minInt = Integer.valueOf(timeArr[1]);

        int curHour  = cal.get(Calendar.HOUR_OF_DAY);
        int curMin = cal.get(Calendar.MINUTE);
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

        if(alarmDateTime.getTime() < currentDateTime.getTime()){
            Log.d("NotificationTrigger", "Alarm set Time: " +alarmDateTime.getTime());
            Log.d("NotificationTrigger", "Alarm current Time: " +currentDateTime.getTime());
        }

        // Play ringtone from raw folder or from sd card.
        queryString = "SELECT * FROM alarm_song WHERE song_name = '"+ringtone+"'";
        c = db.rawQuery(queryString, null);
        if(c.getCount()==0) {
            Log.d("AlarmBroadcaster", "Playing song from raw folder.");
            Log.d("NotificationTrigger", "ringtone : " +ringtone);
            int currentSongId = getResources().getIdentifier(ringtone, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),currentSongId );
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        while(c.moveToNext())
        {
            path = c.getString(1);
            break;
        }

        // This final variable will be used in below thread.
        final String finalPath = path;

        // create a handler to post messages to the main thread
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                RingtoneActivity ringtoneActivity = new RingtoneActivity();
                if(finalPath != "") {
                    Log.d("AlarmBroadcaster", "new handler starts.");
                    Log.d("NotificationTrigger", "Playing song from sd card");
                    ringtoneActivity.playSongFromSDCard(finalPath);
                }
            }
        });



        // Show alarm notification

        // set notification channel id for orio version and after that.
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("AlarmBroadcaster", "Notification starts.");
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent mainIntent = new Intent(NotificationTrigger.this, MainActivity.class);

        // Create pending intent
        PendingIntent notificationIntent = PendingIntent.getActivity(this, 0, mainIntent , 0);

        // set notification information
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.notification_icon1)
                .setColor(Color.parseColor("#3F51B5"))
                //.addAction(R.drawable.ic_stat_name, "Snooz", notificationIntent)
                //.addAction(R.drawable.ic_stat_name, "Dismiss", notificationIntent)
                .setContentTitle("Alarm")
                .setTicker(getApplicationContext().getString(R.string.app_name))
                .setContentText(note);

        notificationBuilder.setContentIntent(notificationIntent);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        notificationBuilder.setAutoCancel(true);

        // display notification
        notificationManager.notify(0, notificationBuilder.build());

        // disable alarm if repeat type is once

        if(repeatType.equals("Once")){
            Log.d("NotificationTrigger", "alarm update starts.");
            // Update request code for alarm
            ContentValues updValues = new ContentValues();
            updValues.put("alarm_switch","false");
            int result =  db.update("alarm_tbl", updValues, "id=" + alarm_id, null);
        }

        // transfer to alar
        // m activity right after displaying notification
        Log.d("AlarmBroadcaster", "alarmIntent starts.");
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmActivity.class);
        alarmIntent.putExtra("alarm_id",alarm_id);
        alarmIntent.putExtra("time",time);
        alarmIntent.putExtra("ampm",ampm);
        alarmIntent.putExtra("note",note);
        alarmIntent.putExtra("vibrate",vibrate);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(alarmIntent);

    }

}
