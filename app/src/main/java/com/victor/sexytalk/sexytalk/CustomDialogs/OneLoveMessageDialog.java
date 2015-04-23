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
import android.widget.CheckBox;

import com.victor.sexytalk.sexytalk.Helper.SharedPrefsHelper;
import com.victor.sexytalk.sexytalk.R;


/**
 * Created by Victor on 10/03/2015.
 */
public class OneLoveMessageDialog extends DialogFragment implements DialogInterface.OnShowListener {
    protected Context mContext;
    protected CheckBox doNotShowAgain;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_one_message_per_day_warning,null);
        mContext = inflatedView.getContext();
        doNotShowAgain  = (CheckBox) inflatedView.findViewById(R.id.checkBoxNotShowAgain);


        builder.setView(inflatedView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cehck dali ne iskame da se pokazva nikoga i ako da zapisvame v shared prefs
                        if (doNotShowAgain.isChecked()) {
                            SharedPrefsHelper.doNotShowOneMessagePerDay(mContext, true);

                        }
                    }
                });//end na  save positive button



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
