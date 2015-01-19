package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.backendless.BackendlessUser;

/**
 * Created by Victor on 19/01/2015.
 */
public class AdapterSendTo extends RecyclerView.Adapter<AdapterSendTo.ViewHolder> {
    private BackendlessUser[] mPartners;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mPartnerUserName;
        public ImageView mPartnerThumbnail;
        public CheckBox mSendYesNo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mPartnerUserName = (TextView) itemLayoutView.findViewById(R.id.partnerUsername);
            mPartnerThumbnail = (ImageView) itemLayoutView.findViewById(R.id.thumbnail_partner);
            mSendYesNo = (CheckBox) itemLayoutView.findViewById(R.id.sendYesNo);

            //mSendYesNo.setOnClickListener(this);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(mSendYesNo.isChecked()) {
                mSendYesNo.setChecked(false);
            } else {
                mSendYesNo.setChecked(true);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterSendTo(BackendlessUser[] myPartners) {

        mPartners = myPartners;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterSendTo.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_sendto, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(itemLayoutView);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String username = (String) mPartners[position].getProperty(Statics.KEY_USERNAME);
        holder.mPartnerUserName.setText(username);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPartners.length;
    }
}
