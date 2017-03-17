package com.sandeep.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private StockAdapter stockAdapter;
    private DatabaseHandler databaseHandler;
    private static String marketWatchURL = "http://www.marketwatch.com/investing/stock/";
    private static String stockNameURL = "http://stocksearchapi.com/api";
    private static String financeURL = "http://finance.google.com/finance/info";
    private static String api_key = "514a19854cd893ac2a86139e381c3678f2b2aee6";
    private static final String TAG = "MainActivity";
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // DB calls
        databaseHandler = new DatabaseHandler(this);

        // SwipeRefresh Calls
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doSwipeRefresh();
            }
        });

        // Loading the data from the database
        ArrayList<Stock> aList = databaseHandler.loadStock();

        // Internet Connection check
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            Toast.makeText(this, "You ARE Connected to the Internet!", Toast.LENGTH_LONG).show();

            for(Stock s1 : aList) {
                new StockDataDownloader().execute(s1.getStockSymbol(),s1.getCompanyName());
            }

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();

            for(Stock s:aList) {
                stockList.add(s);
                stockAdapter.notifyDataSetChanged();
            }
        }
    }

    private void doSwipeRefresh() {
        Log.d(TAG, "doSwipeRefresh: Entered");
//        Toast.makeText(MainActivity.this, "On Refresh Called", Toast.LENGTH_LONG).show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        List<Stock> copyList = new ArrayList<>();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(TAG, "doSwipeRefresh: Inside If");

            copyList.addAll(stockList);
            for(Stock s1:copyList) {
                Log.d(TAG, "doSwipeRefresh: Inside for");
                System.out.println(s1.getCompanyName()+"\n"+ s1.getStockSymbol()+"\n"+s1.getPriceChange()+"\n"+s1.getPrice()+"\n"+s1.getPercentage());
            }
            Log.d(TAG, "doSwipeRefresh: Before Clearing Contents");
            stockList.clear();
            Log.d(TAG, "doSwipeRefresh: After Clearing Contents");
            for(Stock s1:copyList) {
                Log.d(TAG, "doSwipeRefresh: Inside for");
                System.out.println(s1.getCompanyName()+"\n"+ s1.getStockSymbol()+"\n"+s1.getPriceChange()+"\n"+s1.getPrice()+"\n"+s1.getPercentage());
            }
            Log.d(TAG, "doSwipeRefresh: Before 2nd For");
            for(Stock s: copyList) {
                Log.d(TAG, "doSwipeRefresh: Inside 2nd For");
                if (copyList.size() == 0) {
                    swiper.setRefreshing(false);
                }else {
                    Log.d(TAG, "doSwipeRefresh: Inside For -> Else");
                    new StockDataDownloader().execute(s.getStockSymbol(), s.getCompanyName());
                }
            }
        }else {
            Log.d(TAG, "doSwipeRefresh: Inside Else");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        swiper.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();

                if (netInfo != null && netInfo.isConnectedOrConnecting()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText et = new EditText(MainActivity.this);

                    et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);

                    builder.setTitle(R.string.selection);
                    builder.setMessage(R.string.selectionMessage);
                    builder.setPositiveButton(R.string.positiveSelection, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String typedText = et.getText().toString();
                            new StockNameDownLoader().execute(typedText);
                        }
                    });
                    builder.setNegativeButton(R.string.negativeSelection, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do Nothing
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.noConnection);
                    builder.setMessage(R.string.noConnectionMessage);
                    builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }

            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock stock = stockList.get(pos);

//        Toast.makeText(this, stock.getCompanyName(), Toast.LENGTH_LONG).show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            String url = marketWatchURL + stock.getStockSymbol();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noConnection);
            builder.setMessage(R.string.noConnectionMessage);
            builder.setIcon(R.drawable.ic_signal_cellular_off_black_48dp);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onLongClick(View v) {

        final int pos = recyclerView.getChildLayoutPosition(v);
        Stock stock = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.deleteText);
        builder.setMessage(getString(R.string.deleteMessage)+" "+stock.getStockSymbol()+"?");

        builder.setIcon(R.drawable.ic_delete_black_48dp);

        builder.setPositiveButton(R.string.deletePositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.deleteStock(stockList.get(pos).getStockSymbol());
                stockList.remove(pos);
                stockAdapter.notifyDataSetChanged();
                databaseHandler.dumpLog();
            }
        });

        builder.setNegativeButton(R.string.deleteNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void addToStock(Stock stock) {

        Stock stock1 = new Stock(stock.getStockSymbol(), stock.getCompanyName());

        Log.d(TAG, "addToStock: Entered");
        try {
            Log.d(TAG, "addToStock: Entered Try");
            if (stock.getStockSymbol() == null) {
                Log.d(TAG, "addToStock: Entered First If");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notFoundForStock);
                builder.setMessage(R.string.noDataFoundForStockMessage);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else if(stockList.size() == 0) {
                Log.d(TAG, "addToStock: Inside 2nd Else If");
                stockList.add(stock);
                Collections.sort(stockList, new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                    }
                });
                databaseHandler.addStock(stock1);
                stockAdapter.notifyDataSetChanged();
                databaseHandler.dumpLog();
            }else if(containsString(stockList, stock.getStockSymbol())) {
                Log.d(TAG, "addToStock: Else If entered - duplicate element");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.duplicateTitle);
                builder.setIcon(R.drawable.ic_action_name);
                builder.setMessage(getString(R.string.duplicateMessage)+" "+stock.getStockSymbol()+" already in the list");
                AlertDialog dialog = builder.create();
                dialog.show();
            }else if(!containsString(stockList, stock.getStockSymbol())) {
                Log.d(TAG, "addToStock: Inside else if - If not equal");
                stockList.add(stock);
                Log.d(TAG, "addToStock: Before Sorting***************************************************");
                for(Stock s2 : stockList) {
                    System.out.println("Stock Symbol: "+s2.getStockSymbol());
                }
                Collections.sort(stockList, new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                    }
                });
                Log.d(TAG, "addToStock: After Sorting***************************************************");
                for(Stock s2 : stockList) {
                    System.out.println("Stock Symbol: "+s2.getStockSymbol());
                }
                Stock stock2 = new Stock(stock.getStockSymbol(), stock.getCompanyName());
                databaseHandler.addStock(stock2);
                stockAdapter.notifyDataSetChanged();
                databaseHandler.dumpLog();
            }
        }catch(NullPointerException npe) {
            Log.d(TAG, "addToStock: in addToStock - NullPointerException");
        }catch (SQLiteConstraintException e) {
            Log.d(TAG, "addToStock: SQLiteException block");
        }catch(Exception e1) {
            Log.d(TAG, "addToStock: Normal Exception");
        }
    }

    private boolean containsString(List<Stock> sList, String symbol) {
        for(Stock s : sList) {
            if(s.getStockSymbol().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public void newSymbol(String symbol, String company) {
        Log.d(TAG, "newSymbol: ");
        System.out.println("StockSymbol:               "+symbol);
        System.out.println("Company Name:              "+company);

        new StockDataDownloader().execute(symbol, company);
    }






    ////////////////////////////////////////////////////
    /////////// StockNameDownLoader Async Task /////////
    ////////////////////////////////////////////////////

    private class StockNameDownLoader extends AsyncTask<String, Void, List<String>> {

        private MainActivity mainActivity;
        private static final String TAG = "StockNameLoader";


        @Override
        protected List<String> doInBackground(String... params) {

            List<String> symbolAndCompany = new ArrayList<>();

            String textInEditText = params[0];
            System.out.println(textInEditText);
            String newURL = stockNameURL + "?search_text=" + textInEditText + "&api_key=" + api_key;
            Uri stockURI = Uri.parse(newURL);
            String urlToUse = stockURI.toString();
            Log.d(TAG, "doInBackground: " + urlToUse);
            InputStream inputStream;
            BufferedReader reader;

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");

                Log.d(TAG, "doInBackground: " + "before call to InputStream");
                inputStream = httpConnection.getInputStream();
                Log.d(TAG, "doInBackground: After call to InputStream");

                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(TAG, "doInBackground: After call to BufferedReader");


                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                System.out.println(sb.toString());

                JSONArray jsonArray = new JSONArray(sb.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String company_symbol = jsonObject.getString("company_symbol");
                    String company_name = jsonObject.getString("company_name");

                    symbolAndCompany.add(company_symbol + "-" + company_name);
                }
                inputStream.close();
                reader.close();
                return symbolAndCompany;
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: ", e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<String> strings) {

            Log.d(TAG, "onPostExecute: ");

            String[] stringList = {};
            try {
                if (strings == null) {
                    Log.d(TAG, "onPostExecute: Inside The If statement");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.noStockTitle);
                    builder.setMessage(R.string.noStockMessage);
                    builder.setIcon(R.drawable.ic_action_name);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


                final String[] arrayOfStrings = strings.toArray(new String[strings.size()]);
                if (arrayOfStrings.length == 0) {
                    Log.d(TAG, "onPostExecute: ");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.noStockTitle);
                    builder.setMessage(R.string.noStockMessage);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (arrayOfStrings.length == 1) {

                    Log.d(TAG, "onPostExecute: In first else if");

                    for (int i = 0; i < arrayOfStrings.length; i++) {
                        stringList = arrayOfStrings[i].split("-");
                    }

                    System.out.println("Symbol: "+stringList[0]+" Company Name: "+stringList[1]);


                    newSymbol(stringList[0], stringList[1]);
                } else if (arrayOfStrings.length > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.multipleSelection);


                    builder.setItems(arrayOfStrings, new DialogInterface.OnClickListener() {
                        String [] newArray = {};
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println(arrayOfStrings[which]);
                            newArray = arrayOfStrings[which].split("-");
                            newSymbol(newArray[0], newArray[1]);
                        }
                    });

                    builder.setNegativeButton(R.string.cancelMultipleSelection, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } catch (NullPointerException npe) {
                Log.d(TAG, "onPostExecute: NullPointerException");
            }catch (Exception e) {
                Log.d(TAG, "onPostExecute: General Exception");
            }
        }
    }



    ////////////////////////////////////////////
    //////// StockDataDownloader called ////////
    ////////////////////////////////////////////

    private class StockDataDownloader extends AsyncTask<String, Void, String> {

        String stockSymbol;
        String company_name;

        private static final String TAG = "StockDataDownloader";
        @Override
        protected String doInBackground(String... params) {

            stockSymbol = params[0];
            company_name = params[1];

            String newURI = financeURL+"?client=ig"+"&q="+params[0];
            Uri financeURI = Uri.parse(newURI);
            String urlToUse = financeURI.toString();

            Log.d(TAG, "doInBackground: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                System.out.println("Text from the finance:    "+sb.toString());

                StringBuilder sb1 = sb.replace(0,3, "");

                System.out.println("Text from the finance:    "+sb1.toString());
                Log.d(TAG, "doInBackground: Before the return");
                return sb1.toString();
            }catch (Exception e) {
                Log.d(TAG, "doInBackground: Exception");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d(TAG, "onPostExecute: ");
            System.out.println("The new string from do in background:   "+s);

            if(s == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.noDataForSelectedStockTitle);
                builder.setMessage(R.string.noDataForSelectedStockMessage);
                builder.setIcon(R.drawable.ic_action_name);

                AlertDialog dialog = builder.create();
                dialog.show();
            }else {

                try {
                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String ticker = jsonObject.getString("t");
                        Log.d(TAG, "onPostExecute: Before comparison");
                        Log.d(TAG, "onPostExecute: Enetered If Statement");
                        Double lastTradePrice = jsonObject.getDouble("l");
                        Double priceChangeAmount = jsonObject.getDouble("c");
                        Double priceChangePercentage = jsonObject.getDouble("cp");

                        System.out.println(ticker + " " + lastTradePrice + " " + priceChangeAmount + " " + priceChangePercentage);
                        System.out.println("Symbol: " + stockSymbol + "\n" + "Company: " + company_name);

                        stock = new Stock(stockSymbol, company_name, lastTradePrice, priceChangeAmount, priceChangePercentage);
                        addToStock(stock);
                    }
                } catch (Exception e1) {
                    Log.d(TAG, "onPostExecute: In the Exception Block");
                }
            }
        }
    }
}
