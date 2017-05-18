package com.sandeep.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerView.OnClickListener, View.OnLongClickListener{

    private RecyclerView recyclerView;
    private ArrayList<Official> officialList = new ArrayList<>();
    private OfficialAdapter officialAdapter;
    private static final String TAG = "MainActivity";
    private Locator locator;
    private static final String GoogleAPIUrl = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String address = "&address=";
    private static final String api_key = "AIzaSyBwddpkU-Cf_4TbD0TrQCmdx3SUxTB6Z-0";
    private TextView currentLocation;
    private String locationToBeDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Entered");
        setContentView(R.layout.new_activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentLocation = (TextView) findViewById(R.id.locationText);

        // Internet Connection check
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()) {
            locator = new Locator(this);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();

            currentLocation.setText(R.string.noDataForLocation);
        }

    }

    /////// doLocationWork() //////
    ////// Called from onRequestPermissionResult() //////
    public void doLocationWork(double latitude, double longitude) {

        Log.d(TAG, "doAddress: Lat: " + latitude + ", Lon: " + longitude);

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            Log.d(TAG, "doAddress: Getting address now");

            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            StringBuilder sb = new StringBuilder();

            Address addr = addresses.get(0);
            String zip = addr.getPostalCode();

            Toast.makeText(this, zip, Toast.LENGTH_LONG).show();
            new CivicInfoDownloader().execute(zip);
        }catch (Exception e) {
            Toast.makeText(this, "Address cannot be acquired from the the provided "+latitude+" and "+longitude, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //////// Called when a user is trying to access the location service from Google /////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if(requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
//                        locator.setUpLocationManager();
                        locator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official official = officialList.get(pos);

        Log.d(TAG, "onClick: party ==> "+official.getParty());

        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("office", official.getOffice());
        intent.putExtra("name", official.getName());
        intent.putExtra("party", official.getParty());
        intent.putExtra("officeAddress", official.getOfficeAddress());
        intent.putExtra("phone", official.getPhone());
        intent.putExtra("email", official.getEmailaddress());
        intent.putExtra("website", official.getWebsite());
        intent.putExtra("twitterUrl", official.getTwitterLink());
        intent.putExtra("facebookUrl", official.getFacebookLink());
        intent.putExtra("googlePlusUrl", official.getGooglePlusLink());
        intent.putExtra("youtubeUrl", official.getYoutubeLink());
        intent.putExtra("imageUrl", official.getImage());
        intent.putExtra("settingLoc", locationToBeDisplayed);

        startActivity(intent);

//        Toast.makeText(this, official.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {

        Log.d(TAG, "onLongClick: Entered");

        onClick(v);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.locationId:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(MainActivity.this);

//                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);

                builder.setTitle(R.string.zip);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(MainActivity.this, et.getText().toString(), Toast.LENGTH_LONG).show();
                        String typedText = et.getText().toString();
                        officialList.clear();
                        new CivicInfoDownloader().execute(typedText);
                    }
                });
                builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing just Exit
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.helpId:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                super.onOptionsItemSelected(item);
                    return false;
        }
    }

    public void setOfficialList(Object [] results) {

//        Log.d(TAG, "setOfficialList: Entered");

        locationToBeDisplayed = (String) results[0];

        if(results == null) {
            currentLocation.setText(R.string.noDataForLocation);
            officialList.clear();
            officialAdapter.notifyDataSetChanged();
        }else {
            currentLocation.setText((String) results[0]);
            officialList.addAll((ArrayList<Official>) results[1]);
            officialAdapter.notifyDataSetChanged();

//            Log.d(TAG, "setOfficialList: ========================");
//            Log.d(TAG, "setOfficialList: After notify");
//            Log.d(TAG, "setOfficialList: ========================");
//            for(Official o : officialList) {
//                Log.d(TAG, "setOfficialList: "+o.getOffice());
//                Log.d(TAG, "setOfficialList: "+o.getName());
//                Log.d(TAG, "setOfficialList: "+o.getParty());
//                Log.d(TAG, "setOfficialList: "+o.getPhone());
//                Log.d(TAG, "setOfficialList: "+o.getWebsite());
//                Log.d(TAG, "setOfficialList: "+o.getEmailaddress());
//                Log.d(TAG, "setOfficialList: =======================================================");
//            }


        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        locator.shutDown();
        super.onDestroy();
    }

    /////////////////////////////////////////////////
    ////////////// CivicInfoDownloader //////////////
    /////////////////////////////////////////////////
    public class CivicInfoDownloader extends AsyncTask<String, Void, String> {
        private static final String TAG = "CivicInfoDownloader";


        @Override
        protected String doInBackground(String... params) {

            Log.d(TAG, "doInBackground: Entered");

            String zipCode = params[0];
            Log.d(TAG, "doInBackground: "+zipCode);
            String newURL = GoogleAPIUrl + api_key + address + zipCode;
            Uri officialUri = Uri.parse(newURL);
            String urlToUse = officialUri.toString();
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

                System.out.println("Built String: \n"+sb.toString());

                inputStream.close();
                reader.close();

                return sb.toString();
            }catch(IOException e) {
                Log.d(TAG, "doInBackground: IOException raised ... Inside catch");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d(TAG, "onPostExecute: Entered");
//            super.onPostExecute(s);

            Log.d(TAG, "onPostExecute: Returned String from doInBackground() \n"+s);

            try {
                if (s.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No Data available for the specified location", Toast.LENGTH_LONG).show();
                    setOfficialList(null);
                } else if (s == null) {
                    Toast.makeText(MainActivity.this, "Civic Info Service is unavailable", Toast.LENGTH_LONG).show();
                    setOfficialList(null);
                }
                else if(!s.isEmpty() && s != null) {

                    Log.d(TAG, "onPostExecute: Entered Else if");

                    int indent = 2;

                    // Main JSONObject which holds the entire data
                    JSONObject jsonObject = new JSONObject(s);

                    // Fetching data from "normalizedInput" from the Main JSONObject
                    String normalizedInput = jsonObject.getString("normalizedInput");

                    // Fetching city, state and zip fom "normalizedInput"
                    JSONObject obj1 = new JSONObject(normalizedInput);
                    String city = obj1.getString("city");
                    String state = obj1.getString("state");
                    String zip = obj1.getString("zip");

                    String location = city+", "+state+" "+zip;
                    Log.d(TAG, "onPostExecute: "+location);

//                    Log.d(TAG, "onPostExecute: "+city+","+state+" "+zip);

                    ///// Displaying purposes /////
                    String offices = jsonObject.getString("offices");
                    String off = new JSONArray(offices).toString(indent);

                    // Creating JSONArray for "offices"
                    JSONArray arr1 = jsonObject.getJSONArray("offices");
//                    Log.d(TAG, "onPostExecute: Length of offices array is: "+arr1.length());

                    // Creating JSON Array for "officials"
                    JSONArray arr2 = jsonObject.getJSONArray("officials");
//                    Log.d(TAG, "onPostExecute: Length of officials array is: "+arr2.length());

                    for(int i=0;i<arr1.length();i++) {
                        JSONObject obj2 = (JSONObject) arr1.get(i);

                        // Extracting office
                        String officeName;
                        if(obj2.has("name")) {
                            officeName = obj2.getString("name");
                        }else {
                            officeName = "No Data Provided";
                        }


                        // Array of indices`
                        JSONArray indices = obj2.getJSONArray("officialIndices");
//                        Log.d(TAG, "onPostExecute: "+indices);
//                        Log.d(TAG, "onPostExecute: "+officeName);

                        for(int j=0;j<indices.length();j++) {

                            JSONObject obj3 = (JSONObject) arr2.get(indices.getInt(j));

                            // Extracting name
                            String officialName;
                            if(obj3.has("name")) {
                                officialName = obj3.getString("name");
//                                Log.d(TAG, "onPostExecute: " +"Name ==> " +officialName);
                            }else {
                                officialName = "No Data Provided";
//                                Log.d(TAG, "onPostExecute: "+"Name ==> No data Provided");
                            }

                            // Extracting address
                            String homeAddress;
                            if(obj3.has("address")) {
                                JSONArray address = obj3.getJSONArray("address");

                                JSONObject obj4 = address.getJSONObject(0);
                                String line1 = obj4.getString("line1");
                                String city1 = obj4.getString("city");
                                String state1 = obj4.getString("state");
                                String zip1 = obj4.getString("zip");

                                String line3 = "";
                                String line2 = "";
                                if (obj4.has("line2") && obj4.has("line3")) {
                                    line2 = obj4.getString("line2");
                                    line3 = obj4.getString("line3");
                                    homeAddress = line1 + "\n" + line3 + "\n" + line2 + "\n" + city1 + ", " + state1 + " " + zip1;
//                                    Log.d(TAG, "onPostExecute: " + homeAddress);
                                } else if (obj4.has("line2")) {
                                    line2 = obj4.getString("line2");
                                    homeAddress = line1 + "\n" + line2 + "\n" + city1 + ", " + state1 + " " + zip1;
//                                    Log.d(TAG, "onPostExecute: " + homeAddress);
                                } else {
                                    homeAddress = line1 +"\n"+ city1 + ", " + state1 + " " + zip1;
//                                    Log.d(TAG, "onPostExecute: " + homeAddress);
                                }
                            }else {
                                homeAddress = "No Data Provided";
//                                Log.d(TAG, "onPostExecute: "+"Address ==> No data Provided");
                            }

                            // Extracting party
                            String party;
                            if(obj3.has("party")) {
                                party = obj3.getString("party");
//                                Log.d(TAG, "onPostExecute: "+"Party ==> "+party);
                            }else {
                                party = "Unknown";
//                                Log.d(TAG, "onPostExecute: "+"Party ==> No data Provided");
                            }

                            // Extracting phone
                            String phoneNum;
                            if(obj3.has("phones")) {
                                JSONArray phones = obj3.getJSONArray("phones");
                                phoneNum = phones.getString(0);
//                                Log.d(TAG, "onPostExecute: "+"Phones ==> "+phoneNum);
                            }else {
                                phoneNum = "No Data Provided";
//                                Log.d(TAG, "onPostExecute: "+"Phones ==> No data Provided");
                            }

                            // Extracting website URL
                            String websiteUrl;
                            if(obj3.has("urls")) {
                                JSONArray urls = obj3.getJSONArray("urls");
                                websiteUrl = urls.getString(0);
//                                Log.d(TAG, "onPostExecute: "+"Website URL ==> "+websiteUrl);
                            }else {
                                websiteUrl = "No Data Provided";
//                                Log.d(TAG, "onPostExecute: "+"Website URL ==> No data Provided");
                            }

                            // Extracting email
                            String emailAddress;
                            if(obj3.has("emails")) {
                                JSONArray emails = obj3.getJSONArray("emails");
                                emailAddress = emails.getString(0);
//                                Log.d(TAG, "onPostExecute: "+"Emails ==> "+emailAddress);
                            }else {
                                emailAddress = "No Data Provided";
//                                Log.d(TAG, "onPostExecute: "+"Emails ==> No data Provided");
                            }

                            // Extracting photoURL
                            String photoURL;
                            if(obj3.has("photoUrl")) {
                                photoURL = obj3.getString("photoUrl");
//                                Log.d(TAG, "onPostExecute: "+"Photo URL ==> "+photoURL);
                            }else {
                                photoURL = null;
//                                Log.d(TAG, "onPostExecute: "+"Photo URL ==> No data Provided");
                            }




                            // Extracting channels
                            String googlePlusId = "";
                            String facebookId = "";
                            String twitterId = "";
                            String youtubeId = "";

                            if(obj3.has("channels")) {
                                JSONArray channels = obj3.getJSONArray("channels");

                                for(int k=0;k<channels.length();k++) {

                                    JSONObject obj5 = channels.getJSONObject(k);

                                    if(obj5.has("type")) {
                                        String type = obj5.getString("type");
                                        String id = obj5.getString("id");
//                                        Log.d(TAG, "onPostExecute: "+type);
//                                        Log.d(TAG, "onPostExecute: "+id);

                                        switch (type) {
                                            case "GooglePlus":
                                                googlePlusId = obj5.getString("id");
                                                break;
                                            case "Facebook":
                                                facebookId = obj5.getString("id");
                                                break;
                                            case "Twitter":
                                                twitterId = obj5.getString("id");
                                                break;
                                            case "YouTube":
                                                youtubeId = obj5.getString("id");
                                                break;
                                        }

                                    }else {
//                                        Log.d(TAG, "onPostExecute: No type key present");
                                    }
                                }

                                if(googlePlusId == "") {
                                    googlePlusId = "No Data Provided";
//                                    Log.d(TAG, "onPostExecute: "+"No Data Provided for GooglePlus");
                                }

                                if(youtubeId == "") {
                                    youtubeId = "No Data Provided";
//                                    Log.d(TAG, "onPostExecute: "+"No Data Provided for YouTube");
                                }

                                if(twitterId == "") {
                                    twitterId = "No Data Provided";
//                                    Log.d(TAG, "onPostExecute: "+"No Data Provided for Twitter");
                                }

                                if(facebookId == "") {
                                    facebookId = "No Data Provided";
//                                    Log.d(TAG, "onPostExecute: "+"No Data Provided for Facebook");
                                }

//                                Log.d(TAG, "onPostExecute: GooglePlusId ==> "+googlePlusId);
//                                Log.d(TAG, "onPostExecute: YoutubeId ==> "+youtubeId);
//                                Log.d(TAG, "onPostExecute: FacebookId ==> "+facebookId);
//                                Log.d(TAG, "onPostExecute: TwitterId ==> "+twitterId);

                            }else {
//                                Log.d(TAG, "onPostExecute: "+"Channels ==> No data Provided");
                            }

//                            Log.d(TAG, "onPostExecute: =========================================");

                            Official official = new Official(officeName, officialName, party, homeAddress, phoneNum, emailAddress, websiteUrl, facebookId, twitterId, googlePlusId, youtubeId, photoURL);
                            List<Official> otherList = new ArrayList<>();
                            otherList.add(official);

                            Object [] objArray = new Object[] {location, otherList};
                            setOfficialList(objArray);

                            }
                        }
                    }


            }catch (NullPointerException npe) {
                Log.d(TAG, "onPostExecute: Null Pointer Exception");
            }catch (Exception e) {
                Log.d(TAG, "onPostExecute: General Exception");
            }

        }



    }
}
