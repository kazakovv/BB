package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;
import com.victor.sexytalk.sexytalk.UserInterfaces.SendMessage;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Victor on 24/03/2015.
 */
public class LoveMessageTimeRemaining extends DialogFragment implements DialogInterface.OnShowListener {
    TextView timeLeft;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_love_message_time_left,null);
        timeLeft = (TextView) inflatedView.findViewById(R.id.timeLeft);

        final long dateMessageUploaded = getArguments().getLong(Statics.DATE_CREATED_LOVE_MESSAGE);

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        long timeElapsed = (now.getTime() - dateMessageUploaded);
        long timeToWait = 24*60*60*1000; //one dayin millsec 24 hours * 60 min * 60 sec * 10000 mil sec

        long millsUntilYouCanSendANewMessage = timeToWait - timeElapsed ;
        new CountDownTimer(millsUntilYouCanSendANewMessage,1000){

            @Override
            public void onTick(long millisUntilFinished) {


                //formatirane za time
                String timeRemaining =  String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));


                timeLeft.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        }.start();

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }//krai na ok on click

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


    }
}
