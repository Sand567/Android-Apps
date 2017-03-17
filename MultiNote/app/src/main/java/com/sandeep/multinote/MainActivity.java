package com.sandeep.multinote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandeep on 15-02-2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    public static List<Note> mNote = new ArrayList<>();
    public List<Note> newList = new ArrayList<>();
    public List<Note> thirdList = new ArrayList<>();
    private boolean running = false;
    int size = 0;

    public static NoteAdapter noteAdapter;
    EditActivity editActivity = new EditActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.noteRecycler);
        Log.d(TAG, "onCreate: before call to noteAdapter");
        noteAdapter = new NoteAdapter(mNote, this);
        Log.d(TAG, "onCreate: After call to noteadapter");
        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "onCreate: Called");

        Intent intent = getIntent();
        Log.d(TAG, "onCreate: After saving from either side: "+intent.getStringExtra("someString")+"======================================");

        if(!((intent.hasExtra("someString")) || (intent.hasExtra("Hey")) || (intent.hasExtra("cancelPressed")))) {
            Log.d(TAG, "onCreate: Inside if before onResult()");
            onResult();
        }
    }

    public void onResult() {
        Log.d(TAG, "onResult: ");
        if(running) {
            Toast.makeText(this, R.string.complete, Toast.LENGTH_LONG).show();
            return;
        }

        new MyAsyncTask().execute();
    }






    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "MyAsyncTask";
        private Note note = new Note();

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<Note> a = new ArrayList<>();
            Log.d(TAG, "doInBackground: ");
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(MainActivity.this, R.string.taskDone, Toast.LENGTH_LONG).show();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d(TAG, "onProgressUpdate: ");
            super.onProgressUpdate(values);
            Log.d(TAG, "loadNote: Inside");
            try {
                Log.d(TAG, "loadNote: Before reading file");
                FileInputStream fileInputStream = getApplicationContext().openFileInput(getString(R.string.fileName));
                JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream, getString(R.string.encoding)));

                jsonReader.beginArray();

                while(jsonReader.hasNext()) {

//                    newList.add(EditActivity.k, loading(jsonReader));
//                    EditActivity.k++;
                    newList.add(0, loading(jsonReader));
                }
                jsonReader.endArray();

                Log.d(TAG, "onProgressUpdate: mNote contents***********************************************************");
                for(Note n : mNote) {
                    Log.d(TAG, "onProgressUpdate: "+n.getTitle()+"         "+n.getNotes());
                }
                mNote.addAll(newList);
                noteAdapter.notifyDataSetChanged();

            }catch (FileNotFoundException fnf) {
                Toast.makeText(MainActivity.this, R.string.notSaved, Toast.LENGTH_LONG).show();
                fnf.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Note loading(JsonReader jsonReader) {
        Log.d(TAG, "loading: IN");
        Note note = new Note();
        try {

            jsonReader.beginObject();
            while(jsonReader.hasNext()) {

                String name = jsonReader.nextName();
                if(name.equals("dateTime")) {
                    note.setDateAndTime(jsonReader.nextString());
                }else if(name.equals("notepad")) {
                    note.setNotes(jsonReader.nextString());
                }else if(name.equals("newTitle")) {
                    note.setTitle(jsonReader.nextString());
                }else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("returning note!!");
        return note;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Note note = mNote.get(position);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        String pos = Integer.toString(position);
        intent.putExtra("positionValue", pos);
        intent.putExtra("date and time", note.getDateAndTime());
        intent.putExtra("title", note.getTitle());
        intent.putExtra("notes", note.getNotes());
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(final View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_black_48dp);
        builder.setMessage(R.string.delete);
        builder.setTitle(R.string.dialogTitle);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = recyclerView.getChildLayoutPosition(v);
                mNote.remove(position);
                size = mNote.size();
                noteAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addNote:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                return true;

            case R.id.showInfo:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent1);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: Called ********************************");
        super.onPause();
        Log.d(TAG, "onPause: ");
        saveNote();
    }

    private void saveNote() {

        Log.d("Saving", "Note");
        try {
            Log.d(TAG, "saveNote: Entering");
            System.out.println("In save notes!!!");
          /*  PrintWriter writer = new PrintWriter(getString(R.string.fileName));
            writer.print("");
            writer.close();*/
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.fileName), Context.MODE_PRIVATE);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, getString(R.string.encoding)));

            jsonWriter.setIndent(" ");
            jsonWriter.beginArray();

            int count=0;
                for(Note note:mNote) {
                    jsonWriter.beginObject();
                    jsonWriter.name("dateTime").value(note.getDateAndTime());
                    jsonWriter.name("notepad").value(note.getNotes());
                    jsonWriter.name("newTitle").value(note.getTitle());
                    jsonWriter.endObject();
                    System.out.println("count "+count);
                }

               jsonWriter.endArray();

           // jsonWriter.close();
/*

                StringWriter sw = new StringWriter();
                jsonWriter = new JsonWriter(sw);
                jsonWriter.setIndent(" ");

                jsonWriter.beginArray();
                for(Note note:MainActivity.mNote) {
                    jsonWriter.beginObject();
                    jsonWriter.name("dateTime").value(note.getDateAndTime());
                    jsonWriter.name("notepad").value(note.getNotes());
                    jsonWriter.name("newTitle").value(note.getTitle());
                    Log.d(TAG, "saveProduct: JSON:\n" + sw.toString());
                    jsonWriter.endObject();
                }*/
               // jsonWriter.endArray();


            jsonWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();

            File f = new File(getString(R.string.fileName));

            String f2 = f.getCanonicalPath();
            System.out.println(f2);

            Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void saveNote() {
//
//        try {
//            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.fileName), Context.MODE_PRIVATE);
//            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, getString(R.string.encoding)));
//
//            jsonWriter.setIndent("  ");
//            jsonWriter.beginArray();
//
//            for(Note note: MainActivity.mNote) {
//                saving(jsonWriter, note);
//            }
//
//            jsonWriter.endArray();
//            jsonWriter.close();
//            //fileOutputStream.close();
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saving(JsonWriter jsonwriter, Note note) {
//
//        try {
//            jsonwriter.beginObject();
//            jsonwriter.name("dateTime").value(note.getDateAndTime());
//            jsonwriter.name("notepad").value(note.getNotes());
//            jsonwriter.name("newTitle").value(note.getTitle());
//            jsonwriter.endObject();
//
//            StringWriter sw = new StringWriter();
//            jsonwriter = new JsonWriter(sw);
//
//            jsonwriter.beginObject();
//            jsonwriter.name("dateTime").value(note.getDateAndTime());
//            jsonwriter.name("notepad").value(note.getNotes());
//            jsonwriter.name("newTitle").value(note.getTitle());
//            jsonwriter.endObject();
//
//            Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
