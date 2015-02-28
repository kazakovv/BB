package com.victor.sexytalk.sexytalk.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.List;

/**
 * Created by Victor on 10/01/2015.
 */
public class AdapterSearchPartners extends ArrayAdapter<BackendlessUser> {

    protected Context mContext;
    protected List<BackendlessUser> mFoundUsers;
    protected BackendlessUser mCurrentUser;

    public AdapterSearchPartners(Context context, List<BackendlessUser> partners, BackendlessUser currentUser) {
        super(context, R.layout.item_add_partner, partners);
        mContext = context;
        mFoundUsers = partners;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_partner, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.buttonAddPartner = (ImageButton) convertView.findViewById(R.id.addPartnerButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BackendlessUser partner = mFoundUsers.get(position);
        holder.nameLabel.setText(partner.getProperty(Statics.KEY_USERNAME).toString());

        holder.buttonAddPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check dali ne se opitva da dobavi sebe si
                if(mFoundUsers.get(position).getEmail().equals(mCurrentUser.getEmail())){
                    Toast.makeText(mContext,R.string.toast_cannot_add_yourself_as_partner,Toast.LENGTH_LONG).show();

                    return;
                }

                //check dali ne se opitva da dobavi veche sastestvuvasht partner
                if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[] ) {
                    BackendlessUser[] existingPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                    for(BackendlessUser partner : existingPartners) {
                        if(mFoundUsers.get(position).getEmail().equals(partner.getEmail())) {
                            String message = mFoundUsers.get(position).getProperty(Statics.KEY_USERNAME) + " "
                                    + mContext.getResources().getString(R.string.user_already_partner);

                            Toast.makeText(mContext, message,Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                sendPartnerRequest(position);
            }
        });

        //zarezdame profile pic, ako ima takava
        BackendlessUser partnerSearch = mFoundUsers.get(position);
        if(partnerSearch.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
            String existingProfilePicPath = (String) partnerSearch.getProperty(Statics.KEY_PROFILE_PIC_PATH);
            Picasso.with(mContext)
                    .load(existingProfilePicPath)
                    .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                    .into(holder.iconImageView);
        }


        return convertView;
    }



    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        ImageButton buttonAddPartner;
    }


    protected void sendPartnerRequest(final int selectedPartnerPosition) {


        //Ako caknem na add ot list se sluchvat 2 neshta chrez 2 async tasks edna v druga
        //1. Kazchavame data table s user request
        //2. Izprashtame push message, che ima pending partner request na saotvetnia user


        final BackendlessUser selectedPartner = mFoundUsers.get(selectedPartnerPosition);

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
                String channel = selectedPartner.getEmail();
                //TODO tr da izpratim i istinsko push message

                Backendless.Messaging.publish(channel,Statics.KEY_PARTNER_REQUEST,
                        new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus messageStatus) {
                        //iztrivame rezultata ot spisaka i refreshvame spisaka
                        mFoundUsers.remove(selectedPartnerPosition);
                        notifyDataSetChanged();

                        Toast.makeText(mContext,
                                R.string.partner_request_sent_toast, Toast.LENGTH_LONG).show();
                    }
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
