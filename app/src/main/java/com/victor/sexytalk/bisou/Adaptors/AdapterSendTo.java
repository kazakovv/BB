package com.victor.sexytalk.bisou.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.BackendlessUser;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.bisou.Helper.RoundedTransformation;
import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;


/**
 * Created by Victor on 19/01/2015.
 */
public class AdapterSendTo extends ArrayAdapter<BackendlessUser> {
    protected Context mContext;
    protected BackendlessUser mCurrentUser;
    protected BackendlessUser[] mPartners;


    public AdapterSendTo(Context context,  BackendlessUser[] partners, BackendlessUser currentUser) {
        super(context, R.layout.item_list_sendto, partners );
        mContext = context;
        mPartners = partners;
        mCurrentUser = currentUser;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(mContext, R.layout.item_list_sendto, null);

        final ViewHolder holder;

        if (convertView == null || convertView.getTag() == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_sendto, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.sendYesNoCheckbox = (CheckBox) convertView.findViewById(R.id.sendYesNo);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BackendlessUser partner = mPartners[position];
        holder.nameLabel.setText(partner.getProperty(Statics.KEY_USERNAME).toString());
        //zarezdame avatarchetata
        if(mPartners[position].getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
            String existingProfilePicPath = (String) mPartners[position].getProperty(Statics.KEY_PROFILE_PIC_PATH);
            Picasso.with(mContext)
                    .load(existingProfilePicPath)
                    .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS,0))
                    .into(holder.iconImageView);
        }
        //ako ne se napravi tova onClick listener vav SendTo fragment ne raboti
        holder.sendYesNoCheckbox.setClickable(false);
        holder.sendYesNoCheckbox.setFocusable(false);

        holder.nameLabel.setFocusable(false);
        holder.nameLabel.setClickable(false);

        holder.iconImageView.setFocusable(false);
        holder.iconImageView.setClickable(false);

        convertView.setFocusable(false);
        convertView.setClickable(false);

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        CheckBox sendYesNoCheckbox;
    }

} //krai na adaptera
