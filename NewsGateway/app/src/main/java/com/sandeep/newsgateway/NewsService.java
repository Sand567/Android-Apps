package com.sandeep.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.sandeep.newsgateway.MainActivity.ACTION_MSG_TO_SERVICE;
import static com.sandeep.newsgateway.MainActivity.ACTION_NEWS_STORY;
import static com.sandeep.newsgateway.MainActivity.API_KEY;
import static com.sandeep.newsgateway.MainActivity.API_KEY_STRING;
import static com.sandeep.newsgateway.MainActivity.ARTICLE_DATA;
import static com.sandeep.newsgateway.MainActivity.NEWS_ARTICLE_URL;
import static com.sandeep.newsgateway.MainActivity.SOURCE_DATA;

public class NewsService extends Service {

    private static final String TAG = "NewsService";

    private ServiceReceiver serviceReceiver;
    private boolean isRunning = true;
    private ArrayList<Articles> storyList = new ArrayList<>();
    public Sources sources;


    public NewsService() {

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        unregisterReceiver(serviceReceiver);
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: Entered Service");


        serviceReceiver = new ServiceReceiver();

        IntentFilter intentFilter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver,intentFilter);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(isRunning) {
                    try {
                        while(storyList.size() == 0) {
                            Thread.sleep(250);
                        }

//                        Bundle extra = new Bundle();
//                        extra.putSerializable(SOURCE_DATA, storyList);

                        Intent intent = new Intent();
                        intent.setAction(ACTION_NEWS_STORY);
                        intent.putExtra(ARTICLE_DATA, storyList);
                        sendBroadcast(intent);

                        storyList.clear();

                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }

        }).start();

        return Service.START_STICKY ;
    }

    public void setArticles(ArrayList<Articles> storyList1) {

        this.storyList.clear();
        this.storyList.addAll(storyList1);
    }

    class ServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(intent.getAction()) {
                case ACTION_MSG_TO_SERVICE:
                    if(intent.hasExtra(SOURCE_DATA)) {
                        sources = (Sources) intent.getSerializableExtra(SOURCE_DATA);
                        String id = sources.getId();
                        new NewsArticleDownloader(NewsService.this, id).execute();
                    }
            }
        }
    }




    private class NewsArticleDownloader extends AsyncTask<String, Void, String> {

        private NewsService newsService;
        private String sourceName;
        Articles articles;

        public NewsArticleDownloader(NewsService newsService, String sourceName) {
            this.newsService = newsService;
            this.sourceName = sourceName;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.d(TAG, "doInBackground: Entered");
            String newURL = NEWS_ARTICLE_URL + sourceName + API_KEY_STRING + API_KEY;
            Uri articleUri = Uri.parse(newURL);
            String urlToUse = articleUri.toString();
            Log.d(TAG, "doInBackground: "+urlToUse);
            InputStream inputStream;
            BufferedReader reader;
            StringBuilder sb = new StringBuilder();
            try {
                Log.d(TAG, "doInBackground: Inside try block");
                URL url = new URL(urlToUse);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");

                if(httpConnection.getResponseCode() == 400) {
                    return sb.toString();
                }

                Log.d(TAG, "doInBackground: " + "before call to InputStream");
                inputStream = httpConnection.getInputStream();
                Log.d(TAG, "doInBackground: After call to InputStream");

                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(TAG, "doInBackground: After call to BufferedReader");

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

//                System.out.println("Built String: \n"+sb.toString());

                inputStream.close();
                reader.close();

                return sb.toString();

            }catch(IOException io) {
                Log.d(TAG, "doInBackground: Inside Catch");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            int indent = 2;
            Log.d(TAG, "onPostExecute: Entered");

            Log.d(TAG, "onPostExecute: "+s);

            try {
                if(s.isEmpty()) {
                    Log.d(TAG, "onPostExecute: String empty");
                    Toast.makeText(NewsService.this, R.string.stringEmpty, Toast.LENGTH_LONG).show();
                }else if(s == null) {
                    Log.d(TAG, "onPostExecute: S is null");
                    Toast.makeText(NewsService.this, R.string.stringNull, Toast.LENGTH_LONG).show();
                }else if(!s.isEmpty() && s != null) {

                    Log.d(TAG, "onPostExecute: If s not empty and s not null");

                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray1 = jsonObject.getJSONArray("articles");

                    ////////////////////////////////////////
                    // Displaying purposes
                    String sources = jsonObject.getString("articles");
                    String sou = new JSONArray(sources).toString(indent);
                    Log.d(TAG, "onPostExecute: "+sou);
                    ////////////////////////////////////////

                    ArrayList<Articles> articleArrayList = new ArrayList<>();

                    for(int i=0;i<jsonArray1.length();i++) {

                        JSONObject jsonObj1 = jsonArray1.getJSONObject(i);

                        String author = jsonObj1.getString("author");
                        String title = jsonObj1.getString("title");
                        String description = jsonObj1.getString("description");
                        String urlToImage = jsonObj1.getString("urlToImage");
                        String publishedAt = jsonObj1.getString("publishedAt");
                        String websiteURL = jsonObj1.getString("url");

                        if(author == null || author == "") {
                            author = "";
                        }

                        if(title == null || title == "") {
                            title = "";
                        }

                        if(description == null || description == "") {
                            description = "";
                        }

                        if(publishedAt == null || publishedAt == "") {
                            publishedAt = "";
                        }

                        articles = new Articles(author, title, description, urlToImage, publishedAt, websiteURL);

                        articleArrayList.add(articles);
                    }

                    Log.d(TAG, "onPostExecute: size of article array list"+articleArrayList.size());
                    
                    for(Articles article : articleArrayList) {
                        Log.d(TAG, "onPostExecute: Author ==> "+ article.getAuthor());
                        Log.d(TAG, "onPostExecute: Title ==> "+article.getTitle());
                        Log.d(TAG, "onPostExecute: Description ==> "+article.getDescription());
                        Log.d(TAG, "onPostExecute: URL ==> "+article.getUrl());
                        Log.d(TAG, "onPostExecute: PublishDate ==> "+article.getPublishDate());
                        Log.d(TAG, "onPostExecute: ===============================================");
                    }

                    setArticles(articleArrayList);
                }
            }catch(Exception e) {
                Log.d(TAG, "onPostExecute: Inside Catch");
                e.printStackTrace();
            }
        }
    }
}
