package com.victor.sexytalk.sexytalk.Adaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.R;

public class AdapterViewImage extends RecyclerView.Adapter<AdapterViewImage.ContactViewHolder> {


    private String loveMessageToDisplay;
    private String imageUrl;
    private Context context;

    private int rotationAngle;

    public AdapterViewImage(String loveMessageToDisplay, String imageUrl,  Context context) {
        this.loveMessageToDisplay = loveMessageToDisplay;
        this.imageUrl = imageUrl;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {

        contactViewHolder.vLoveMessage.setText(loveMessageToDisplay);

        //Picasso e vanshta bibilioteka, koito ni pozvoliava da otvariame snimki ot internet
        Picasso.with(context).load(imageUrl).into(contactViewHolder.vPic);

        contactViewHolder.imageRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotationAngle = (int) contactViewHolder.vPic.getRotation();
                contactViewHolder.vPic.setRotation(rotationAngle +90);
            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_view_image, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView vLoveMessage;
        protected ImageView vPic;
        protected ImageView imageRotate;
        public ContactViewHolder(View v) {
            super(v);
            vLoveMessage =  (TextView) v.findViewById(R.id.loveMessage);
            vPic = (ImageView) v.findViewById(R.id.imageView_to_display_picture);
            imageRotate = (ImageView) v.findViewById(R.id.imageRotate);

        }
    }
}