package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.BackendlessUser;

/**
 * Created by Victor on 14/01/2015.
 */
public class AdapterExistingPartners  extends ArrayAdapter<BackendlessUser> {
    protected Context mContext;
    protected BackendlessUser mCurrentUser;
    BackendlessUser[] mPartners;

    public AdapterExistingPartners(Context context,  BackendlessUser[] partners, BackendlessUser currentUser) {
        super(context, R.layout.item_delete_partner, partners );
        mContext = context;
        mPartners = partners;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_delete_partner, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.deletePartnerButton = (Button) convertView.findViewById(R.id.deletePartnerButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BackendlessUser partner = mPartners[position];
        holder.nameLabel.setText( partner.getProperty(Statics.KEY_USERNAME).toString());

        holder.deletePartnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete partner
            }
        });


        return convertView;
    }



    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button deletePartnerButton;
    }
}
