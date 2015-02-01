package com.victor.sexytalk.sexytalk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.Helper.FileHelper;
import com.victor.sexytalk.sexytalk.Helper.UploadPicture;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class EditProfileActivity extends ActionBarActivity {
    protected Toolbar toolbar;
    protected  MyFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);

        //fragment
        fragment = new MyFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Clicked",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    NACHALO NA FRAGMENTA S LIST
     */
    public static class MyFragment extends ListFragment {
        public static int MEDIA_TYPE_IMAGE = 111;
        public static int CHOOSE_PHOTO_REQUEST = 222;
        public static int TAKE_PHOTO_REQUEST = 333;
        protected String mMessageType;
        protected Uri mMediaUri;
        public static final int FILE_SIZE_LIMIT = 1024*1024*10;

        protected ListView editProfileOptionsList;
        protected Toolbar toolbar;
        protected ImageView profilePicture;
        protected BackendlessUser mCurrentUser;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View inflatedView = inflater.inflate(R.layout.activity_edit_profile, container, false);
            profilePicture = (ImageView) inflatedView.findViewById(R.id.profilePicture);
            toolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            ((EditProfileActivity) getActivity()).setSupportActionBar(toolbar);

            return inflatedView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            editProfileOptionsList = getListView();
            ArrayAdapter<CharSequence> arrayAdapter =
                    ArrayAdapter.createFromResource(getActivity(), R.array.edit_profile_options, android.R.layout.simple_spinner_dropdown_item);

            editProfileOptionsList.setAdapter(arrayAdapter);

            if(Backendless.UserService.CurrentUser() != null) {
                mCurrentUser = Backendless.UserService.CurrentUser();
                //zarezdame profile pic, ako ima takava
                if(! mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH).equals("")) {
                    //ako ima profile pic ia zarezdame s picaso
                    String imageUrl = mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH).toString();
                    Picasso.with(getActivity()).load(imageUrl).into(profilePicture);
                }
            }


        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            UploadPicture help = new UploadPicture(getActivity());
            if (resultCode == RESULT_OK) {
                if (requestCode == CHOOSE_PHOTO_REQUEST ) {
                    //tova e sluchaia v koito izbirame photo ot galeriata
                    if (data == null) {
                        Toast.makeText(getActivity(), R.string.general_error_message, Toast.LENGTH_LONG).show();
                    } else {
                        mMediaUri = data.getData();
                    }
                    //parvo proveriavame razmera
                    if (help.checkFileSizeExceedsLimit(FILE_SIZE_LIMIT, mMediaUri) == true) {
                        Toast.makeText(getActivity(), R.string.error_file_too_large, Toast.LENGTH_LONG).show();
                        mMediaUri = null;
                        return; //prekratiavame metoda tuk.
                    } else {
                        uploadProfilePicInBackendless();
                    }//krai na else statement
                }
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            switch (position) {
                case 0:
                    return;
                case 1:
                    return;
                case 2:
                    return;
                case 3:
                    //change profile picture

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.menu_camera_alertdialog_title);
                    builder.setItems(R.array.camera_choices, mUploadPicture);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;

            }

        }
        /*
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        HELPER METODI
        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */

        //uploadvane na profile pic v Backendless i updatevane na profila na protrebitelia
        // s patia kam profile kartinkata

        protected void uploadProfilePicInBackendless() {
            UploadPicture help = new UploadPicture(getActivity());
            //1. uploadvame profile picture v backendless
            //2. updatevame user s patia kam profile picture


            Bitmap profilePictureBitmap = help.createThumbnail(mMediaUri);
            profilePicture.setImageBitmap(profilePictureBitmap);

            String fileName = "";
            if (mMediaUri != null && mMessageType.equals(Statics.TYPE_IMAGE)) {
                fileName = FileHelper.getFileName(getActivity(),mMediaUri,Statics.TYPE_IMAGE);
            }

            //1. uploadvame profile picture v backendles
            final String uploadingFileMessage = getResources().getString(R.string.uploading_file_message);
            Backendless.Files.Android.upload(profilePictureBitmap,
                    Bitmap.CompressFormat.PNG, 50 ,fileName,"profilePictures",
                    new DefaultCallback<BackendlessFile>(getActivity(),uploadingFileMessage) {
                        @Override
                        public void handleResponse(BackendlessFile backendlessFile) {
                            super.handleResponse(backendlessFile);

                            //2. zapazvame patia kam profile pic v properties na current user
                            String profilePictureUrl = backendlessFile.getFileURL();
                            mCurrentUser.setProperty(Statics.KEY_PROFILE_PIC_PATH, profilePictureUrl);
                            Backendless.UserService.update(mCurrentUser,
                                    new DefaultCallback<BackendlessUser>(context,uploadingFileMessage) {
                                        @Override
                                        public void handleResponse(BackendlessUser backendlessUser) {
                                            super.handleResponse(backendlessUser);
                                            Toast.makeText(context,
                                                    R.string.profile_pic_uploaded_successfully,Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void handleFault(BackendlessFault backendlessFault) {
                                            super.handleFault(backendlessFault);
                                            Toast.makeText(context,
                                                    R.string.error_associating_profile_pic_with_profile,Toast.LENGTH_LONG).show();
                                        }
                                    });


                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            super.handleFault(backendlessFault);
                            Toast.makeText(context,R.string.error_uploading_profile_pic,Toast.LENGTH_LONG).show();

                        }
                    });


        }

        //onClick listener za uploadvane na snimko
        protected DialogInterface.OnClickListener mUploadPicture =
                new DialogInterface.OnClickListener() {

                    //UploadPicture up = new UploadPicture();

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UploadPicture help = new UploadPicture(getActivity());
                        switch (which) {
                            case 0: //take picture
                                //tova e metod, koito frashta adresa na kartinakata kaot Uri
                                mMediaUri = help.getOutputMediaFileUri();
                                if (mMediaUri == null) {
                                    Toast.makeText(getActivity(), R.string.error_message_toast_external_storage, Toast.LENGTH_LONG).show();
                                } else {
                                    mMessageType = Statics.TYPE_IMAGE;
                                    takePicture();
                                }
                                break;

                            case 1: //choose picture
                                mMessageType = Statics.TYPE_IMAGE;
                                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                choosePhotoIntent.setType("image/*");
                                startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
                                break;

                        }
                    }
                };
                //helper za onClick listener
                public void takePicture( ) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
        }



    }//Krai na internal list fragment

}//krai celia klas
