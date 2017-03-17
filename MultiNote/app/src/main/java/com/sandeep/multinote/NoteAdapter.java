package com.sandeep.multinote;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sandeep on 15-02-2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Note> multiNote;
    private MainActivity mainAct;

    public NoteAdapter(List<Note> noteList, MainActivity mainActivity) {
        this.multiNote = noteList;
        mainAct = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String extraText = "";
        Note note = multiNote.get(position);

        if(note.getNotes().length() >= 80) {
            extraText = note.getNotes();
            extraText = extraText.substring(0,80)+"...";
            holder.newNote.setText(extraText);
            holder.noteTitle.setText(note.getTitle());
            holder.noteDateAndTime.setText(note.getDateAndTime());
        }else {

            holder.newNote.setText(note.getNotes());
            holder.noteTitle.setText(note.getTitle());
            holder.noteDateAndTime.setText(note.getDateAndTime());

        }
    }

    @Override
    public int getItemCount() {
        return multiNote.size();
    }
}
