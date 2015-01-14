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
public class PartnerRequestsAdapter extends ArrayAdapter<PartnersAddRequest> {
    protected Context mContext;
    protected List<PartnersAddRequest> mPendingPartnerRequests;
    protected BackendlessUser mCurrentUser;
    public PartnerRequestsAdapter(Context context, List<PartnersAddRequest> pendingPartnerRequests,
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
                    //3 async tasks edna v druga
                    //
                    //1.namirame array sas segashtinte partniori
                    //2.dobaviame novia partnior kam array i go kachvame v backendless
                    //3.iztrivame chakashtia request i updatvame spisaka

                    //TODO: tova e oprosten check po-dolu dali ima partniori ili ne.
                    //moze da propusnem 1 async task
                    /*
                    Object partners = mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                    if(partners instanceof BackendlessUser[]) {
                            //sashtestvuvat partniori veche
                    } else {
                            //niama nito 1 partnior
                    }*/
                    //1. Namirame sashtestvuvashtite partniori
                    String whereClause = "email='" + mCurrentUser.getEmail() + "'";
                    BackendlessDataQuery query = new BackendlessDataQuery();
                    QueryOptions queryOptions = new QueryOptions();
                    query.setWhereClause(whereClause);
                    queryOptions.addRelated( "partners" );
                    queryOptions.addRelated( "partners.RELATION-OF-RELATION" );
                    query.setQueryOptions( queryOptions );
                    Backendless.Data.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<BackendlessUser> currentUser) {
                          List<BackendlessUser> currentUserData = currentUser.getData();
                            if(currentUserData.size()>0 ) {
                                //nameren e tekushtiat potrebitel
                                //Tr da vzemem ot properties array s partniorite mu
                                BackendlessUser[] newListWithPartners;
                                if(currentUserData.get(0).getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                                    //ako veche ima drugi partniori
                                    BackendlessUser[] existingPartners =
                                            (BackendlessUser[]) currentUserData.get(0).getProperty(Statics.KEY_PARTNERS);

                                    //dobaviame novia partnior kam spisaka i uploadvame v Backendless
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
                                //2.updatevame propoerty za current user i kachvame v Backendless
                               mCurrentUser.setProperty(Statics.KEY_PARTNERS, newListWithPartners);
                               Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                                   @Override
                                   public void handleResponse(BackendlessUser backendlessUser) {
                                       //3. iztrivame pending partner request i updatvame spisaka
                                       Backendless.Data.of(PartnersAddRequest.class)
                                               .remove(mPendingPartnerRequests.get(position), new AsyncCallback<Long>() {
                                                   @Override
                                                   public void handleResponse(Long aLong) {
                                                       mPendingPartnerRequests.remove(position);
                                                       notifyDataSetChanged();
                                                       Toast.makeText(mContext,R.string.new_partner_added_successfully,Toast.LENGTH_LONG).show();
                                                        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                                        // TUK E KRAIAT NA USPESHNO DOBAVIANE NA PARTNIOR
                                                        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
                                                   }

                                                   @Override
                                                   public void handleFault(BackendlessFault backendlessFault) {
                                                       Log.d("Vic","error" + backendlessFault.getMessage());
                                                       Toast.makeText(mContext,R.string.general_error_message,Toast.LENGTH_LONG).show();

                                                   }
                                               });
                                   }

                                   @Override
                                   public void handleFault(BackendlessFault backendlessFault) {
                                    Log.d("Vic","error" + backendlessFault.getMessage());
                                       Toast.makeText(mContext,R.string.general_error_message,Toast.LENGTH_LONG).show();

                                   }
                               });



                            } else {
                            //tekushtiat potrebitel ne e otkrit, sledovatelno ima greshka
                                Toast.makeText(mContext,R.string.general_error_message,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(mContext, R.string.general_server_error,Toast.LENGTH_LONG).show();
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
