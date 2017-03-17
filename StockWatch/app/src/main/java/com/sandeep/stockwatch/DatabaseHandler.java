package com.sandeep.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sandeep on 06-03-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static final String PRICE = "Price";
    private static final String PRICE_CHANGE = "PriceChange";
    private static final String PERCENTAGE = "Percentage";

//    private static final String SQL_CREATE_TABLE =
//                            "CREATE TABLE "+TABLE_NAME+" ("+
//                                    SYMBOL+" TEXT not null unique,"+
//                                    COMPANY+" TEXT not null,"+
//                                    PRICE+" DOUBLE not null,"+
//                                    PRICE_CHANGE+" DOUBLE not null,"+
//                                    PERCENTAGE+" DOUBLE not null)";

    private static final String SQL_CREATE_TABLE =
                    "CREATE TABLE "+TABLE_NAME+" ("+
                            SYMBOL+" TEXT not null unique,"+
                            COMPANY+" TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: Constructor Entered");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Making new DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addStock(Stock stock) {

        try {
            Log.d(TAG, "addStock: Adding " + stock.getStockSymbol());

            ContentValues contentValues = new ContentValues();
            contentValues.put(SYMBOL, stock.getStockSymbol());
            contentValues.put(COMPANY, stock.getCompanyName());
//            contentValues.put(PRICE, stock.getPrice());
//            contentValues.put(PRICE_CHANGE, stock.getPriceChange());
//            contentValues.put(PERCENTAGE, stock.getPercentage());

//            database.insert(TABLE_NAME, null, contentValues);
            database.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            Log.d(TAG, "addStock: Add Complete");
        }catch (Exception e) {
            Log.d(TAG, "addStock: DatabaseHandler - Inside normal exception");
        }
    }

    public void deleteStock(String stockSymbol) {
        Log.d(TAG, "deleteStock: Deleting Stock "+stockSymbol);
        int count = database.delete(TABLE_NAME, SYMBOL+" = ?", new String[]{stockSymbol});
        Log.d(TAG, "deleteStock: "+count);
    }

    public void updateStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(COMPANY, stock.getCompanyName());
//        values.put(PRICE, stock.getPrice());
//        values.put(PRICE_CHANGE, stock.getPriceChange());
//        values.put(PERCENTAGE, stock.getPercentage());

        long key = database.update(TABLE_NAME, values, SYMBOL+" = ?", new String[] {stock.getStockSymbol()});
        Log.d(TAG, "updateStock: "+key);
    }

//    public ArrayList<Stock> loadStock() {
//        Log.d(TAG, " loadStocks: Load all symbol-company entries from DB");
//        ArrayList<Stock> stocks = new ArrayList<>();
//
//        Cursor cursor = database.query(TABLE_NAME, new String[] {SYMBOL, COMPANY, PRICE, PRICE_CHANGE, PERCENTAGE}, null, null, null, null, null);
//        if(cursor != null) {
//            cursor.moveToFirst();
//
//            for(int i=0;i<cursor.getCount();i++) {
//                String stockSymbol = cursor.getString(0);
//                String companyName = cursor.getString(1);
//                Double price = cursor.getDouble(2);
//                Double priceChange = cursor.getDouble(3);
//                Double percentage = cursor.getDouble(4);
//
//                stocks.add(new Stock(stockSymbol, companyName, price, priceChange, percentage));
//                cursor.moveToNext();
//            }
//            cursor.close();
//        }
//        return stocks;
//    }

    public ArrayList<Stock> loadStock() {
        Log.d(TAG, " loadStocks: Load all symbol-company entries from DB");
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(TABLE_NAME, new String[] {SYMBOL, COMPANY}, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++) {
                String stockSymbol = cursor.getString(0);
                String companyName = cursor.getString(1);

                stocks.add(new Stock(stockSymbol, companyName));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void dumpLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String stockSymbol = cursor.getString(0);
                String companyName = cursor.getString(1);

                Log.d(TAG, "dumpLog: " +
                        String.format("%s %-18s", SYMBOL + ":", stockSymbol) +
                        String.format("%s %-18s", COMPANY + ":", companyName));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "dumpLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
}
