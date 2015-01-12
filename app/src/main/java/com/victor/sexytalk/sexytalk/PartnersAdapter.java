package com.victor.sexytalk.sexytalk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.backendless.messaging.MessageStatus;

import java.util.List;

/**
 * Created by Victor on 10/01/2015.
 */
public class PartnersAdapter extends ArrayAdapter<BackendlessUser> {

    protected Context mContext;
    protected List<BackendlessUser> mPartners;
    protected BackendlessUser mCurrentUser;

    public PartnersAdapter(Context context, List<BackendlessUser> partners, BackendlessUser currentUser) {
        super(context,R.layout.add_partner_item, partners);
        mContext = context;
        mPartners = partners;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        holder.buttonAddPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Vic","we are ahre");
                sendPartnerRequest(position);
               // ((FragmentSearchPartners)mContext).sendPartnerRequest(position);
            }
        });


        return convertView;
    }



    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button buttonAddPartner;
    }


    protected void sendPartnerRequest(int selectedPartnerPosition) {


        //Ako caknem na add ot list se sluchvat 2 neshta chrez 2 async tasks edna v druga
        //1. Kazchavame data table s user request
        //2. Izprashtame push message, che ima pending partner request na saotvetnia user

        //zatvariame prozoreca i se vrashtame kam main activity
        Intent mainActivity = new Intent(mContext,Main.class);
        mContext.startActivity(mainActivity);

        final BackendlessUser selectedPartner = mPartners.get(selectedPartnerPosition);

        //izprashtame request da si stanem partniori
        PartnersAddRequest partnerToAdd = new PartnersAddRequest();
        partnerToAdd.setEmail_partnerToConfirm(selectedPartner.getEmail());
        partnerToAdd.setEmail_userRequesting(mCurrentUser.getEmail());
        partnerToAdd.setPartnerToConfirm(selectedPartner);
        partnerToAdd.setUserRequesting(mCurrentUser);
        partnerToAdd.setUsername_userRequesting((String) mCurrentUser.getProperty(Statics.KEY_USERNAME));
        partnerToAdd.setUsername_userToConfirm((String) selectedPartner.getProperty(Statics.KEY_USERNAME));
        //Kachvame zaiavkata v Backendless

        Backendless.Data.of(PartnersAddRequest.class).save(partnerToAdd, new AsyncCallback<PartnersAddRequest>() {
            @Override
            public void handleResponse(PartnersAddRequest partnersAddRequest) {
                //sled kato kachim data v backendless izprashtame i push

                //tova e za kanala, po koito da izpratim push message
                String receiverID = selectedPartner.getObjectId();

                Backendless.Messaging.publish(receiverID,Statics.KEY_PARTNER_REQUEST,new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus messageStatus) {
                        Toast.makeText(mContext,
                                R.string.partner_request_sent_toast, Toast.LENGTH_LONG).show();                            }
                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO:tr da se promeni saobshtenieto. Izpratili sme tablicata, no ne push message
                        Toast.makeText(mContext,
                                R.string.partner_request_not_sent_toast,Toast.LENGTH_LONG).show();                            }
                });
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(mContext,
                        R.string.partner_request_not_sent_toast,Toast.LENGTH_LONG).show();
            }
        });

    }

}
