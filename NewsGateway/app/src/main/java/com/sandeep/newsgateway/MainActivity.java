package com.sandeep.newsgateway;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ARTICLE_DATA = "ARTICLE_DATA";
    static final String SOURCE_DATA = "SOURCE_DATA";
    static final String API_KEY = "6b01938b3a6b4a0390b5931ec199ed86";
    static final String NEWS_SOURCE_URL = "https://newsapi.org/v1/sources?language=en&country=us&apiKey=";
    static final String API_KEY_STRING = "&apiKey=";
    static final String NEWS_URL_BY_CATEGORY = "https://newsapi.org/v1/sources?language=en&country=us&category=";
    static final String NEWS_ARTICLE_URL = "https://newsapi.org/v1/articles?source=";
    int flag = 1;
    int flag2 = 1;

    private PageAdapter pageAdapter;
    private List<Fragment> fragmentList;
    private ViewPager viewPager;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> sourceNames = new ArrayList<>();
    private HashMap<String, Sources> sourcesHashMap = new HashMap<>();
    ArrayList<Sources> sourceObjectArrayList = new ArrayList<>();
    private ArrayAdapter drawerAdapter;
    private Articles articles;
    private NewsReceiver newsReciver;
    private Menu menu;
    private ArrayList<String> someCategoryList = new ArrayList<>();
    private String [] arr = new String[someCategoryList.size()];
    public int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: Entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        newsReciver = new NewsReceiver();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerAdapter = new ArrayAdapter(this , R.layout.drawer_list_item, items);

        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pos = position;
                        viewPager.setBackground(null);
                        selectedItem(position);

                        for(int i = 0; i < sourceObjectArrayList.size();i++) {
                            if(items.get(position).equals(sourceObjectArrayList.get(i).getName())) {

                                Intent intent = new Intent();
                                intent.putExtra(SOURCE_DATA, sourceObjectArrayList.get(i));
                                intent.setAction(ACTION_MSG_TO_SERVICE);
                                sendBroadcast(intent);
                                drawerLayout.closeDrawer(drawerList);
                            }
                        }
                    }
                }
        );

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException npe) {
            npe.printStackTrace();
        }
        getSupportActionBar().setHomeButtonEnabled(true);

        fragmentList = getFragments();

        // View Pager configuration
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);

        IntentFilter intentFilter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReciver, intentFilter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            new NewsSourceDownloader(this, "").execute();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void selectedItem(int position) {
        setTitle(items.get(position));
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "onCreateOptionsMenu: Entered");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Log.d(TAG, "onPrepareOptionsMenu: Entered");

        menu.clear();

        Log.d(TAG, "onPrepareOptionsMenu: Length of Array ==> "+arr.length);

        if(arr.length != 0) {
            Log.d(TAG, "onPrepareOptionsMenu: Array Length not 0");
            for (int i = 0; i < arr.length; i++) {
                menu.add(arr[i]);
            }
            return true;
        }else {
            Log.d(TAG, "onPrepareOptionsMenu: Array length is 0");
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Internet Connection check
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()) {
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
            new NewsSourceDownloader(this, item.toString()).execute();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReciver);
        super.onDestroy();
    }

//    @Override
//    protected void onResume() {
//        Log.d(TAG, "onResume: Entered");
//        super.onResume();
//        new NewsSourceDownloader(this, "").execute();
//
//    }

    public void setSources(ArrayList<Sources> sourcesArrayList, ArrayList<String> categoryArrayList) {

        Log.d(TAG, "setSources: Entered");

        Log.d(TAG, "setSources: Size of ArrayList<Sources> ==> " + sourcesArrayList.size());
        Log.d(TAG, "setSources: Size of ArrayList<String> ==> "+ categoryArrayList.size());

        sourceNames.clear();
        for(Sources source : sourcesArrayList) {

            sourceNames.add(source.getName());
            sourcesHashMap.put(source.getName(), source);
        }

//        Log.d(TAG, "setSources: Size ==> "+categoryList.size());
//
        if(flag2 == 1) {
            someCategoryList.add(0, "all");
            flag2++;
        }

        arr = someCategoryList.toArray(arr);

//        for(int i = 0;i<arr.length;i++) {
//            Log.d(TAG, "setSources: Array Values ==> "+arr[i]);
//        }

        if(flag == 1) {
            Log.d(TAG, "setSources: Entered If of flag");
            invalidateOptionsMenu();
            flag++;
        }

        Log.d(TAG, "setSources: Size of Source Names ==> "+sourceNames.size());
        Log.d(TAG, "setSources: value of flag is ==> "+flag);
        Log.d(TAG, "setSources: value of flag2 is ==> "+flag2);

        items.clear();
        items.addAll(sourceNames);
        drawerAdapter.notifyDataSetChanged();
    }

    //////////////////////////////////////////////
    ///////////// Broadcast Receiver /////////////
    //////////////////////////////////////////////

    class NewsReceiver extends BroadcastReceiver {

        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: Entered");

            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    if(intent.hasExtra(ARTICLE_DATA)) {

                        reDoFragments((ArrayList<Articles>) intent.getSerializableExtra(ARTICLE_DATA));
                    }
                    break;
            }
        }

        public void reDoFragments(ArrayList<Articles> data) {

//            Log.d(TAG, "reDoFragments: ===========================");
//            for(Articles a : data) {
//                Log.d(TAG, "reDoFragments: "+a.getTitle());
//            }
//            Log.d(TAG, "reDoFragments: ============================");
            
            setTitle(items.get(pos));

            for(int i=0; i< pageAdapter.getCount(); i++) {
                pageAdapter.notifyChangeInPosition(i);
            }

            fragmentList.clear();

            for(int j = 0; j < data.size(); j++) {

                fragmentList.add(MyFragment.newInstance(data.get(j).getTitle(),
                        data.get(j).getPublishDate(), data.get(j).getUrl(),
                        data.get(j).getAuthor(), data.get(j).getDescription(),
                        "Page "+(j+1)+" of "+data.size(),
                        data.get(j).getWebsiteUrl()));
            }

            pageAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(0);
        }
    }


    ///////////////////////////////////////////////
    /////////////////Fragments/////////////////////
    ///////////////////////////////////////////////

    private class PageAdapter extends FragmentPagerAdapter {

        private static final String TAG = "PageAdapter";

        private long baseId = 0;

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force re-creation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }


    //////////////////////////////////////////////////////////////////
    ////////////////////// NewsSourceDownloader //////////////////////
    //////////////////////////////////////////////////////////////////

    private class NewsSourceDownloader extends AsyncTask<String, Void, String> {

        private static final String TAG = "NewsSourceDownloader";

        private MainActivity mainAct;
        private String category;

        public NewsSourceDownloader(MainActivity mainAct, String category) {
            this.mainAct = mainAct;
            this.category = category;
        }

        @Override
        protected String doInBackground(String... params) {

            int indent = 2;

            Log.d(TAG, "doInBackground: Entered");
            String newURL;
            if(category.equals("all") || category.equals("")) {
                newURL = NEWS_SOURCE_URL + API_KEY;
                Log.d(TAG, "doInBackground: No Category | All Selected ==> "+newURL);
            }else {
                newURL = NEWS_URL_BY_CATEGORY +category+API_KEY_STRING+API_KEY;
                Log.d(TAG, "doInBackground: Categorized URL ==> "+newURL);
            }

            Uri officialUri = Uri.parse(newURL);
            String urlToUse = officialUri.toString();
            Log.d(TAG, "doInBackground: =============================================");
            Log.d(TAG, "doInBackground: The Main URL to be used ==> "+urlToUse);
            Log.d(TAG, "doInBackground: =============================================");

            InputStream inputStream;
            BufferedReader bufferedReader;

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

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(TAG, "doInBackground: After call to BufferedReader");

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

//                System.out.println("Built String: \n"+sb.toString());

                inputStream.close();
                bufferedReader.close();

                return sb.toString();

            }catch(IOException e) {
                Log.d(TAG, "doInBackground: IOException raised ... Inside catch");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            int indent = 2;

            Log.d(TAG, "onPostExecute: Entered");
//            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute: \n"+s);

            try {
                if(s.isEmpty()) {
                    Log.d(TAG, "onPostExecute: String empty");
                    Toast.makeText(MainActivity.this, R.string.stringEmpty, Toast.LENGTH_LONG).show();
                }else if(s == null) {
                    Log.d(TAG, "onPostExecute: S is null");
                    Toast.makeText(MainActivity.this, R.string.stringNull, Toast.LENGTH_LONG).show();
                }else if(!s.isEmpty() && s != null) {

                    Log.d(TAG, "onPostExecute: If s not empty and s not null");

                    // Main object which holds the entire data
                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray1 = jsonObject.getJSONArray("sources");

                    ////////////////////////////////////////
                    // Displaying purposes
//                    String sources = jsonObject.getString("sources");
//                    String sou = new JSONArray(sources).toString(indent);
//                    Log.d(TAG, "onPostExecute: "+sou);
                    ////////////////////////////////////////

                    sourceObjectArrayList.clear();

                    for(int i=0;i<jsonArray1.length();i++) {
                        JSONObject jsonObj1 = jsonArray1.getJSONObject(i);
                        String id = jsonObj1.getString("id");
                        String name = jsonObj1.getString("name");
                        String url = jsonObj1.getString("url");
                        String category = jsonObj1.getString("category");

//                        Log.d(TAG, "onPostExecute: ID ==> "+id);
//                        Log.d(TAG, "onPostExecute: NAME ==> "+name);
//                        Log.d(TAG, "onPostExecute: URL ==> "+url);
//                        Log.d(TAG, "onPostExecute: CATEGORY ==> "+category);
//                        Log.d(TAG, "onPostExecute: ====================================");

                        Sources sourceObj = new Sources(id, name, url, category);
                        sourceObjectArrayList.add(sourceObj);

                        if(!containsString(someCategoryList, category)) {
                            someCategoryList.add(category);
                        }else if(containsString(someCategoryList, category)) {
                            Log.d(TAG, "onPostExecute: String Already Exists");
                        }
                    }
                    setSources(sourceObjectArrayList, someCategoryList);
                    Log.d(TAG, "onPostExecute: "+sourceObjectArrayList.size());
                }
            }catch(Exception e) {
                Log.d(TAG, "onPostExecute: Inside Catch...");
                e.printStackTrace();
            }
        }

        private boolean containsString(List<String> catList, String categoryString) {
            for(String s : catList) {
                if(s.equals(categoryString)) {
                    return true;
                }
            }
            return false;
        }
    }
}
