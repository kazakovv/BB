package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import static com.victor.sexytalk.sexytalk.R.layout.*;

/**
 * Tozi klas sazdava custom Array Adaptor za spisaka s polucheni saobshtenia.
 * Na vseki red ot liavo se pokazva kartinka v zavisimost dali e izpratena kartinka ili filmche
 * ot drugata strana izliza imeto na choveka
 */
public class MessageAdapter extends ArrayAdapter<Messages> {
    protected Context mContext;
    protected List<Messages> mMessages;

    public MessageAdapter(Context context, List<Messages> messages) {

        super(context, R.layout.message_item, messages);

        mContext = context;
        mMessages = messages;
    }







    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
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

        } else if (message.getMessageType().equals(Statics.TYPE_VIDEO)){
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);

        } else {
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



    public void refill(List<Messages> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }


}








