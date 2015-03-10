package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.victor.sexytalk.sexytalk.R;


/**
 * Created by Victor on 10/03/2015.
 */
public class OneLoveMessageDialog extends DialogFragment {
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

                    }
                })//end na  save positive button


                .setTitle(mContext.getResources().getString(R.string.title_dialog_one_message_per_day));

        return builder.create();

    }
}
