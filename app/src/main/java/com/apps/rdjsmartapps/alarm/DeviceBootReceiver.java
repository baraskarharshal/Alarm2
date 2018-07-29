package com.apps.rdjsmartapps.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Harshal on 5/19/2018.
 */

public class DeviceBootReceiver extends BroadcastReceiver {

    DBSingleton dbInstance;
    SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Log.d("DeviceBootReceiver", "Inside boot completed if.");

                // On Boot complete, fetch all active alarms from database and schedule.
                // Initializing db object
                dbInstance = DBSingleton.getInstance(context);
                db = dbInstance.getReadableDatabase();
                Cursor c;
                String alarm_id="";
                SetAlarm setAlarm = new SetAlarm();

                // Fetch all alarms time and check for first alarm
                String queryString = "SELECT * FROM alarm_tbl WHERE alarm_switch = 'true'";
                c = db.rawQuery(queryString, null);
                if(c.getCount()==0)
                {
                    Log.d("DeviceBootReceiver", "alarm not found.");
                    dbInstance.msg(context, "Alarm not found!");
                    return;
                }
                StringBuffer alarmBuffer = new StringBuffer();

                while(c.moveToNext())
                {
                    Log.d("DeviceBootReceiver", "inside while loop.");
                    // Call set alarm method for each active alarm.
                    alarm_id = c.getString(0);
                    Log.d("DeviceBootReceiver", "setting alarm no:"+alarm_id);
                    setAlarm.alarmSetter(context, alarm_id);

                }
                Log.d("DeviceBootReceiver", "after while loop.");

            }
        }
    }


}
