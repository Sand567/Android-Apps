package com.sandeep.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Sandeep on 08-04-2017.
 */

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";

    private TextView office, name, party, officeAddress, phone, email, website, location;
    private ImageView facebookLink, youTubeLink, twitterlink, googlePluslink, imageLink;
    private String loc1, office1, name1, party1, officeAddress1, phone1, email1, website1, facebookLink1, twitterLink1, youtubeLink1, googlePlusLink1, imageLink1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_layout);

        office = (TextView) findViewById(R.id.officeName);
        name = (TextView) findViewById(R.id.officialName);
        party = (TextView) findViewById(R.id.officialParty);
        officeAddress = (TextView) findViewById(R.id.officialAddress);
        phone = (TextView) findViewById(R.id.officialPhone);
        email = (TextView) findViewById(R.id.officialEmail);
        website = (TextView) findViewById(R.id.officialWebsite);
        location = (TextView) findViewById(R.id.locationText2);
        facebookLink = (ImageView) findViewById(R.id.linkFacebook);
        youTubeLink = (ImageView) findViewById(R.id.linkYoutube);
        twitterlink = (ImageView) findViewById(R.id.linkTwitter);
        googlePluslink = (ImageView) findViewById(R.id.linkGoogleplus);
        imageLink = (ImageView) findViewById(R.id.officialImage);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.cLayout);

        Intent intent = getIntent();
        if (intent.hasExtra("office")) {

            Log.d(TAG, "onCreate: Entered if for intent");

            office1 = intent.getStringExtra("office");
            name1 = intent.getStringExtra("name");
            party1 = intent.getStringExtra("party");
            officeAddress1 = intent.getStringExtra("officeAddress");
            phone1 = intent.getStringExtra("phone");
            email1 = intent.getStringExtra("email");
            website1 = intent.getStringExtra("website");
            facebookLink1 = intent.getStringExtra("facebookUrl");
            youtubeLink1 = intent.getStringExtra("youtubeUrl");
            twitterLink1 = intent.getStringExtra("twitterUrl");
            googlePlusLink1 = intent.getStringExtra("googlePlusUrl");
            imageLink1 = intent.getStringExtra("imageUrl");
            loc1 = intent.getStringExtra("settingLoc");
        }

        Log.d(TAG, "onCreate: Party ==> " + party1);
        Log.d(TAG, "onCreate: GooglePlusId ==> " + googlePlusLink1);
        Log.d(TAG, "onCreate: YoutubeId ==> " + youtubeLink1);
        Log.d(TAG, "onCreate: FacebookId ==> " + facebookLink1);
        Log.d(TAG, "onCreate: TwitterId ==> " + twitterLink1);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            if (loc1 != null && !loc1.isEmpty()) {
                location.setText(loc1);
            } else {
                location.setText(R.string.noLocationFound);
            }

            if (party1 != "") {
                Log.d(TAG, "onCreate: Value of party1 inside the first if ==> " + party1);
                if (party1.equals("Republican")) {
                    Log.d(TAG, "onCreate: Entered in Republican");
                    scrollView.setBackgroundColor(Color.RED);
                    constraintLayout.setBackgroundColor(Color.RED);
                    party.setText("(" + party1 + ")");
                } else if (party1.equals("Democrat") || party1.equals("Democratic")) {
                    Log.d(TAG, "onCreate: Entered in Democrat");
                    scrollView.setBackgroundColor(Color.BLUE);
                    constraintLayout.setBackgroundColor(Color.BLUE);
                    party.setText("(" + party1 + ")");
                } else {
                    party.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onCreate: Entered in Unknown");
                    scrollView.setBackgroundColor(Color.BLACK);
                    constraintLayout.setBackgroundColor(Color.BLACK);
                    party.setText("(" + party1 + ")");
                }
            } else {
                party.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onCreate: Entered the first else for the first if");
                scrollView.setBackgroundColor(Color.BLACK);
                constraintLayout.setBackgroundColor(Color.BLACK);
                party.setText("");
            }

            if (office1 != "No Data Provided") {
                Log.d(TAG, "onCreate: Entered if for office");
                office.setText(office1);
            } else {
                office.setText(R.string.noDataProvided);
            }

            if (name1 != "No Data Provided") {
                name.setText(name1);
            } else {
                name.setText(R.string.noDataProvided);
            }

            if (imageLink1 != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        // Here we try https if the http image attempt failed
                        final String changedUrl = imageLink1.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .fit()
                                .error(R.drawable.missingimage)
                                .placeholder(R.drawable.placeholder)
                                .into(imageLink);
                    }
                }).build();

                picasso.load(imageLink1).fit()
                        .error(R.drawable.missingimage)
                        .placeholder(R.drawable.placeholder)
                        .into(imageLink);
            } else {
                Picasso.with(this)
                        .load(imageLink1).fit()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(imageLink);
            }

            Log.d(TAG, "onCreate: ==========================================");
            Log.d(TAG, "onCreate: IMAGE LINK ==> "+imageLink1);
            Log.d(TAG, "onCreate: OFFICE ADDRESS ==> "+officeAddress1);
            Log.d(TAG, "onCreate: ==========================================");
            if (officeAddress1 != "No Data Provided") {
                officeAddress.setText(officeAddress1);
                Linkify.addLinks(officeAddress, Linkify.MAP_ADDRESSES);
            } else {
                officeAddress.setText(R.string.noDataProvided);
            }

            if (phone1 != "No Data Provided") {
                phone.setText(phone1);
                Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);

            } else {
                phone.setText(R.string.noDataProvided);
            }

            if (email1 != "No Data Provided") {
                email.setText(email1);
                Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            } else {
                email.setText(R.string.noDataProvided);
            }
//
            if (website1 != "No Data Provided") {
                website.setText(website1);
                Linkify.addLinks(website, Linkify.WEB_URLS);
            } else {
                website.setText(R.string.noDataProvided);
            }
//
            if (!facebookLink1.equals("No Data Provided") && facebookLink1 != null) {
                facebookLink.setVisibility(View.VISIBLE);
                facebookLink.setImageResource(R.drawable.facebook);
            } else {
                facebookLink.setVisibility(View.INVISIBLE);
            }
//
            if (!googlePlusLink1.equals("No Data Provided") && googlePlusLink1 != null) {
                facebookLink.setVisibility(View.VISIBLE);
                googlePluslink.setImageResource(R.drawable.googleplus);
            } else {
                facebookLink.setVisibility(View.INVISIBLE);
            }
//
            if (!youtubeLink1.equals("No Data Provided") && youtubeLink1 != null) {
                youTubeLink.setVisibility(View.VISIBLE);
                youTubeLink.setImageResource(R.drawable.youtube);
            } else {
                youTubeLink.setVisibility(View.INVISIBLE);
            }
//
            if (!twitterLink1.equals("No Data Provided") && twitterLink1 != null) {
                twitterlink.setVisibility(View.VISIBLE);
                twitterlink.setImageResource(R.drawable.twitter);
            } else {
                twitterlink.setVisibility(View.INVISIBLE);
            }
        }else{
            if (loc1 != null && !loc1.isEmpty()) {
                location.setText(loc1);
            } else {
                location.setText(R.string.noLocationFound);
            }

            if (party1 != "") {
                Log.d(TAG, "onCreate: Value of party1 inside the first if ==> " + party1);
                if (party1.equals("Republican")) {
                    Log.d(TAG, "onCreate: Entered in Republican");
                    scrollView.setBackgroundColor(Color.RED);
                    party.setText("(" + party1 + ")");
                } else if (party1.equals("Democrat") || party1.equals("Democratic")) {
                    Log.d(TAG, "onCreate: Entered in Democrat");
                    scrollView.setBackgroundColor(Color.BLUE);
                    party.setText("(" + party1 + ")");
                } else {
                    party.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onCreate: Entered in Unknown");
                    scrollView.setBackgroundColor(Color.BLACK);
                    party.setText("(" + party1 + ")");
                }
            } else {
                party.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onCreate: Entered the first else for the first if");
                scrollView.setBackgroundColor(Color.BLACK);
                party.setText("");
            }

            if (office1 != "No Data Provided") {
                Log.d(TAG, "onCreate: Entered if for office");
                office.setText(office1);
            } else {
                office.setText(R.string.noDataProvided);
            }

            if (name1 != "No Data Provided") {
                name.setText(name1);
            } else {
                name.setText(R.string.noDataProvided);
            }
//
            Log.d(TAG, "onCreate: =================================");
            Log.d(TAG, "onCreate: Image Link"+imageLink1);
            Log.d(TAG, "onCreate: =================================");

            if (imageLink1 != null) {

                Log.d(TAG, "onCreate: If imagelink not null");
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        // Here we try https if the http image attempt failed
                        final String changedUrl = imageLink1.replace("http:", "https:");

                        Log.d(TAG, "onImageLoadFailed: If imageload fails ===> In the else if not internet connection");
                        picasso.load(changedUrl).fit()
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.brokenimage)
                                .into(imageLink);
                    }
                }).build();

                Log.d(TAG, "onCreate: Just before load");
                picasso.load(imageLink1).fit()
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(imageLink);
            } else {
                Log.d(TAG, "onCreate: Else entered if imagelink is null");
                Picasso.with(this).load(imageLink1).fit()
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(imageLink);
            }
//
            if (officeAddress1 != "No Data Provided") {
                officeAddress.setText(officeAddress1);
                Linkify.addLinks(officeAddress, Linkify.MAP_ADDRESSES);
            } else {
                officeAddress.setText(R.string.noDataProvided);
            }

            if (phone1 != "No Data Provided") {
                phone.setText(phone1);
                Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);

            } else {
                phone.setText(R.string.noDataProvided);
            }

            if (email1 != "No Data Provided") {
                email.setText(email1);
                Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            } else {
                email.setText(R.string.noDataProvided);
            }
//
            if (website1 != "No Data Provided") {
                website.setText(website1);
                Linkify.addLinks(website, Linkify.WEB_URLS);
            } else {
                website.setText(R.string.noDataProvided);
            }
//
            if (!facebookLink1.equals("No Data Provided")) {
                facebookLink.setVisibility(View.VISIBLE);
                facebookLink.setImageResource(R.drawable.facebook);
            } else {
                facebookLink.setVisibility(View.INVISIBLE);
            }
//
            if (!googlePlusLink1.equals("No Data Provided")) {
                facebookLink.setVisibility(View.VISIBLE);
                googlePluslink.setImageResource(R.drawable.googleplus);
            } else {
                facebookLink.setVisibility(View.INVISIBLE);
            }
//
            if (!youtubeLink1.equals("No Data Provided")) {
                youTubeLink.setVisibility(View.VISIBLE);
                youTubeLink.setImageResource(R.drawable.youtube);
            } else {
                youTubeLink.setVisibility(View.INVISIBLE);
            }
//
            if (!twitterLink1.equals("No Data Provided")) {
                twitterlink.setVisibility(View.VISIBLE);
                twitterlink.setImageResource(R.drawable.twitter);
            } else {
                twitterlink.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void facebookClicked(View v) {

        String FACEBOOK_URL = "https://www.facebook.com/" + facebookLink1;
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {

            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {

                //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {

                //older versions of fb app
                urlToUse = "fb://page/" + facebookLink1;
            }

        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }

        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {

        Intent intent = null;
        String name = twitterLink1;
        try {

            // get the Twitter app if possible getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {

            // no Twitter app, revert to browser
            Log.d(TAG, "twitterClicked: Exception ");
            String uri = "https://twitter.com/" + name;
            Log.d(TAG, "twitterClicked: " + uri);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }


    public void googlePlusClicked(View v) {

        String name = googlePlusLink1;
        Intent intent = null;
        try {

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");

            intent.putExtra("customAppUri", name);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {

        String name = youtubeLink1;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));

        }

    }

    public void imageClick(View v) {

        if(imageLink1 != null) {
            Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
            intent.putExtra("l", loc1);
            intent.putExtra("OFF", office1);
            intent.putExtra("NAME", name1);
            intent.putExtra("PARTY", party1);
            intent.putExtra("imageUrl", imageLink1);
            startActivity(intent);
        }
    }
}
