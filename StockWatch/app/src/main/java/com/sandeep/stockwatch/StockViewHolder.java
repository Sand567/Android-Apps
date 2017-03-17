package com.sandeep.stockwatch;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sandeep on 03-03-2017.
 */

public class StockViewHolder extends RecyclerView.ViewHolder{

    public TextView label, price, pChange, percent, cName, triangle;


    public StockViewHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(R.id.label);
        price = (TextView) itemView.findViewById(R.id.price);
        pChange = (TextView) itemView.findViewById(R.id.pChange);
        percent = (TextView) itemView.findViewById(R.id.percent);
        cName = (TextView) itemView.findViewById(R.id.cName);
        triangle = (TextView) itemView.findViewById(R.id.triangle);
    }
}
