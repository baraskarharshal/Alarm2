package com.apps.rdjsmartapps.alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    Button noteOkButton, noteCancelButton;
    EditText noteEdit;
    String mode, id, note, time, ampm, repeatType, repeatDays, ringtone, vibrate_switch;
    private static final String TAG = "NoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        // Setting activity dialog window size
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initializing view objects
        noteEdit = (EditText) findViewById(R.id.noteEdit);
        noteOkButton = (Button) findViewById(R.id.okButton);
        noteCancelButton = (Button) findViewById(R.id.cancelButton);

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

        noteEdit.setText(note);

        // Setting onclick listeners

        noteOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note = noteEdit.getText().toString();
                //Go back to AlarmInfoActivity with selected ringtone
                Intent AlarmInfoIntent = new Intent(NoteActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","NoteActivity");
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

        noteCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Go back to AlarmInfoActivity with selected ringtone
                Intent AlarmInfoIntent = new Intent(NoteActivity.this, AlarmInfoActivity.class);
                AlarmInfoIntent.putExtra("from_activity","NoteActivity");
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
                AlarmInfoIntent.putExtra("ringtone",ringtone);
                AlarmInfoIntent.putExtra("vibrate_switch",vibrate_switch);

                startActivity(AlarmInfoIntent);
            }
        });



    }
}
