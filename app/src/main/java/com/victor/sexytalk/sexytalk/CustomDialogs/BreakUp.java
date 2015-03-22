package com.victor.sexytalk.sexytalk.CustomDialogs;

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

import com.victor.sexytalk.sexytalk.Adaptors.AdapterExistingPartners;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

/**
 * Created by Victor on 22/03/2015.
 */
public class BreakUp extends DialogFragment implements DialogInterface.OnShowListener {
    protected TextView mMessage;
    protected  Listener mListener;
    protected int positionToRemove;

    //sluzhi za razmeniane na info m/u dialog box i array adaptor
    public void setListener(Listener listener) {
        mListener = listener;
    }



    public static interface Listener {
        void returnData(int result, int positionToRemove);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_break_up,null);
        mMessage = (TextView) inflatedView.findViewById(R.id.message_dialog);

        if(getArguments().getString(Statics.ALERTDIALOG_MESSAGE) != null) {
            mMessage.setText(getArguments().getString(Statics.ALERTDIALOG_MESSAGE));
        }

        positionToRemove = getArguments().getInt(Statics.BREAK_UP_DIALOG_POSITION_TO_REMOVE);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflatedView)
                .setPositiveButton(R.string.dialog_delete_partner_yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.returnData(AdapterExistingPartners.BREAK_UP_YES,positionToRemove);
                        }

                        dismiss();

                    }
                })
                .setNegativeButton(R.string.dialog_delete_partner_no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    dismiss();
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
