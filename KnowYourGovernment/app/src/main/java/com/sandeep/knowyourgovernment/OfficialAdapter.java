package com.sandeep.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandeep on 02-04-2017.
 */

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    List<Official> secondOfficialList = new ArrayList<>();
    private MainActivity mainActivity;

    public OfficialAdapter(List<Official> secondOfficialList, MainActivity mainActivity) {
        this.secondOfficialList = secondOfficialList;
        this.mainActivity = mainActivity;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_list_row, parent, false);

        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);

        return new OfficialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = secondOfficialList.get(position);
        holder.position.setText(official.getOffice());
        holder.name.setText(official.getName());
        holder.party.setText("("+official.getParty()+")");
    }

    @Override
    public int getItemCount() {
        return secondOfficialList.size();
    }
}
