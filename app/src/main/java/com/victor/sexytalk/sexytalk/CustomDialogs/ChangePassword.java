package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.Calendar;

public class ChangePassword extends DialogFragment implements DialogInterface.OnShowListener {
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    protected EditText mChangePassword;
    protected EditText mConfirmPassword;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_change_password,null);
        mContext = inflatedView.getContext();
        mChangePassword = (EditText) inflatedView.findViewById(R.id.enterPassword);
        mConfirmPassword = (EditText) inflatedView.findViewById(R.id.confirmPassword);

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String password = mChangePassword.getText().toString().trim();
                        String confirmPassword = mConfirmPassword.getText().toString().trim();
                        //check dali ne e vkarana prazna parola
                        if (password.isEmpty() || confirmPassword.isEmpty()) {
                            //ako edno ot dvete e prazno pokavame error

                            String title = getResources().getString(R.string.general_error_title);
                            String message = getResources().getString(R.string.password_empty);
                            CustomAlertDialog dialogError = new CustomAlertDialog();
                            Bundle dialogContent = new Bundle();
                            dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                            dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                            dialogError.setArguments(dialogContent);
                            dialogError.show(getActivity().getFragmentManager(),"tag_alert_dialog");


                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.general_error_title)
                                    .setMessage(R.string.password_empty)
                                    .setPositiveButton(R.string.ok, null);
                            AlertDialog error = builder.create();
                            error.show();
                            */
                        }
                        //check dali 2 paroli savpadat
                        if (!password.equals(confirmPassword)) {
                            //error message che parolite ne savpadat

                            String title = getResources().getString(R.string.general_error_title);
                            String message = getResources().getString(R.string.passwords_dont_match);
                            CustomAlertDialog dialogError = new CustomAlertDialog();
                            Bundle dialogContent = new Bundle();
                            dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                            dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                            dialogError.setArguments(dialogContent);
                            dialogError.show(getActivity().getFragmentManager(),"tag_alert_dialog");
                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.general_error_title)
                                    .setMessage(R.string.passwords_dont_match)
                                    .setPositiveButton(R.string.ok, null);
                            AlertDialog error = builder.create();
                            error.show();
                            */
                        }

                        //vsicko e ok case
                        if (password.equals(confirmPassword) && !password.isEmpty() && !confirmPassword.isEmpty()) {
                            //vsichko e ok, zapisvame v backendless

                            mCurrentUser.setPassword(password);
                            Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    Toast.makeText(mContext, R.string.password_changed_toast, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Toast.makeText(mContext, R.string.password_not_changed_error_toast, Toast.LENGTH_LONG).show();

                                }
                            });
                            //zatvariame kutiata
                            dismiss();
                        }

                    }//krai na on click ok button

                })//end ok button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangePassword.this.getDialog().cancel();
                    }
                });



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
