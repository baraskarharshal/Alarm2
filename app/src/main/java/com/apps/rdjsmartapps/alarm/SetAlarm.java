package com.apps.rdjsmartapps.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Harshal on 5/19/2018.
 */

public class SetAlarm {

    DBSingleton dbInstance;
    SQLiteDatabase db;

    public void alarmSetter(Context context, String alarm_id) {

        // Initializing db object
        dbInstance = DBSingleton.getInstance(context);
        db = dbInstance.getReadableDatabase();
        Cursor c;
        int request_code_int = -1, hour=0, min=0;
        Long timeToAlarmMillis=0L;
        String time="", ampm="", repeat_type="";


        // Fetch all alarms time and check for first alarm
        String queryString = "SELECT * FROM alarm_tbl WHERE id = '"+alarm_id+"'";
        c = db.rawQuery(queryString, null);
        if(c.getCount()==0)
        {
            dbInstance.msg(context, "Alarm not found!");
            return;
        }
        StringBuffer alarmBuffer = new StringBuffer();

        while(c.moveToNext())
        {
            time = c.getString(1);
            ampm = c.getString(2);
            repeat_type = c.getString(3);
            request_code_int = Integer.parseInt(c.getString(9));

        }

        // get time for alarm in milliseconds
        // Convert date time to 24 hour format
        Date d = getDateIn24HrFormat(time, ampm);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance(); // Alarm time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        Calendar calendar2 = Calendar.getInstance(); // Current time

        Long current = calendar2.getTimeInMillis();
        Long alarmTime = calendar.getTimeInMillis();
        timeToAlarmMillis = alarmTime - current; // difference is the exact milliseconds to alarm

        if(timeToAlarmMillis < 0){
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(tomorrow.DAY_OF_YEAR, 1);
            tomorrow.set(tomorrow.HOUR_OF_DAY, hour);
            tomorrow.set(tomorrow.MINUTE, min);
            tomorrow.set(tomorrow.SECOND, 0);

            alarmTime = tomorrow.getTimeInMillis();
            timeToAlarmMillis = alarmTime - current;
        }

        Log.d("SetAlarm", "Alarm Time millis: " +timeToAlarmMillis);

        // Generate unique id for new alarm. If alarm is in update mode then override it by existing request code.
        String request_code = "";
        if(request_code_int != -1) {
            // If request code is positive that means alarm is already scheduled. So cancel it first.
            AlarmInfoActivity alarmInfoActivity = new AlarmInfoActivity();
            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmInfoActivity.cancelAlarm(context, request_code_int); // Cancel this alarm if scheduled already.
        }
        else{
            // Generate new request code
            Random rand = new Random();
            request_code_int = rand.nextInt(9000000) + 1000000;
        }
        request_code = Integer.toString(request_code_int);
        Log.d("SetAlarm", "request code: " + request_code);
        Log.d("SetAlarm", "alarm id: " + alarm_id);

        // Update alarm with new request code
        ContentValues updateValues = new ContentValues();
        updateValues.put("request_code",request_code);
        int result =  db.update("alarm_tbl", updateValues, "id=" + alarm_id, null);

        // Schedule alarm using Alarm Manager

        final Intent intent = new Intent(context, AlarmBroadcaster.class);
        intent.putExtra("alarm_id", alarm_id);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                request_code_int,
                intent,
                0
        );
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(repeat_type.equals("Once")) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToAlarmMillis, pIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToAlarmMillis, pIntent);
            }
        }
        else{
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToAlarmMillis, AlarmManager.INTERVAL_DAY,  pIntent);
        }

        //Toast.makeText(context, "Alarm time: "+  timeToAlarmMillis, Toast.LENGTH_SHORT).show();



    } // End of method



    public Date getDateIn24HrFormat(String time, String ampm){
        //Input 12 hour format date
        //Output 24 hour format date

        Date date24 = new Date();
        SimpleDateFormat dateFormat12 = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm");

        try {
            date24 = dateFormat12.parse(time + " " + ampm);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date24;

    } // End of method


}// End of class
