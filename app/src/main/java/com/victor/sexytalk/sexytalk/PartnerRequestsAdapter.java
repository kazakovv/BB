package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            //onClick za accept butona
            holder.buttonAccceptPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //2 asnc tasks edna v druga
                    //1.dobaviame partniora
                    //2.iztrivame chakashtia request


                }
            });

            //onClick za reject butona
            holder.buttonRejectPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //iztrivame request ot backendless
                    Backendless.Data.of(PartnersAddRequest.class)
                            .remove(mPendingPartnerRequests.get(position), new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    //iztrivame reda ot spisaka
                                    mPendingPartnerRequests.remove(position);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });
        return convertView;
    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button buttonAccceptPartner;
        Button buttonRejectPartner;
    }
}
