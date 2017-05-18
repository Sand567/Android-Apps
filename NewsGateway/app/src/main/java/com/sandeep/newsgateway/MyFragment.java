package com.sandeep.newsgateway;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    private static final String TAG = "MyFragment";

    public static final String KEY1 = "KEY1";
    public static final String KEY2 = "KEY2";
    public static final String KEY3 = "KEY3";
    public static final String KEY4 = "KEY4";
    public static final String KEY5 = "KEY5";
    public static final String KEY6 = "KEY6";
    public static final String KEY7 = "KEY7";

    public static final String NEW_KEY1 = "NEW_KEY1";
    public static final String NEW_KEY2 = "NEW_KEY2";
    public static final String NEW_KEY3 = "NEW_KEY3";
    public static final String NEW_KEY4 = "NEW_KEY4";
    public static final String NEW_KEY5 = "NEW_KEY5";
    public static final String NEW_KEY6 = "NEW_KEY6";
    public static final String NEW_KEY7 = "NEW_KEY7";

    private TextView title1, date1, author1, description1, number1;
    private ImageView url1;

    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance(String title, String date, String url, String author, String desc, String pageNumber, String urlToWebsite) {
        MyFragment myFragment = new MyFragment();
        Bundle bundle = new Bundle(7);
        bundle.putString(KEY1, title);
        bundle.putString(KEY2, date);
        bundle.putString(KEY3, url);
        bundle.putString(KEY4, author);
        bundle.putString(KEY5, desc);
        bundle.putString(KEY6, pageNumber);
        bundle.putString(KEY7, urlToWebsite);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(NEW_KEY1, getArguments().getString(KEY1));
        outState.putString(NEW_KEY2, getArguments().getString(KEY2));
        outState.putString(NEW_KEY3, getArguments().getString(KEY3));
        outState.putString(NEW_KEY4, getArguments().getString(KEY4));
        outState.putString(NEW_KEY5, getArguments().getString(KEY5));
        outState.putString(NEW_KEY6, getArguments().getString(KEY6));
        outState.putString(NEW_KEY7, getArguments().getString(KEY7));
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            // Inflate the layout for this fragment
            String title = getArguments().getString(KEY1);
            String date = getArguments().getString(KEY2);
            final String url = getArguments().getString(KEY3);
            String author = getArguments().getString(KEY4);
            String description = getArguments().getString(KEY5);
            String number = getArguments().getString(KEY6);


            View view = inflater.inflate(R.layout.myfragment_layout, container, false);

            title1 = (TextView) view.findViewById(R.id.title1);
            date1 = (TextView) view.findViewById(R.id.dateAndTime);
            url1 = (ImageView) view.findViewById(R.id.newsImageId);
            author1 = (TextView) view.findViewById(R.id.author);
            description1 = (TextView) view.findViewById(R.id.descriptionText);
            number1 = (TextView) view.findViewById(R.id.number);

            /*
             * Title Setting done here
             */
            if(title != "" || title != null) {
                title1.setText(title);
                title1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String webURL = getArguments().getString(KEY7);
                        Log.d(TAG, "onClick: WEBSITE URL ==> " + webURL);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(webURL));
                        startActivity(intent);
                    }
                });
            }else {
                title1.setVisibility(View.INVISIBLE);
            }

            /*
             * Date Setting done here
             */
            if(date != "" || date != null) {
                Log.d(TAG, "onCreateView: " + date);

                String d2 = "";

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                    Date createdDate = simpleDateFormat.parse(date.replaceAll("Z$", "+0000"));

                    simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.getDefault());
                    String newDate = simpleDateFormat.format(createdDate);

                    String[] d1 = newDate.split(" ");
                    String finalDate = d1[1] + " " + d1[2] + ", 2017 " + d1[3];
                    d2 = finalDate;
                    Log.d(TAG, "onCreateView: Final Date ==> " + finalDate);

                } catch (Exception pe) {
                    Log.d(TAG, "onCreateView: Inside exception");
                }

                Log.d(TAG, "onCreateView: Date outside try" + d2);
                date1.setText(d2);
            }else {
                date1.setVisibility(View.INVISIBLE);
            }

            /*
             * Author Setting done here
             */
            Log.d(TAG, "onCreateView: AUTHOR NAME ==> "+author);
            if(author != ("") || author != (null) || author.length() > 4) {
                Log.d(TAG, "onCreateView: Author if entered");
                author1.setText(author);
            }else if(author.length() == 4) {
                Log.d(TAG, "onCreateView: Entered else if author");
                author1.setText("");
//                author1.setVisibility(View.INVISIBLE);
            }else {
                Log.d(TAG, "onCreateView: Author else entered");
                author1.setText("");
                author1.setVisibility(View.INVISIBLE);
            }

            /*
             * Description Setting done here
             */
            if(description != "" || description != null) {
                description1.setText(description);
                description1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String webURL = getArguments().getString(KEY7);
                        Log.d(TAG, "onClick: WEBSITE URL ==> " + webURL);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(webURL));
                        startActivity(intent);
                    }
                });
            }

            Log.d(TAG, "onCreateView: number ==> " + number);
            number1.setText(number);

            if (url != null || url != "") {
                Picasso picasso = new Picasso.Builder(this.getContext()).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        // Here we try https if the http image attempt failed
                        final String changedUrl = url.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .fit()
                                .error(R.drawable.missingimage)
                                .placeholder(R.drawable.placeholder)
                                .into(url1);
                        url1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String webURL = getArguments().getString(KEY7);
                                Log.d(TAG, "onClick: WEBSITE URL ==> " + webURL);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(webURL));
                                startActivity(intent);
                            }
                        });
                    }
                }).build();

                picasso.load(url).fit()
                        .error(R.drawable.missingimage)
                        .placeholder(R.drawable.placeholder)
                        .into(url1);
                url1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String webURL = getArguments().getString(KEY7);
                        Log.d(TAG, "onClick: WEBSITE URL ==> " + webURL);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(webURL));
                        startActivity(intent);
                    }
                });
            } else {
                Picasso.with(this.getContext())
                        .load(url).fit()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(url1);
                url1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String webURL = getArguments().getString(KEY7);
                        Log.d(TAG, "onClick: WEBSITE URL ==> " + webURL);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(webURL));
                        startActivity(intent);
                    }
                });
            }
        return view;
    }
}
