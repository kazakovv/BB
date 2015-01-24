package com.victor.sexytalk.sexytalk;

import android.content.Context;
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
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Victor on 11/01/2015.
 */
public class AdapterPartnerRequests extends ArrayAdapter<PartnersAddRequest> {
    protected Context mContext;
    protected List<PartnersAddRequest> mPendingPartnerRequests;
    protected BackendlessUser mCurrentUser;
    protected BackendlessUser mUserRequesting;

    public AdapterPartnerRequests(Context context, List<PartnersAddRequest> pendingPartnerRequests,
                                  BackendlessUser currentUser) {
        super(context, R.layout.partner_request_item, pendingPartnerRequests);
        mContext = context;
        mPendingPartnerRequests = pendingPartnerRequests;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.partner_request_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.buttonAccceptPartner = (Button) convertView.findViewById(R.id.acceptPartnerButton);
            holder.buttonRejectPartner = (Button) convertView.findViewById(R.id.rejectPartnerButton);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

            PartnersAddRequest request = mPendingPartnerRequests.get(position);
            String userName = request.getUsername_userRequesting();
            holder.nameLabel.setText(userName);
            //onClick za accept butona
            holder.buttonAccceptPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.Namirame partniorite na tekushtia potrebitel
                    //2.Namirame partiorite na partner, koito prashta request
                    //3. uploadvame v backendless novia spisak s partniorite za tekushtia potrebitel
                    //4. uploadvame v backendless novai spisak s partniorite za potrebitel, koito izprashta partner request
                    //5. iztrivame pending request

                    //1.Namirame partniorite na tekushtia potrebitel
                    // i sazdavame nov massiv sas spisak ot partniori za tekushtia potrebitel

                    BackendlessUser[] newListWithPartners;
                    if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                        BackendlessUser[] existingPartners =
                                (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                        //vzimame novia partnio or mPendingPartnerRequests
                        BackendlessUser partnerToAdd = mPendingPartnerRequests.get(position).getUserRequesting();
                        //Dobaviame partnerToAdd kam sashtestvuvashtite partionri
                        int newSize = existingPartners.length + 1;
                        newListWithPartners = new BackendlessUser[newSize];
                        //dobaviame novia partnior v nachaloto na spisaka
                        newListWithPartners[0] = partnerToAdd;
                        //dobaviame starite partniori kam novia spisak
                        int i = 1;
                        for (BackendlessUser existingPartner : existingPartners) {
                            newListWithPartners[i] = existingPartner;
                            i++;
                        }
                    } else {
                        //ako niama drugi partniori dobaviame samo noviat
                        BackendlessUser partnerToAdd = mPendingPartnerRequests.get(position).getUserRequesting();
                        newListWithPartners = new BackendlessUser[1];
                        newListWithPartners[0] = partnerToAdd;
                    }
                    //updatevame spisaka s partniori za tekushtia potrebitel
                    mCurrentUser.setProperty(Statics.KEY_PARTNERS, newListWithPartners);


                    //2.Namirame partniorite na user, koito prashta partner request
                    // i sazdavame nov massiv sas spisak ot partniori za nego

                    BackendlessUser[] newListWithPartnersUser2;
                    mUserRequesting = mPendingPartnerRequests.get(position).getUserRequesting();

                    if(mUserRequesting.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                        //vzimame spisak sas sashtesvuvashtite partniori
                        BackendlessUser[] existingPartnersUser2 =
                                (BackendlessUser[]) mUserRequesting.getProperty(Statics.KEY_PARTNERS);
                    //partner to add e tekushtiat potrebitel
                        BackendlessUser partnerToAdd = mCurrentUser;
                        //Dobaviame partnerToAdd kam sashtestvuvashtite partionri
                        int newSize = existingPartnersUser2.length + 1;
                        newListWithPartnersUser2 = new BackendlessUser[newSize];
                        //dobaviame novia partnior v nachaloto na spisaka
                        newListWithPartnersUser2[0] = partnerToAdd;
                        //dobaviame starite partniori kam novia spisak
                        int i = 1;
                        for (BackendlessUser existingPartner : existingPartnersUser2) {
                            newListWithPartnersUser2[i] = existingPartner;
                            i++;
                        }
                    } else {
                        //ako niama drugi partniori dobaviame samo noviat
                        BackendlessUser partnerToAdd = mCurrentUser;
                        newListWithPartnersUser2 = new BackendlessUser[1];
                        newListWithPartnersUser2[0] = partnerToAdd;
                    }
                    //updatevame partniorite za userRequesting
                    mUserRequesting.setProperty(Statics.KEY_PARTNERS,newListWithPartnersUser2);

                    //3.updatevame tekushtia potrebitel v backendless
                    Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            //useshno dobaven partner za tekushtia potrebitel

                            //4. updatevame userrequesting v Backendless
                            Backendless.UserService.update(mUserRequesting, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                   //uspeshno sme updatenali user requesing

                                    //5. Iztrivame pending partner request
                                    Backendless.Data.of(PartnersAddRequest.class)
                                            .remove(mPendingPartnerRequests.get(position), new AsyncCallback<Long>() {
                                                @Override
                                                public void handleResponse(Long aLong) {
                                                    mPendingPartnerRequests.remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(mContext,R.string.new_partner_added_successfully,Toast.LENGTH_LONG).show();
                                                    //TODO:tr da izpratim push na drugia chvek da si updatene spikat s partniorite
                                                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                                    // TUK E KRAIAT NA USPESHNO DOBAVIANE NA PARTNIOR
                                                    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault backendlessFault) {
                                                    Log.d("Vic","error" + backendlessFault.getMessage());
                                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Log.d("Vic","error" + backendlessFault.getMessage());
                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Log.d("Vic","error" + backendlessFault.getMessage());
                            Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                        }
                    });



                }//end onClick
            });//end onClick Listener

            //onClick za reject butona
            holder.buttonRejectPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //iztrivame request ot backendless
                    Backendless.Data.of(PartnersAddRequest.class)
                            .remove(mPendingPartnerRequests.get(position), new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    //iztrivame reda ot spisaka
                                    mPendingPartnerRequests.remove(position);
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    String error = backendlessFault.getMessage();
                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });
        return convertView;
    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button buttonAccceptPartner;
        Button buttonRejectPartner;
    }
}
