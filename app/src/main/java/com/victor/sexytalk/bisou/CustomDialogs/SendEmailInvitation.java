package com.victor.sexytalk.bisou.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;

/**
 * Created by Victor on 21/03/2015.
 */
public class SendEmailInvitation extends DialogFragment implements DialogInterface.OnShowListener {
    String emailToSend;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_send_email_invitation,null);

        if(getArguments().getString(Statics.KEY_RECEPIENT_EMAILS) != null) {
            emailToSend = getArguments().getString(Statics.KEY_RECEPIENT_EMAILS);
        }
        builder.setView(inflatedView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send email

                        String subject = getActivity().getResources().getString(R.string.email_invite_to_bisou_subject);
                        String body = getActivity().getResources().getString(R.string.email_invite_to_bisou_body);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        if (emailToSend != null) {
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailToSend});
                        }
                        i.putExtra(Intent.EXTRA_SUBJECT, subject);
                        i.putExtra(Intent.EXTRA_TEXT, body);
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), R.string.toast_no_email_clients_installed, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);//end na  save positive button



        Dialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }


    //tova promenia cveta na butona kato se klikne na nego
    @Override
    public void onShow(DialogInterface dialog) {

        Button positiveButton = ((AlertDialog) dialog)
                .getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setBackgroundResource(R.drawable.custom_dialog_button);

        Button negativeButton = ((AlertDialog) dialog)
                .getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setBackgroundResource(R.drawable.custom_dialog_button);
    }
}
