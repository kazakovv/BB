package com.victor.sexytalk.sexytalk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.List;

/**
 * Created by Victor on 14/01/2015.
 */
public class AdapterExistingPartners  extends ArrayAdapter<BackendlessUser> {
    protected Context mContext;
    protected BackendlessUser mCurrentUser;
    protected List<BackendlessUser> mPartners;

    protected BackendlessUser[] newListWithPartners;

    public AdapterExistingPartners(Context context,  List<BackendlessUser> partners, BackendlessUser currentUser) {
        super(context, R.layout.item_delete_partner, partners );
        mContext = context;
        mPartners = partners;
        mCurrentUser = currentUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        //TODO: crashesh ako
        BackendlessUser partner = mPartners.get(position);
        holder.nameLabel.setText(partner.getProperty(Statics.KEY_USERNAME).toString());

        holder.deletePartnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pogazvame dialog boh dali naistina iskame da iztriem partiora
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String namePartnerToDelete = (String) mPartners.get(position).getProperty(Statics.KEY_USERNAME);
                String message = mContext.getString(R.string.dialog_delete_partner_confirmation)
                        + " " + namePartnerToDelete + "?";
                builder.setMessage(message);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.dialog_delete_partner_yes_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //deletePartner(position);
                                mPartners.remove(position);
                                Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext,R.string.existing_partner_deleted,Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        String error = backendlessFault.getMessage();
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

    private void deletePartner(int position) {
        /*
        //delete partner
        if (mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            BackendlessUser[] existingPartners =
                    (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
            final int newNumberOfPartners = (existingPartners.length - 1) ;
            newListWithPartners = new BackendlessUser[newNumberOfPartners];
            //ako ima poveche ot 1 partnior
            if(newNumberOfPartners > 0) {
                //kopirame array kato propuskame user, koito iskame da iztriem
                for(BackendlessUser user : existingPartners) {
                    int i = 0;
                    if(! user.equals(existingPartners[position]))
                        newListWithPartners[i] = user;
                    i++;
                }
                Log.d("Vic", "result");
            }
            //ako ima 1 partnior izprashtame prazen spisak v Backendless.
            //toi veche e sazdaden gore, kadeto inicializirame newListWithPartners[0]
            mCurrentUser.setProperty(Statics.KEY_PARTNERS,newListWithPartners);
            Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    Toast.makeText(mContext,R.string.existing_partner_deleted,Toast.LENGTH_LONG).show();
                    if(newListWithPartners.length > 0) {
                        mPartners = new BackendlessUser[newListWithPartners.length];
                        mPartners = newListWithPartners;
                    } else {
                        mPartners = new BackendlessUser[0];
                    }
                    notifyDataSetChanged();
                    Log.d("Vic","success");
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    String error = backendlessFault.getMessage();
                    Toast.makeText(mContext,R.string.general_server_error,Toast.LENGTH_LONG).show();
                }
            });
        }
        */
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        Button deletePartnerButton;
    }
}
