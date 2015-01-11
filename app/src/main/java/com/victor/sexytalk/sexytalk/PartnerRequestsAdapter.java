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

import java.util.List;

/**
 * Created by Victor on 11/01/2015.
 */
public class PartnerRequestsAdapter extends ArrayAdapter<PartnersAddRequest> {
    protected Context mContext;
    protected List<PartnersAddRequest> mPendingPartnerRequests;

    public PartnerRequestsAdapter(Context context, List<PartnersAddRequest> pendingPartnerRequests) {
        super(context, R.layout.partner_request_item, pendingPartnerRequests);
        mContext = context;
        mPendingPartnerRequests = pendingPartnerRequests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.partner_request_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.buttonAccceptPartner = (Button) convertView.findViewById(R.id.acceptPartnerButton);
            holder.buttonRejectPartner = (Button) convertView.findViewById(R.id.rejectPartnerButton);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

            PartnersAddRequest request = mPendingPartnerRequests.get(position);
            String userName = request.getUsername_userRequesting();
            holder.nameLabel.setText(userName);
        //TODO:add on click listeners za 2 butona tuk

        return convertView;
    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button buttonAccceptPartner;
        Button buttonRejectPartner;
    }
}
