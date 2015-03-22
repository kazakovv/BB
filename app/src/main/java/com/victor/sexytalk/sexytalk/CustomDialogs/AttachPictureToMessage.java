package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.backendless.BackendlessUser;
import com.victor.sexytalk.sexytalk.Helper.UploadPicture;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;
import com.victor.sexytalk.sexytalk.UserInterfaces.SendMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Victor on 21/03/2015.
 */
public class AttachPictureToMessage  extends DialogFragment implements DialogInterface.OnShowListener{
    protected RadioButton mTakePic;
    protected RadioButton mChoosePic;
    protected Context mContext;
    protected BackendlessUser mCurrentUser;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SendMessage.mMediaUri=null;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_attach_picture_to_message,null);

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
                        if (mTakePic.isChecked()) {
                            //snimame
                            SendMessage.mMediaUri = getOutputMediaFileUri(SendMessage.MEDIA_TYPE_IMAGE); //tova e metod, koito e definiran po-dolu
                            if (SendMessage.mMediaUri == null) {
                                Toast.makeText(mContext, R.string.error_message_toast_external_storage, Toast.LENGTH_LONG).show();
                            } else {
                                takePicture();
                            }
                        } else {
                            //izbirame pic ot galeriata na telefona
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            getActivity().startActivityForResult(choosePhotoIntent, SendMessage.CHOOSE_PHOTO_REQUEST);


                        }//krai na else za izbirane na pik ot galeriata
                    }//krai na ok on click

                })//end ok button
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AttachPictureToMessage.this.getDialog().cancel();
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

    /*
    HELPER METODI
     */

    private Uri getOutputMediaFileUri(int mediaType) {
        //parvo triabva da se proveri dali ima external storage

        if (isExternalStorageAvailable()) {

            //sled tova vrashtame directoriata za pictures ili ia sazdavame
            //1.Get external storage directory
            String appName = mContext.getString(R.string.app_name);
            String environmentDirectory; //
            //ako snimame picture zapismave v papkata za kartiniki, ako ne v papkata za Movies


            environmentDirectory = Environment.DIRECTORY_PICTURES;
            File mediaStorageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(environmentDirectory),
                    appName);

            //2.Create subdirectory if it does not exist
            if (!mediaStorageDirectory.exists()) {
                if (!mediaStorageDirectory.mkdirs()) {
                    return null;
                }
            }

            //3.Create file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File mediaFile;
            mediaFile = new File(mediaStorageDirectory.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");


            //4.Return the file's URI
            return Uri.fromFile(mediaFile);

        } else //ako niama external storage
            Log.d("Vic", "no external strogage, mediaUri si null");
        return null;

    }


    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void takePicture() {

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, SendMessage.mMediaUri);
        getActivity().startActivityForResult(takePhotoIntent, SendMessage.TAKE_PHOTO_REQUEST);
    }


}
