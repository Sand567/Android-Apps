package com.sandeep.multinote;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sandeep on 15-02-2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView noteTitle;
    public TextView noteDateAndTime;
    public TextView newNote;

    public MyViewHolder(View itemView) {
        super(itemView);
        noteTitle = (TextView) itemView.findViewById(R.id.titleId);
        noteDateAndTime = (TextView) itemView.findViewById(R.id.dateId);
        newNote = (TextView) itemView.findViewById(R.id.noteId);


    }
}
