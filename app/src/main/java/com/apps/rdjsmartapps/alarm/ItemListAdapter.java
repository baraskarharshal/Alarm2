package com.apps.rdjsmartapps.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Created by Harshal on 4/7/2018.
 */

public class ItemListAdapter extends BaseAdapter {

    private Context alarmContext;
    private List<Item> alarmItemList;

    DBSingleton dbInstance;
    SQLiteDatabase db;

    public ItemListAdapter(Context alarmContext, List<Item> alarmItemList) {
        this.alarmContext = alarmContext;
        this.alarmItemList = alarmItemList;

        // Initializing db object
        dbInstance = DBSingleton.getInstance(alarmContext);
        db = dbInstance.getReadableDatabase();
        Cursor c;
    }


    @Override
    public int getCount() {
        return alarmItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //final String time="", ampm="", tmToAlarm="";

        View itemView = View.inflate(alarmContext, R.layout.list_activity, null);
        final TextView alarmTime = (TextView) itemView.findViewById(R.id.alarmTime);
        final TextView alarmAmPm = (TextView) itemView.findViewById(R.id.ampm);
        final TextView repeatType = (TextView) itemView.findViewById(R.id.alarmRecurrence);
        final TextView alarmNote = (TextView) itemView.findViewById(R.id.alarmNote);
        Switch alarmState = (Switch) itemView.findViewById(R.id.state);
        final TextView timeToAlarm = (TextView) itemView.findViewById(R.id.timeForAlarm);

        // set values
        final String alarm_id = Integer.toString(alarmItemList.get(position).getId());

        alarmTime.setText(alarmItemList.get(position).getTime());
        alarmAmPm.setText(alarmItemList.get(position).getAmpm());
        repeatType.setText(alarmItemList.get(position).getRepeatType());
        String tempNote = alarmItemList.get(position).getNote();
        if(tempNote.length() > 21) {
            tempNote = tempNote.substring(0, 21) + "...";
        }
        alarmNote.setText(tempNote);
        timeToAlarm.setText("");
        alarmState.setChecked(Boolean.valueOf(alarmItemList.get(position).getSwitchState()));

        // Give black color for active alarms
        if(alarmState.isChecked()){
            alarmTime.setTextColor(Color.parseColor("#000000"));
            // get time to alarm
            final String time = alarmItemList.get(position).getTime();
            final String ampm = alarmItemList.get(position).getAmpm();
            AlarmInfoActivity alarmInfoActivity = new AlarmInfoActivity();
            final String tmToAlarm = alarmInfoActivity.getTimeForAlarm(time, ampm);
            timeToAlarm.setText(tmToAlarm);

            alarmAmPm.setTextColor(Color.parseColor("#000000"));
            alarmNote.setTextColor(Color.parseColor("#000000"));
            repeatType.setTextColor(Color.parseColor("#000000"));
        }

        alarmState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                // fetch alarm data
                String queryString = "SELECT * FROM alarm_tbl WHERE id = '"+alarm_id+"'";
                Cursor c = db.rawQuery(queryString, null);
                int alarm_request_code = 0;

                if(c.getCount()==0)
                {
                    // dbInstance.msg(alarmContext, "Alarm not found!");
                }
                StringBuffer alarmBuffer = new StringBuffer();

                while(c.moveToNext()) {
                    alarm_request_code = Integer.parseInt(c.getString(9));
                }

                if(isChecked){
                    alarmTime.setTextColor(Color.parseColor("#000000"));
                    alarmAmPm.setTextColor(Color.parseColor("#000000"));
                    alarmNote.setTextColor(Color.parseColor("#000000"));
                    repeatType.setTextColor(Color.parseColor("#000000"));

                    String time = alarmTime.getText().toString();
                    String ampm = alarmAmPm.getText().toString();
                    // get time to alarm
                    AlarmInfoActivity alarmInfoActivity = new AlarmInfoActivity();
                    String tmToAlarm = alarmInfoActivity.getTimeForAlarm(time, ampm);
                    timeToAlarm.setText(tmToAlarm);

                    // enable alarm
                    ContentValues updValues = new ContentValues();
                    updValues.put("alarm_switch","true");
                    int result =  db.update("alarm_tbl", updValues, "id=" + alarm_id, null);

                    // Set alarm
                    SetAlarm setAlarm = new SetAlarm();
                    setAlarm.alarmSetter(alarmContext, alarm_id);

                }
                else{
                    alarmTime.setTextColor(Color.parseColor("#808080"));
                    alarmAmPm.setTextColor(Color.parseColor("#808080"));
                    alarmNote.setTextColor(Color.parseColor("#808080"));
                    repeatType.setTextColor(Color.parseColor("#808080"));
                    timeToAlarm.setText("");

                    // disable alarm
                    ContentValues updValues = new ContentValues();
                    updValues.put("alarm_switch","false");
                    int result =  db.update("alarm_tbl", updValues, "id=" + alarm_id, null);

                    // cancel alarm request in alarm manager.
                    AlarmInfoActivity alarmInfoActivity = new AlarmInfoActivity();
                    final AlarmManager am = (AlarmManager) alarmContext.getSystemService(Context.ALARM_SERVICE);
                    alarmInfoActivity.cancelAlarm(alarmContext, alarm_request_code); // Cancel this alarm if scheduled already.

                }
            }
        });

        // save item id to tag

        itemView.setTag(alarmItemList.get(position).getId());

        return itemView;
    }


}// End of class
