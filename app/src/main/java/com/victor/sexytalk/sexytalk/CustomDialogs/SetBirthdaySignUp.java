package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.UserInterfaces.SignUpActivity;

import java.util.Calendar;
import java.util.Date;

public class SetBirthdaySignUp extends DialogFragment implements DialogInterface.OnShowListener {
    protected DatePicker mBirthday;
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    private OnCompleteListener mListener;

    public SetBirthdaySignUp() {
        //empty constructor required for dialog fragment
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        if(Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_set_birthday,null);
        mContext = inflatedView.getContext();
        mBirthday = (DatePicker) inflatedView.findViewById(R.id.birthDate);
        mBirthday.setMaxDate(new Date().getTime());
        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Calendar birthDate = Calendar.getInstance();
                        birthDate.set(mBirthday.getYear(), mBirthday.getMonth(), mBirthday.getDayOfMonth());
                        SignUpActivity activity = (SignUpActivity) getActivity();
                        //predavame stoinostta
                        activity.onComplete(birthDate.getTime());
                        dismiss();
                    }//krai na on click ok button

                });//end ok button



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


    //interface za pass value to SignUp Activity
    public static interface OnCompleteListener {
        public abstract void onComplete(Date dateOfBirth);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
