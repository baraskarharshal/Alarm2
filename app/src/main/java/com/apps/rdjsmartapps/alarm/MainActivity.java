package com.apps.rdjsmartapps.alarm;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ListView alarmList;
    public List<Item> aList;
    private ItemListAdapter adapter;
    public SQLiteDatabase db, db2;
    public DBSingleton dbInstance;
    public TextView jobInfo;
    String queryString, hour, min, tmToAlarm = "";
    Cursor c;
    StringBuffer alarmBuffer;
    String[] timeArr;
    Intent mServiceIntent;
    private SensorService mSensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep service running
        mSensorService = new SensorService(getApplicationContext());
        mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }


        // Initializing db object
        dbInstance = DBSingleton.getInstance(getApplicationContext());
        db = dbInstance.getReadableDatabase();

        // Fetch Alarm list from database

        queryString = "SELECT * FROM alarm_tbl";
        c = db.rawQuery(queryString, null);
        if(c.getCount()==0)
        {
            dbInstance.msg(this, "No alarms found!");
        }

        alarmBuffer = new StringBuffer();
        // Initializing list view object
        alarmList = (ListView) findViewById(R.id.alarmList);

        // Initializing list class object and adding items in it
        aList = new ArrayList<>();

        while(c.moveToNext())
        {
            int id;
            String time, ampm, repeatType, repeatDays, note, alarmSwitch, ringtone, vibrateSwitch;
            id = Integer.valueOf(c.getString(0));
            time = c.getString(1);
            ampm = c.getString(2);
            repeatType = c.getString(3);
            note = c.getString(5);
            alarmSwitch = c.getString(6);

            // format time in 00:00 format
            timeArr = time.split(":");
            hour = timeArr[0];
            min = timeArr[1];
            if(hour.length() == 1){
                hour = '0' + hour;
            }
            if(min.length() == 1){
                min = '0' + min;
            }
            time = hour + ":" + min;


            aList.add(new Item(id, time, ampm, repeatType.trim(), note, alarmSwitch, tmToAlarm));

        }


        // Initializing custom adapter class object.
        adapter = new ItemListAdapter(this, aList); //Adding list in adapter
        alarmList.setAdapter(adapter); // Assign adapter to list view


        // set on item click listener

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),"Clicked on Item: " + view.getTag(),Toast.LENGTH_LONG ).show();

                String id = view.getTag().toString();
                int itemId = Integer.valueOf(id);
                Intent alarmInfo = new Intent(MainActivity.this, AlarmInfoActivity.class);
                String mode = "update";
                alarmInfo.putExtra("mode",mode);
                alarmInfo.putExtra("id",itemId);
                alarmInfo.putExtra("from_activity","MainActivity");
                startActivity(alarmInfo);
            }
        });


        // Switch on click event listener



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Navigate to alarminfo activity to setup new alarm
                String mode = "add";
                Intent alarmInfo = new Intent(MainActivity.this, AlarmInfoActivity.class);
                alarmInfo.putExtra("mode",mode);
                alarmInfo.putExtra("from_activity","MainActivity");
                startActivity(alarmInfo);
            }
        });




    } // End of onCreate




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // method to check service run state
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
        Log.d("MainActivity", "inside onDestroy");
        //startService(new Intent(this, NotificationTrigger.class));
    }

    // To remove settings three dots in title ribbon
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

}
