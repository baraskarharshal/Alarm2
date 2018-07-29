package com.apps.rdjsmartapps.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Harshal on 4/21/2018.
 */

public class DBSingleton extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "alarm_db";
    private static final String DATABASE_TABLE = "alarm_tbl";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS alarm_tbl" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ",time VARCHAR" +
            ",ampm VARCHAR" +
            ",repeat_type VARCHAR" +
            ",repeat_days VARCHAR" +
            ",note VARCHAR" +
            ",alarm_switch VARCHAR" +
            ",ringtone VARCHAR" +
            ",vibrate_switch VARCHAR" +
            ",request_code VARCHAR" +
            ");";

    private static final String CREATE_TABLE_SONG = "CREATE TABLE IF NOT EXISTS alarm_song" +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ",song_path VARCHAR" +
            ",song_name VARCHAR" +
            ");";

    public SQLiteDatabase db;


    private static DBSingleton dbInstance;

    public static DBSingleton getInstance(Context context) {

        if (dbInstance == null){ //if there is no instance available... create new one
            dbInstance = new DBSingleton(context.getApplicationContext());

        }
        return dbInstance;
    }

    // private constructor
    private DBSingleton(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DBSingleton.CREATE_TABLE);
        db.execSQL(DBSingleton.CREATE_TABLE_SONG);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_NAME);

    }

// Display message function

    public void msg(Context context, String str)
    {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


} // End of class
