package com.sandeep.multinote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;

import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private EditText newTitle;
    private EditText notepad;
    public static int k = 0;
    Note note = new Note();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd, hh:mm aa", Locale.US);
    Date d = new Date();

    private List<Note> newNoteList = new ArrayList<>();
    int position;
    private String dt, t, n, p;
    private Boolean flag = false, flag1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        newTitle = (EditText) findViewById(R.id.editText1);
        notepad = (EditText) findViewById(R.id.editText2);
        notepad.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if(intent.hasExtra("date and time") && intent.hasExtra("title") && intent.hasExtra("notes")) {
            dt = intent.getStringExtra("date and time");
            t = intent.getStringExtra("title");
            n = intent.getStringExtra("notes");
            p = intent.getStringExtra("positionValue");

            newTitle.setText(t);
            notepad.setText(n);

        }else {
            newTitle.setText("");
            notepad.setText("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                Log.d(TAG, "onOptionsItemSelected: ");

                String title = newTitle.getText().toString();
                String notes = notepad.getText().toString();
                String DT = simpleDateFormat.format(d);


                note.setTitle(title);
                note.setNotes(notes);
                note.setDateAndTime(DT);

                if (title.isEmpty()) {
                    Toast.makeText(this, R.string.untitled, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.d(TAG, "onOptionsItemSelected: Else");

                    for(Note n : MainActivity.mNote) {
                                System.out.println("Title: "+n.getTitle()+"\n"+"Notes: "+n.getNotes()+"\n"+"Date and time: "+n.getDateAndTime());
                    }
//                    MainActivity.mNote.add(k, note);
//                    k++;
//                    MainActivity.noteAdapter.notifyDataSetChanged();

                    MainActivity.mNote.add(0, note);
                    MainActivity.noteAdapter.notifyDataSetChanged();

                    flag = true;
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.putExtra("Hey", "How");
                    startActivity(intent);
                }
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.doSave);
        builder.setTitle(R.string.backTitle);

        String title = newTitle.getText().toString();
        if(title.isEmpty()) {
            Toast.makeText(this, R.string.untitled, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            startActivity(intent);
        }

        builder.setPositiveButton(R.string.saveTheNotes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = newTitle.getText().toString();
                String notes = notepad.getText().toString();
                String DT = simpleDateFormat.format(d);

                note.setNotes(notes);
                note.setTitle(title);
                note.setDateAndTime(DT);

//                MainActivity.mNote.add(k, note);
//                k++;
//                MainActivity.noteAdapter.notifyDataSetChanged();
                MainActivity.mNote.add(0, note);
                MainActivity.noteAdapter.notifyDataSetChanged();

                flag = true;
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                intent.putExtra("someString", "Hi");
                startActivity(intent);

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag = false;

                Intent intent1 = new Intent(EditActivity.this, MainActivity.class);
                intent1.putExtra("cancelPressed", "cancel");
                startActivity(intent1);
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
