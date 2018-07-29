package com.apps.rdjsmartapps.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Harshal on 5/19/2018.
 */

public class AlarmBroadcaster extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmBroadcaster", "OnReceive starts.");
        String alarm_id = "";
        // Power manager keeps track on active alarms. Without powermanager, alarms scheduled after one hour does not get triggered.
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmBroadcaster");
        wl.acquire(); // Acquire wake lock

        alarm_id = intent.getStringExtra("alarm_id");
        Log.d("AlarmBroadcaster", "alarm id:"+alarm_id);
        //alarm_id = "2";

        createNotification(context, alarm_id);

        wl.release();// Release wake lock
        Log.d("AlarmBroadcaster", "OnReceive end.");
    }

    public void createNotification(Context context, String alarm_id) {
        NotificationTrigger.starNotification(context, alarm_id);
    }

}
