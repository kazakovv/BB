package com.victor.sexytalk.sexytalk.Adaptors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.CustomDialogs.CustomAlertDialog;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
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
    protected Activity mActivity;

    public AdapterSearchPartners(Context context, List<BackendlessUser> partners, BackendlessUser currentUser, Activity activity) {
        super(context, R.layout.item_add_partner, partners);
        mContext = context;
        mFoundUsers = partners;
        mCurrentUser = currentUser;
        mActivity = activity;
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
            holder.layoutButtons = (RelativeLayout) convertView.findViewById(R.id.layoutButtons);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BackendlessUser partner = mFoundUsers.get(position);
        String username = (String) partner.getProperty(Statics.KEY_USERNAME);
        if(username !=null) {
            holder.nameLabel.setText(username);
        } else {
            holder.nameLabel.setText(partner.getEmail());
        }

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

                sendPartnerRequest(position, holder.layoutButtons, holder.progressBar);
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
        RelativeLayout layoutButtons;
        ProgressBar progressBar;
    }


    protected void sendPartnerRequest(final int selectedPartnerPosition,
                                      final RelativeLayout layoutButtons, final ProgressBar progressBar) {


        //Ako caknem na add ot list se sluchvat 3 neshta chrez 3 async tasks edna v druga
        //1.Proveriavame dali veche ne e izpraten pending partner request. Ako e taka spirame
        //2. Kazchavame data table s user request
        //3. Izprashtame push message, che ima pending partner request na saotvetnia user

        //skrimave view i pokazvame spinner
        layoutButtons.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final BackendlessUser selectedPartner = mFoundUsers.get(selectedPartnerPosition);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = "email_userRequesting='" + mCurrentUser.getEmail() +"'" +
                             "email_partnerToConfirm='" + selectedPartner.getEmail() + "'";
        Backendless.Data.of(PartnersAddRequest.class).find(dataQuery, new AsyncCallback<BackendlessCollection<PartnersAddRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<PartnersAddRequest> pendingRequest) {
                if (pendingRequest.getCurrentPage().size() > 0) {
                    //veche e izpratena zaiavka za partner request. Prekratiavame metoda tuk
                    layoutButtons.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    String title = mContext.getResources().getString(R.string.dialog_partner_request_already_sent_title);
                    String message = mContext.getResources()
                            .getString(R.string.dialog_partner_request_already_sent_to_user_message) + " " +
                            selectedPartner.getProperty(Statics.KEY_USERNAME);

                    CustomAlertDialog waitingForLove = new CustomAlertDialog();
                    Bundle dialogContent = new Bundle();
                    dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                    dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                    waitingForLove.setArguments(dialogContent);
                    waitingForLove.show( mActivity.getFragmentManager(),"tag_alert_dialog");
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.dialog_partner_request_already_sent_title)
                            .setMessage(message)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    */
                } else {
                    //niama izpratena zaiavka

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
                            layoutButtons.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            //sled kato kachim data v backendless izprashtame i push

                            //tova e za kanala, po koito da izpratim push message

                            BackendlessMessage.sendPush(mCurrentUser, selectedPartner, null, mContext, Statics.TYPE_PARTNER_REQUEST);
                            Toast.makeText(mContext, R.string.partner_request_sent_toast, Toast.LENGTH_LONG).show();
                            mFoundUsers.remove(selectedPartnerPosition);
                            notifyDataSetChanged();


                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            layoutButtons.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext,
                                    R.string.partner_request_not_sent_toast, Toast.LENGTH_LONG).show();
                        }

                    }); //krai na kachvane na partner request

                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                layoutButtons.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(mContext,
                        R.string.partner_request_not_sent_toast, Toast.LENGTH_LONG).show();
            }
        }); //krai proverka dali ne e izpraten pending partner request


    }

}
