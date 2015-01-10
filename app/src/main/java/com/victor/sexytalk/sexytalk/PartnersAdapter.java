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
 * Created by Victor on 10/01/2015.
 */
public class PartnersAdapter extends ArrayAdapter<BackendlessUser> {

    protected Context mContext;
    protected List<BackendlessUser> mPartners;

    public PartnersAdapter(Context context, List<BackendlessUser> partners) {
        super(context,R.layout.add_partner_item, partners);
        mContext = context;
        mPartners = partners;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.add_partner_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.buttonAddPartner = (Button) convertView.findViewById(R.id.addPartnerButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BackendlessUser partner = mPartners.get(position);
            holder.nameLabel.setText( partner.getProperty(Statics.KEY_USERNAME).toString());
        return convertView;
    }



    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button buttonAddPartner;
    }
}
