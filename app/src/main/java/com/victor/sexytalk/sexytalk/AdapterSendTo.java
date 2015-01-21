package com.victor.sexytalk.sexytalk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.BackendlessUser;

import java.util.ArrayList;

/**
 * Created by Victor on 19/01/2015.
 */
public class AdapterSendTo extends RecyclerView.Adapter<AdapterSendTo.ViewHolder> {
    private static BackendlessUser[] mPartners;

    //TODO:ne moga da nameria po-dobro reshenie
    public static ArrayList<Integer> mSendTo = new ArrayList<Integer>();

    public static ArrayList<String> mRecepientUserNames = new ArrayList<String>();
    public static ArrayList<String> mRecepientEmails = new ArrayList<String>();


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
            mSendYesNo.setClickable(false);
            itemLayoutView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //TODO: izpolzvam statichni promenlivi. Posle gi vzimam v SendTo kato se cakne na izprati ot toolbar
            // Ne e nai-dobroto reshenie

            if(mSendYesNo.isChecked()) {
                mSendYesNo.setChecked(false);
                int positionToRemove = mSendTo.indexOf(getPosition());
                mSendTo.remove(positionToRemove);
                mRecepientEmails.remove(positionToRemove);
                mRecepientUserNames.remove(positionToRemove);

            } else {
                mSendYesNo.setChecked(true);
                mSendTo.add(getPosition());
                mRecepientEmails.add(mPartners[getPosition()].getEmail());
                mRecepientUserNames.add((String) mPartners[getPosition()].getProperty(Statics.KEY_USERNAME));
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
