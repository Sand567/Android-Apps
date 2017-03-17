package com.sandeep.multinote;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView noteTitle;
    private TextView name;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        noteTitle = (TextView) findViewById(R.id.aboutTitle);
        name = (TextView) findViewById(R.id.aboutName);
        version = (TextView) findViewById(R.id.aboutVersion);
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/android_7.ttf");
        noteTitle.setTypeface(typeface1);
        name.setTypeface(typeface1);
        version.setTypeface(typeface1);
    }
}
