package com.sandeep.stockwatch;

import android.support.annotation.NonNull;

/**
 * Created by Sandeep on 03-03-2017.
 */

public class Stock implements Comparable {

    private String stockSymbol;
    private String companyName;
    private double price;
    private double priceChange;
    private double percentage;

    public Stock(String stockSymbol, String companyName) {
        this.stockSymbol = stockSymbol;
        this.companyName = companyName;
    }

    public Stock(String stockSymbol, String companyName, double price, double priceChange, double percentage) {
        this.stockSymbol = stockSymbol;
        this.companyName = companyName;
        this.price = price;
        this.priceChange = priceChange;
        this.percentage = percentage;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int i = 0;
        double d = ((Stock) o).getPrice();

        // Ascending Order
        double d1 = price - d;
        i = (int) d1;
        return i;
    }
}
