package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;

/**
 * Created by Victor on 02/03/2015.
 */
public class ChangeUsername extends DialogFragment implements DialogInterface.OnShowListener{
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    protected EditText mChangeUsername;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_change_username,null);
        mContext = inflatedView.getContext();
        mChangeUsername = (EditText) inflatedView.findViewById(R.id.changeUsername);


        builder.setView(inflatedView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = mChangeUsername.getText().toString().trim();
                        if (username.isEmpty()) {
                            //ako edno ot dvete e prazno pokavame error

                            String title = getResources().getString(R.string.general_error_title);
                            String message = getResources().getString(R.string.username_cannot_be_empty);
                            CustomAlertDialog dialogError = new CustomAlertDialog();
                            Bundle dialogContent = new Bundle();
                            dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                            dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                            dialogError.setArguments(dialogContent);
                            dialogError.show(getActivity().getFragmentManager(),"tag_alert_dialog");

                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.general_error_title)
                                    .setMessage(R.string.username_cannot_be_empty)
                                    .setPositiveButton(R.string.ok, null);
                            AlertDialog error = builder.create();
                            error.show();
                            */
                        } else { //ako e vavedeno username updatevame lokalno i v backendless
                            mCurrentUser.setProperty(Statics.KEY_USERNAME, username);
                            Backendless.UserService.setCurrentUser(mCurrentUser);
                            String message = mContext.getResources().getString(R.string.saving_preferences_message);

                            Backendless.UserService.update(mCurrentUser,
                                    new DefaultCallback<BackendlessUser>(mContext, message) {
                                        @Override
                                        public void handleResponse(BackendlessUser backendlessUser) {
                                            super.handleResponse(backendlessUser);
                                            Toast.makeText(mContext, R.string.toast_username_updated, Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            super.handleFault(backendlessFault);
                                            String error = backendlessFault.getMessage();
                                            Toast.makeText(mContext, R.string.general_server_error, Toast.LENGTH_LONG).show();

                                        }
                                    });

                        }

                    }
                })//end na  save positive button

            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ChangeUsername.this.getDialog().cancel();
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
