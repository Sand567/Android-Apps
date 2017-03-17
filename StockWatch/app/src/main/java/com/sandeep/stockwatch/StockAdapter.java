package com.sandeep.stockwatch;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Created by Sandeep on 03-03-2017.
 */

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private List<Stock> stockList;
    private MainActivity mainActivity;


    public StockAdapter(List<Stock> stockList, MainActivity mainActivity) {
        this.stockList = stockList;
        this.mainActivity = mainActivity;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_row,parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        Double priceChange = (stock.getPriceChange());

        if(priceChange < 0) {
            holder.triangle.setTextColor(Color.RED);
            holder.pChange.setTextColor(Color.RED);
            holder.cName.setTextColor(Color.RED);
            holder.label.setTextColor(Color.RED);
            holder.percent.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.triangle.setText("▼");
            holder.label.setText(stock.getStockSymbol());
            holder.price.setText(String.format(Locale.US, "%.1f", (stock.getPrice())));
            holder.pChange.setText(String.format(Locale.US, "%.2f", (stock.getPriceChange())));
            holder.percent.setText("("+String.format(Locale.US, "%.2f", (stock.getPercentage()))+"%)");
            holder.cName.setText(stock.getCompanyName());
        }else {
            holder.triangle.setTextColor(Color.GREEN);
            holder.pChange.setTextColor(Color.GREEN);
            holder.cName.setTextColor(Color.GREEN);
            holder.label.setTextColor(Color.GREEN);
            holder.percent.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.triangle.setText("▲");
            holder.label.setText(stock.getStockSymbol());
            holder.price.setText(String.format(Locale.US, "%.1f", (stock.getPrice())));
            holder.pChange.setText(String.format(Locale.US, "%.2f", (stock.getPriceChange())));
            holder.percent.setText("("+String.format(Locale.US, "%.2f", (stock.getPercentage()))+"%)");
            holder.cName.setText(stock.getCompanyName());
        }

//        System.out.println(stock.getCompanyName()+"\n"+stock.getStockSymbol()+"\n"+stock.getPercentage()+"\n"+stock.getPrice()+"\n"+stock.getPriceChange());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
