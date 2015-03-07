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

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
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
            holder.timeToExpiry = (TextView) convertView.findViewById(R.id.timeToExpiry);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        Messages message = mMessages.get(position);
        if(message.getMessageType().equals(Statics.TYPE_IMAGE_MESSAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);

            if(message.getOpened() !=null) {
                holder.timeToExpiry.setText(calculateTimeToExpiry(message.getOpened()));
            } else { //ako saobshtenieto ne e bilo otvoreno , ne se otbroiava nishto
                holder.timeToExpiry.setVisibility(View.INVISIBLE);
            }

        } else if (message.getMessageType().equals(Statics.TYPE_KISS)) {
            holder.iconImageView.setImageResource(R.drawable.ic_kiss_dark);
            holder.timeToExpiry.setVisibility(View.INVISIBLE);
        }

        else { //prosto text saobstehnie
            holder.iconImageView.setImageResource(R.drawable.ic_action_unread);
            if(message.getOpened() !=null) {
                holder.timeToExpiry.setText(calculateTimeToExpiry(message.getOpened()));
            } else { //ako saobshtenieto ne e bilo otvoreno , ne se otbroiava nishto
                holder.timeToExpiry.setVisibility(View.INVISIBLE);
            }
        }
        String namesender = message.getSenderUsername();
        holder.nameLabel.setText(message.getSenderUsername());

        return convertView;

    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeToExpiry;
    }

    private String calculateTimeToExpiry(Date opened) {
        int timeToDisplayMessage = Statics.MESSAGE_TIME_TO_DISPLAY;
        String message = "";
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        float diff = (now.getTime() - opened.getTime());
        float seconds = diff / 1000;
        float minutes = seconds / 60;
        float hours = minutes / 60;
        //formatirane za time to display message
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        if((timeToDisplayMessage - hours) > 1) {

         float disappearing = (timeToDisplayMessage - hours);

        message = mContext.getResources().getString(R.string.message_disappearing) + " " +
                formatter.format(disappearing) + " " + mContext.getResources().getString(R.string.hours);
        } else {
            float disappearing =  ((24 - hours)  * 60);
        message = mContext.getResources().getString(R.string.message_disappearing) + " " +
                formatter.format(disappearing) + " " + mContext.getResources().getString(R.string.minutes);
            //ako ostava 1 pravim minute v edinstveno chislo
            if(disappearing <= 1.5) {
                message = mContext.getResources().getString(R.string.message_disappearing) + " " +
                         mContext.getResources().getString(R.string.minute);
            }
        }

        return message;
    }



}








