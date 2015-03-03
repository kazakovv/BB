package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.Calendar;
import java.util.Date;

public class SetBirthdaySignUp extends DialogFragment {
    protected DatePicker mBirthday;
    protected BackendlessUser mCurrentUser;
    protected Context mContext;
    private OnCompleteListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
                        dismiss();
                    }//krai na on click ok button

                })//end ok button

        .setTitle(mContext.getResources().getString(R.string.choose_date_of_birth));

        return builder.create();
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
