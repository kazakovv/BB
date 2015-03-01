package com.victor.sexytalk.sexytalk.Adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.Helper.BackendlessHelper;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.List;

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
        super(context, R.layout.item_partner_request, pendingPartnerRequests);
        mContext = context;
        mPendingPartnerRequests = pendingPartnerRequests;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null || convertView.getTag() == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_partner_request, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.partnerUsername);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.thumbnail_partner);
            holder.buttonAccceptPartner = (ImageButton) convertView.findViewById(R.id.acceptPartnerButton);
            holder.buttonRejectPartner = (ImageButton) convertView.findViewById(R.id.rejectPartnerButton);
            holder.layoutButtons = (RelativeLayout) convertView.findViewById(R.id.layoutButtons);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

            PartnersAddRequest request = mPendingPartnerRequests.get(position);
            String userName = request.getUsername_userRequesting();
            holder.nameLabel.setText(userName);

        //zarezdame profile pic, ako ima takava
        BackendlessUser userRequesting = mPendingPartnerRequests.get(position).getUserRequesting();
        if(userRequesting.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null) {
            String existingProfilePicPath = (String) userRequesting.getProperty(Statics.KEY_PROFILE_PIC_PATH);
            Picasso.with(mContext).load(existingProfilePicPath)
                    .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                    .into(holder.iconImageView);
        }
            //onClick za accept butona
            holder.buttonAccceptPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //1.Namirame partniorite na tekushtia potrebitel
                    //2.Namirame partiorite na partner, koito prashta request
                    //3. uploadvame v backendless novia spisak s partniorite za tekushtia potrebitel
                    //4. uploadvame v backendless novai spisak s partniorite za potrebitel, koito izprashta partner request
                    //5. iztrivame pending request
                    //6. sazdavame tablica za broia celuvki

                    //prokazvame spinner
                    holder.layoutButtons.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);

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
                                                        //6. sazdavame 2 reda za broia celuvki
                                                    BackendlessHelper.createTables(mCurrentUser, mUserRequesting);
                                                    BackendlessHelper.createTables(mUserRequesting, mCurrentUser);

                                                    //proverka dali ima pending partner request, za da skriem butona
                                                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);

                                                    //skrivame spinner
                                                    holder.layoutButtons.setVisibility(View.VISIBLE);
                                                    holder.progressBar.setVisibility(View.GONE);

                                                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                                    // TUK E KRAIAT NA USPESHNO DOBAVIANE NA PARTNIOR
                                                    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault backendlessFault) {
                                                    Log.d("Vic","error" + backendlessFault.getMessage());
                                                    //skrivame spinner
                                                    holder.layoutButtons.setVisibility(View.VISIBLE);
                                                    holder.progressBar.setVisibility(View.GONE);

                                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                                    //proverka dali ima pending partner request, za da skriem butona
                                                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);
                                                }
                                            });
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Log.d("Vic","error" + backendlessFault.getMessage());
                                    //skrivame spinner
                                    holder.layoutButtons.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.GONE);

                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                    //proverka dali ima pending partner request, za da skriem butona
                                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Log.d("Vic","error" + backendlessFault.getMessage());
                            //skrivame spinner
                            holder.layoutButtons.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                            //proverka dali ima pending partner request, za da skriem butona
                            BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);
                        }
                    });



                }//end onClick
            });//end onClick Listener

            //onClick za reject butona
            holder.buttonRejectPartner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pokazvame spinner
                    holder.layoutButtons.setVisibility(View.INVISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    //iztrivame request ot backendless
                    Backendless.Data.of(PartnersAddRequest.class)
                            .remove(mPendingPartnerRequests.get(position), new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    //iztrivame reda ot spisaka
                                    mPendingPartnerRequests.remove(position);
                                    notifyDataSetChanged();
                                    //skrivame spinner
                                    holder.layoutButtons.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    //proverka dali ima pending partner request, za da skriem butona
                                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    String error = backendlessFault.getMessage();
                                    //skrivame spinner
                                    holder.layoutButtons.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                                    //proverka dali ima pending partner request, za da skriem butona
                                    BackendlessHelper.checkForPendingParnerRequests(mCurrentUser,null);
                                }
                            });
                }
            });
        return convertView;
    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        ImageButton buttonAccceptPartner;
        ImageButton buttonRejectPartner;
        RelativeLayout layoutButtons;
        ProgressBar progressBar;
    }
}
