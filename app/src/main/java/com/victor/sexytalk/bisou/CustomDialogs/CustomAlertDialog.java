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
import android.widget.TextView;

import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;

/**
 * Created by Victor on 16/03/2015.
 */
public class CustomAlertDialog extends DialogFragment implements DialogInterface.OnShowListener {
    protected TextView mTitle;
    protected TextView mMessage;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_custom_alert,null);
        mTitle = (TextView) inflatedView.findViewById(R.id.titleDialog);
        mMessage = (TextView) inflatedView.findViewById(R.id.message_dialog);

        if(getArguments().getString(Statics.ALERTDIALOG_TITLE) != null) {
            mTitle.setText(getArguments().getString(Statics.ALERTDIALOG_TITLE));
        }
        if(getArguments().getString(Statics.ALERTDIALOG_MESSAGE) != null) {
            mMessage.setText(getArguments().getString(Statics.ALERTDIALOG_MESSAGE));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomAlertDialog.this.getDialog().cancel();

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

       /* Button negativeButton = ((AlertDialog) dialog)
                .getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setBackgroundResource(R.drawable.custom_dialog_button);*/
    }


}
