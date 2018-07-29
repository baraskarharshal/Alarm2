package com.apps.rdjsmartapps.alarm;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RingtoneActivity extends AppCompatActivity {

    public Button ringOkButton, ringCancelButton;
    public TextView addNewRingtone;
    public RadioGroup ringOptions;
    public String mode, id, queryString, selectedSongName, note, time, ampm, repeatType, repeatDays, ringtone, vibrate_switch;;
    public int currentSongId;
    public Cursor c;
    public DBSingleton dbInstance;
    public SQLiteDatabase db;
    public StringBuffer alarmBuffer;
    public MediaPlayer mediaPlayer;
    public static int ringtoneId;
    public List<Integer> ringtoneIdList = new ArrayList<>();

    public static final String TAG = "RingtoneActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtone_activity);
        // Setting activity dialog window size
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        // Fetch bundle variables values
        Bundle inBundle = getIntent().getExtras();
        mode = inBundle.get("mode").toString();
        if(mode.equals("update")) {
            id = inBundle.get("id").toString();
        }

        time = inBundle.get("time").toString();
        ampm = inBundle.get("ampm").toString();
        repeatType = inBundle.get("repeatType").toString();
        repeatDays = inBundle.get("repeatDays").toString();
        note = inBundle.get("note").toString();
        ringtone = inBundle.get("ringtone").toString();
        vibrate_switch = inBundle.get("vibrate_switch").toString();

        selectedSongName = ringtone;

        // Initializing static variable with random integer from 0 to 50.
        Random rand = new Random();
        ringtoneId = rand.nextInt(50) + 1;

        // Initialize view objects
        ringOkButton = (Button) findViewById(R.id.okButton);
        ringCancelButton = (Button) findViewById(R.id.cancelButton);
        addNewRingtone = (TextView) findViewById(R.id.addNewRingtone);

        // Initializing db object
        dbInstance = DBSingleton.getInstance(getApplicationContext());
        db = dbInstance.getWritableDatabase();

        // Initializing media player
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.alarm_tone );

        // fetching and displaying songs list
        ringOptions = (RadioGroup)  findViewById(R.id.ringOptions);
        ringOptions.setOrientation(LinearLayout.VERTICAL);
        Field[] fields = R.raw.class.getDeclaredFields();
        final float scale = getResources().getDisplayMetrics().density;
        int padding_20dp = (int) (20 * scale + 0.5f);

        // songs list from raw folder
        for (Field f : fields) {
            RadioButton rdButton = new RadioButton(this);
            rdButton.setId(ringtoneId);
            ringtoneIdList.add(ringtoneId);
            rdButton.setText(f.getName());
            rdButton.setTextSize(17);
            rdButton.setTextColor(Color.parseColor("#808080"));
            rdButton.setPadding(padding_20dp,padding_20dp, padding_20dp, padding_20dp);
            rdButton.setOnClickListener(onSelectRingtone);
            ringOptions.addView(rdButton);
            ringtoneId = ringtoneId + 1;
        }

        // songs list from songs table
        queryString = "SELECT * FROM alarm_song";
        c = db.rawQuery(queryString, null);
        if(c.getCount()==0)
        {
            dbInstance.msg(this, "No ringtone present!");
        }
        alarmBuffer = new StringBuffer();

        while(c.moveToNext()) {
            // get alarm values
            String song_name = c.getString(2);
            RadioButton rdButton = new RadioButton(this);
            rdButton.setId(ringtoneId);
            ringtoneIdList.add(ringtoneId);
            rdButton.setText(song_name);
            rdButton.setTextSize(17);
            rdButton.setTextColor(Color.parseColor("#808080"));
            rdButton.setPadding(padding_20dp,padding_20dp, padding_20dp, padding_20dp);
            rdButton.setOnClickListener(onSelectRingtoneFromSDCard);
            ringOptions.addView(rdButton);
            ringtoneId = ringtoneId + 1;
        }

        // traversing throung list of songs to check selected one

        if(mode.equals("update")) {
            Iterator<Integer> iterator = ringtoneIdList.iterator();
            while (iterator.hasNext()) {
                RadioButton rdButton = (RadioButton) findViewById(iterator.next());
                if (rdButton != null && selectedSongName.equals(rdButton.getText())) {
                    rdButton.setChecked(true);
                    break;
                }
            }
        }

        // setting onclick listeners for ok and cancel button

        ringOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // stop already playing song
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                //Go back to AlarmInfoActivity with selected ringtone
                Intent AlarmInfoIntent = new Intent(RingtoneActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","RingtoneActivity");
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
                AlarmInfoIntent.putExtra("vibrate_switch",vibrate_switch);
                AlarmInfoIntent.putExtra("ringtone",selectedSongName);
                startActivity(AlarmInfoIntent);
            }
        });

        ringCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // stop already playing song
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                Intent AlarmInfoIntent = new Intent(RingtoneActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","RingtoneActivity");
                AlarmInfoIntent.putExtra("result","cancel");
                AlarmInfoIntent.putExtra("mode",mode);
                if(mode.equals("update")) {
                    AlarmInfoIntent.putExtra("id", id);
                }

                AlarmInfoIntent.putExtra("note",note);
                AlarmInfoIntent.putExtra("time",time);
                AlarmInfoIntent.putExtra("ampm",ampm);
                AlarmInfoIntent.putExtra("repeatType",repeatType);
                AlarmInfoIntent.putExtra("repeatDays",repeatDays);
                AlarmInfoIntent.putExtra("vibrate_switch",vibrate_switch);
                AlarmInfoIntent.putExtra("ringtone",ringtone);
                startActivity(AlarmInfoIntent);
            }
        });

        // Setting onclicklistener for add new ringtone

        addNewRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);

            }
        });

    } // End of OnCreate

    // Setting onactivity result for selecting audio file

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == 10){
            Uri uriSound=data.getData();
            saveSong(getApplicationContext(), uriSound);
            Toast.makeText(getApplicationContext(), "Song selected", Toast.LENGTH_SHORT).show();
        }
    }

    // save song method

    private void saveSong(Context context, Uri uri) {

        try {
            //Toast.makeText(getApplicationContext(), "inside play. Uri:"+uri.toString(), Toast.LENGTH_SHORT).show();
            Log.d("AlarmInfoActivity", uri.toString());
            String[] proj = { MediaStore.Audio.Media.DATA };
            Cursor ringtoneCursor = context.getContentResolver().query(uri, proj, null, null, null);
            ringtoneCursor.moveToFirst();
            String path = ringtoneCursor.getString(ringtoneCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            // check if song already present in the song table or not
            queryString = "SELECT * FROM alarm_song WHERE song_path = '"+path+"'";
            c = db.rawQuery(queryString, null);
            if(c.getCount()==0) {
            /* Store song info in database table*/
                int index = path.lastIndexOf('/');
                String song_name = path.substring(index + 1, path.length() - 4);
                //dbInstance.msg(getApplicationContext(), "Song name:" + song_name);
                Log.d("AlarmInfoActivity", "path: " + path + "  song name: " + song_name);
                // insert 1 row
                ContentValues songValues = new ContentValues();
                songValues.put("song_path", path);
                songValues.put("song_name", song_name);
                Long result = db.insert("alarm_song", null, songValues);
                //dbInstance.msg(getApplicationContext(), "New song is saved in table!");

                // add song in radio buttons list

                RadioButton rdButton = new RadioButton(this);
                final float scale = getResources().getDisplayMetrics().density;
                int padding_20dp = (int) (20 * scale + 0.5f);
                rdButton.setText(song_name);
                rdButton.setPadding(padding_20dp,padding_20dp, padding_20dp, padding_20dp);
                rdButton.setOnClickListener(onSelectRingtoneFromSDCard);
                ringOptions.addView(rdButton);

            }
            else{
                dbInstance.msg(this, "Ringtone already present in the list!");
            }
            ringtoneCursor.close();


        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    // Setting method on selecting ringtone from raw folder.

    View.OnClickListener onSelectRingtone = new View.OnClickListener(){
        public void onClick(View v) {

            // stop already playing song
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }

            // Playing selected song
            RadioGroup rg = (RadioGroup) findViewById(R.id.ringOptions);
            //RadioButton rd = (RadioButton)ringtoneDialog.findViewById(rg.getCheckedRadioButtonId());
            selectedSongName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
            //rg.clearCheck();
            //rd.setChecked(true);
            currentSongId = getResources().getIdentifier(selectedSongName, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),currentSongId );
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.start();
        }
    };


    // Setting method on selecting ringtone from sd card.

    View.OnClickListener onSelectRingtoneFromSDCard = new View.OnClickListener(){
        public void onClick(View v) {

            // stop already playing song
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }

            // Playing selected song
            RadioGroup rg = (RadioGroup) findViewById(R.id.ringOptions);
            selectedSongName = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();
            Log.d(TAG, selectedSongName);

            // Fetch ringtone path from song table
            queryString = "SELECT * FROM alarm_song WHERE song_name = '"+selectedSongName+"'";
            c = db.rawQuery(queryString, null);
            if(c.getCount()==0)
            {
                dbInstance.msg(getApplicationContext(), "Ringtone not found!");
            }
            alarmBuffer = new StringBuffer();
            String path = "";

            while(c.moveToNext())
            {
                path = c.getString(1);
                break;
            }

            playSongFromSDCard(path);

        }
    };


    // method to play song from sd card

    public void playSongFromSDCard(String path){

        try{

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(true);
            Log.d(TAG, path);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });
            mediaPlayer.prepareAsync();


        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stopPlayingSong(){
        // stop already playing song
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }



}
