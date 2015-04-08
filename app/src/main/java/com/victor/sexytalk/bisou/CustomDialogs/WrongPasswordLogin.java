package com.victor.sexytalk.bisou.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.victor.sexytalk.bisou.R;

/**
 * Created by Victor on 21/03/2015.
 */
public class WrongPasswordLogin  extends DialogFragment implements DialogInterface.OnShowListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_wrong_password_login,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView)
                .setPositiveButton(R.string.try_again, null)
                .setNeutralButton(R.string.forgot_your_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        ForgotPassword forgotPassword = new ForgotPassword();
                        forgotPassword.show(getActivity().getFragmentManager(), "Welcome");
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

        Button neutralButton = ((AlertDialog) dialog)
                .getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setBackgroundResource(R.drawable.custom_dialog_button);
    }
}
