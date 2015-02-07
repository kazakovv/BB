package com.victor.sexytalk.sexytalk.Adaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victor.sexytalk.sexytalk.R;

public class AdapterViewTextMessage extends RecyclerView.Adapter<AdapterViewTextMessage.ContactViewHolder> {


    private String loveMessageToDisplay;
    private String usernameSender;
    private Context context;

    public AdapterViewTextMessage(String loveMessageToDisplay, String usernameSender, Context context) {
        this.loveMessageToDisplay = loveMessageToDisplay;
        this.usernameSender = usernameSender;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {

        contactViewHolder.vLoveMessage.setText(loveMessageToDisplay);
        String title = context.getResources().getString(R.string.love_message_title) + " " + usernameSender;
        contactViewHolder.vTitle.setText(title);


    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_view_text_message, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView vLoveMessage;
        protected TextView vTitle;
        public ContactViewHolder(View v) {
            super(v);
            vLoveMessage =  (TextView) v.findViewById(R.id.loveMessage);
            vTitle = (TextView) v.findViewById(R.id.message_title);

        }
    }
}