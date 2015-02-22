package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.Calendar;

public class ForgotPassword extends DialogFragment {
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    protected EditText mEmailForPasswordRecovery;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_forgot_password,null);
        mContext = inflatedView.getContext();
        mEmailForPasswordRecovery = (EditText) inflatedView.findViewById(R.id.emailForPasswordRecovery);

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String email = mEmailForPasswordRecovery.getText().toString().trim();
                        if(!email.isEmpty()) {
                            //izprashtame email s password recovery
                            Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void aVoid) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle(R.string.title_recovery_password_dialog)
                                            .setMessage(R.string.recovery_email_sent_dialog)
                                            .setPositiveButton(R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    String error = backendlessFault.getCode();
                                    //niama nameren takav email
                                    if(error.equals(Statics.BACKENDLESS_INVALID_EMAIL_PASSWORD_RECOVERY)) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(R.string.general_error_title)
                                                .setMessage(R.string.email_not_found)
                                                .setPositiveButton(R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    } else {
                                        //niakakva druga greshka
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(R.string.general_error_title)
                                                .setMessage(R.string.general_server_error)
                                                .setPositiveButton(R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                        }
                    }//krai na on click ok button

                })//end ok button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ForgotPassword.this.getDialog().cancel();
                    }
                })
                .setTitle(mContext.getResources().getString(R.string.forgot_your_password));

        return builder.create();

    }


}