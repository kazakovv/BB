package com.victor.sexytalk.sexytalk.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.List;

/**
 * Tozi klas sazdava custom Array Adaptor za spisaka s polucheni saobshtenia.
 * Na vseki red ot liavo se pokazva kartinka v zavisimost dali e izpratena kartinka ili filmche
 * ot drugata strana izliza imeto na choveka
 */
public class AdapterMessage extends ArrayAdapter<Messages> {
    protected Context mContext;
    protected List<Messages> mMessages;

    public AdapterMessage(Context context, List<Messages> messages) {

        super(context, R.layout.item_message, messages);

        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.message_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        Messages message = mMessages.get(position);
        if(message.getMessageType().equals(Statics.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);

        } else if (message.getMessageType().equals(Statics.TYPE_KISS)) {
            holder.iconImageView.setImageResource(R.drawable.ic_kiss_dark);
        }

        else { //prosto text saobstehnie
            holder.iconImageView.setImageResource(R.drawable.ic_action_unread);
        }
        String namesender = message.getSenderUsername();
        holder.nameLabel.setText(message.getSenderUsername());

        return convertView;

    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
    }



}








