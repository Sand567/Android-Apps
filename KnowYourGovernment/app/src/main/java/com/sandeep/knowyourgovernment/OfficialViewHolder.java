package com.sandeep.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sandeep on 02-04-2017.
 */

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    public TextView position;
    public TextView name;
    public TextView party;

    public OfficialViewHolder(View itemView) {
        super(itemView);
        position = (TextView) itemView.findViewById(R.id.position);
        name = (TextView) itemView.findViewById(R.id.name);
        party = (TextView) itemView.findViewById(R.id.party);
    }
}
