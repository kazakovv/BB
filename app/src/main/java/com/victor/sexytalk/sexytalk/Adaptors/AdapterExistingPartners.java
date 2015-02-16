package com.victor.sexytalk.sexytalk.Adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnerDeleteRequest;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.List;

/**
 * Created by Victor on 14/01/2015.
 */
public class AdapterExistingPartners  extends ArrayAdapter<BackendlessUser> {
    protected Context mContext;
    protected BackendlessUser mCurrentUser;
    protected List<BackendlessUser> mPartners;


    public AdapterExistingPartners(Context context,  List<BackendlessUser> partners, BackendlessUser currentUser) {
        super(context, R.layout.item_delete_partner, partners );
        mContext = context;
        mPartners = partners;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {

                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_delete_partner, null);
                holder = new ViewHolder();
                holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
                holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
                holder.deletePartnerButton = (Button) convertView.findViewById(R.id.deletePartnerButton);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

        BackendlessUser partner = mPartners.get(position);
        holder.nameLabel.setText(partner.getProperty(Statics.KEY_USERNAME).toString());

        //zarezdame profilePic
        if(mPartners.get(position).getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
            String existingProfilePicPath = (String) mPartners.get(position).getProperty(Statics.KEY_PROFILE_PIC_PATH);
            Picasso.with(mContext).load(existingProfilePicPath).into(holder.iconImageView);
        }


        holder.deletePartnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pogazvame dialog boh dali naistina iskame da iztriem partiora
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String namePartnerToDelete = (String) mPartners.get(position).getProperty(Statics.KEY_USERNAME);
                String message = mContext.getString(R.string.dialog_delete_partner_confirmation)
                        + " " + namePartnerToDelete + "?";
                builder.setMessage(message);
                builder.setTitle(R.string.dialog_box_deleting_partner);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.dialog_delete_partner_yes_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //sazdavame nov spisak s partniori za tekushtia potrebitel
                                BackendlessUser[] newListWithPartnersForCurrentUser =
                                        new BackendlessUser[(mPartners.size() - 1)];
                                BackendlessUser[] currentListWithPartners =
                                        (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                                int i =0;
                                for(BackendlessUser partner : currentListWithPartners) {
                                    //kopirame vsichki partniori osven toya koito iskame da iztriem
                                    if(! partner.getEmail().equals(mPartners.get(position).getEmail()))
                                        //TODO: zabiva na toya red, ako iztriem partnior vednaga, sled kato sme go dobavili
                                        //TODO Zapiba tuk pri triene na partniori
                                        newListWithPartnersForCurrentUser[i] = partner;
                                        i++;
                                }
                                //updatevame spisaka s partniori za tekushtia potrebitel
                                mCurrentUser.setProperty(Statics.KEY_PARTNERS,newListWithPartnersForCurrentUser);
                                //kachvame novia spisak s partniori v backendless
                                Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        final BackendlessUser partnerToBeDeleted = mPartners.get(position);
                                        mPartners.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext,R.string.existing_partner_deleted,Toast.LENGTH_LONG).show();
                                        //tova e za kanala, po koito da izpratim push message
                                        final String receiverID = partnerToBeDeleted.getObjectId();

                                        //kachvame delete request v backendless
                                        PartnerDeleteRequest deleteRequest = new PartnerDeleteRequest();
                                        deleteRequest.setEmail_userDeleted(partnerToBeDeleted.getEmail());
                                        deleteRequest.setEmail_userDeleting(mCurrentUser.getEmail());
                                        deleteRequest.setUserDeleted(partnerToBeDeleted);
                                        deleteRequest.setUserDeleting(mCurrentUser);
                                        deleteRequest.setUsername_userDeleted((String) partnerToBeDeleted.getProperty(Statics.KEY_USERNAME));
                                        deleteRequest.setUsername_userDeleting((String)mCurrentUser.getProperty(Statics.KEY_USERNAME));
                                        Backendless.Data.of(PartnerDeleteRequest.class).save(deleteRequest, new AsyncCallback<PartnerDeleteRequest>() {
                                            @Override
                                            public void handleResponse(PartnerDeleteRequest partnerDeleteRequest) {
                                               //izprashtame push notification, che ima delete request
                                                //izprashtame push notification na drugia chovek da si updatene partniorite

                                                Backendless.Messaging.publish(receiverID,Statics.KEY_PARTNER_DELETE, new AsyncCallback<MessageStatus>() {
                                                    @Override
                                                    public void handleResponse(MessageStatus messageStatus) {
                                                        /*
                                                        TUK USPESHNO ZAVARSHVAME
                                                         */
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                       //error sending push. Nisho ne mozem da napravim
                                                    }
                                                });
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault backendlessFault) {
                                                    //error uploading delete request. Nishto ne mozem da napravim
                                            }
                                        });


                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        });
                builder.setNegativeButton(R.string.dialog_delete_partner_no_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        });




        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button deletePartnerButton;
    }



}
