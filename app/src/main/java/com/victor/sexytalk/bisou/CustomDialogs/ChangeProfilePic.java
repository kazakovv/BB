package com.victor.sexytalk.bisou.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.victor.sexytalk.bisou.Helper.UploadPicture;
import com.victor.sexytalk.bisou.R;
import com.victor.sexytalk.bisou.Statics;

/**
 * Created by Victor on 15/03/2015.
 */
public class ChangeProfilePic extends DialogFragment implements DialogInterface.OnShowListener {
    protected RadioButton mTakePic;
    protected RadioButton mChoosePic;
    protected Context mContext;
    protected Uri mMediaUriOutputTakePic;
    protected String mMessageType;
    protected BackendlessUser mCurrentUser;

    public static int CHOOSE_PHOTO_REQUEST = 222;
    public static int TAKE_PHOTO_REQUEST = 333;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mCurrentUser = Backendless.UserService.CurrentUser();
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_change_profile_pic,null);
        mContext = inflatedView.getContext();
        mTakePic = (RadioButton) inflatedView.findViewById(R.id.takePicture);
        mChoosePic = (RadioButton) inflatedView.findViewById(R.id.choosePicture);
        mTakePic.setChecked(true);

        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChoosePic.isChecked()){
                    mChoosePic.setChecked(false);
                }
            }
        });

        mChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTakePic.isChecked()){
                    mTakePic.setChecked(false);
                }
            }
        });

        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        UploadPicture help = new UploadPicture(mContext);

                        if (mTakePic.isChecked()) {

                            mMediaUriOutputTakePic = help.getOutputMediaFileUri();
                            if (mMediaUriOutputTakePic == null) {
                                Toast.makeText(mContext, R.string.error_message_toast_external_storage, Toast.LENGTH_LONG).show();
                            } else {
                                mMessageType = Statics.TYPE_IMAGE_MESSAGE;
                                takePicture();
                            }
                        } else {
                            //choose pic
                            mMessageType = Statics.TYPE_IMAGE_MESSAGE;
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            getActivity().startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);

                        }

                    }

                })//end ok button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeProfilePic.this.getDialog().cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;

    }
    //helper za onClick
    public void takePicture( ) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUriOutputTakePic);

        getActivity().startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if ( data == null ) {
            //ako e choose photo request vrashta null znachi ima greshka
            //take photo vrashta null po podrazbirane.
            // V tozi sluchai izpolzvame mMediaUriOutputTakePic
            if(requestCode == CHOOSE_PHOTO_REQUEST) {
                Toast.makeText(mContext, R.string.general_error_message, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            //ako ne e null, znachi sme izbrali da izberem snimka ot galeriata.
            // V takav sluchai vzmimame output
            mMediaUriOutputTakePic = data.getData();
        }

        //uploadvame v backendless
        UploadPicture help = new UploadPicture(mContext);
        help.uploadProfilePicInBackendless(mMediaUriOutputTakePic, mCurrentUser);


    } //krai na onActivity result


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
