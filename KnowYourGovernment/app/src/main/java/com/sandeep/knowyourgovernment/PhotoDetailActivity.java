package com.sandeep.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Sandeep on 12-04-2017.
 */

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";

    private String personOffice, personName, personParty, image, lo;
    private TextView off, n, locText;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: Entered");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);

        off = (TextView) findViewById(R.id.officePhoto);
        n = (TextView) findViewById(R.id.namePhoto);
        locText = (TextView) findViewById(R.id.locationText3);
        pic = (ImageView) findViewById(R.id.imagePhoto);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.photoLayout);

        Intent intent = getIntent();
        if (intent.hasExtra("OFF") && intent.hasExtra("NAME") && intent.hasExtra("PARTY")) {
            personOffice = intent.getStringExtra("OFF");
            personName = intent.getStringExtra("NAME");
            personParty = intent.getStringExtra("PARTY");
            image = intent.getStringExtra("imageUrl");
            lo = intent.getStringExtra("l");
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            if (!personParty.equals("No Data Provided")) {

                Log.d(TAG, "onCreate: Inside party statement");

                if (personParty.equals("Republican")) {
                    constraintLayout.setBackgroundColor(Color.RED);
                } else if (personParty.equals("Democrat") || personParty.equals("Democratic")) {
                    constraintLayout.setBackgroundColor(Color.BLUE);
                } else {
                    constraintLayout.setBackgroundColor(Color.BLACK);
                }
            } else {
                constraintLayout.setBackgroundColor(Color.BLACK);
            }

            if (lo != null && !lo.isEmpty()) {

                Log.d(TAG, "onCreate: Inside location statement");
                locText.setText(lo);
            } else {
                locText.setText(R.string.noLocationFound);
            }


            if (!personName.equals("No Data Provided")) {
                Log.d(TAG, "onCreate: Inside Name statement");
                n.setText(personName);
            } else {
                n.setText("No Data Provided");
            }

            if (!personOffice.equals("No Data Provided")) {

                Log.d(TAG, "onCreate: Inside Office statement");
                off.setText(personOffice);
            } else {
                off.setText("No Data Provided");
            }

            if (image != null) {

                Log.d(TAG, "onCreate: Inside image statement");
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        // Here we try https if the http image attempt failed
                        final String changedUrl = image.replace("http:", "https:");
                        picasso.load(changedUrl).fit()
                                .error(R.drawable.missingimage)
                                .placeholder(R.drawable.placeholder)
                                .into(pic);
                    }
                }).build();

                picasso.load(image).fit()
                        .error(R.drawable.missingimage)
                        .placeholder(R.drawable.placeholder)
                        .into(pic);
            } else {
                Picasso.with(this).load(image).fit()
                        .error(R.drawable.missingimage)
                        .placeholder(R.drawable.missingimage)
                        .into(pic);
            }
        }else {
            if (!personParty.equals("No Data Provided")) {

                Log.d(TAG, "onCreate: Inside party statement");

                if (personParty.equals("Republican")) {
                    constraintLayout.setBackgroundColor(Color.RED);
                } else if (personParty.equals("Democrat") || personParty.equals("Democratic")) {
                    constraintLayout.setBackgroundColor(Color.BLUE);
                } else {
                    constraintLayout.setBackgroundColor(Color.BLACK);
                }
            } else {
                constraintLayout.setBackgroundColor(Color.BLACK);
            }

            if (lo != null && !lo.isEmpty()) {

                Log.d(TAG, "onCreate: Inside location statement");
                locText.setText(lo);
            } else {
                locText.setText(R.string.noLocationFound);
            }


            if (!personName.equals("No Data Provided")) {
                Log.d(TAG, "onCreate: Inside Name statement");
                n.setText(personName);
            } else {
                n.setText("No Data Provided");
            }

            if (!personOffice.equals("No Data Provided")) {

                Log.d(TAG, "onCreate: Inside Office statement");
                off.setText(personOffice);
            } else {
                off.setText("No Data Provided");
            }

            if (image != null) {

                Log.d(TAG, "onCreate: Inside image statement");
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        // Here we try https if the http image attempt failed
                        final String changedUrl = image.replace("http:", "https:");
                        picasso.load(changedUrl).error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(pic);
                    }
                }).build();

                picasso.load(image)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(pic);
            } else {
                Picasso.with(this).load(image)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(pic);
            }
        }
    }
}
