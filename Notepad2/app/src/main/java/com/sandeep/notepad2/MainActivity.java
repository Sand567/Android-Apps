package com.sandeep.notepad2;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText notepad;
    private TextView dateTime;
    private Note note;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd, hh:mm aa", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTime = (TextView) findViewById(R.id.textView);
        notepad = (EditText) findViewById(R.id.editText);
        notepad.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onPause() {
        super.onPause();

        Date d = new Date();
        note.setNotes(notepad.getText().toString());
        note.setDateAndTime(simpleDateFormat.format(d));

        saveNote();
    }

    private void saveNote() {

        Log.d("Saving", "Note");
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.fileName), Context.MODE_PRIVATE);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, getString(R.string.encoding)));
            jsonWriter.setIndent(" ");
            jsonWriter.beginObject();
            jsonWriter.name("dateTime").value(note.getDateAndTime());
            jsonWriter.name("notepad").value(note.getNotes());
            jsonWriter.endObject();
            jsonWriter.close();

            Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onStart();
        note = loadNote();
        if(note != null) {
            Log.d("IN", "Inside If not null");
            Date d = new Date();
            Log.d("Setting date and time", "In progress");
            if(note.getDateAndTime() == null) {
                Log.d("IF", "Getting in");
                String text = simpleDateFormat.format(d);
                //dateTime.setText(getResources().getString(R.string.update)+simpleDateFormat.format(d));
                dateTime.setText(getString(R.string.update1, text));
                notepad.setText("");
            }else {
                Log.d("Else", "Getting in");
                String text = note.getDateAndTime();
                //dateTime.setText(getResources().getString(R.string.update)+ note.getDateAndTime());
                dateTime.setText(getString(R.string.update1, text));
                notepad.setText(note.getNotes());
            }
        }
    }

    private Note loadNote() {
        Log.d("Loading File: ", "In Progress");
        note = new Note();
        try {
            Log.d("TRY BLOCK", "Entered");
            FileInputStream fileInputStream = getApplicationContext().openFileInput(getString(R.string.fileName));
            Log.d("Filename", "Found/Not Found");
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream, getString(R.string.encoding)));

            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "dateTime": note.setDateAndTime(jsonReader.nextString());
                                     break;
                    case "notepad": note.setNotes(jsonReader.nextString());
                                    break;
                    default:jsonReader.skipValue();
                            break;
                }
            }
            jsonReader.endObject();

        }catch (FileNotFoundException e) {
            Log.d("Catch", "FileNotFound");
            Toast.makeText(this, R.string.notSaved, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }
}
