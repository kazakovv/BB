package com.victor.sexytalk.sexytalk.UserInterfaces;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.squareup.picasso.Picasso;
import com.victor.sexytalk.sexytalk.CustomDialogs.ChangePassword;
import com.victor.sexytalk.sexytalk.CustomDialogs.ChangeUsername;
import com.victor.sexytalk.sexytalk.CustomDialogs.MaleOrFemaleDialog;
import com.victor.sexytalk.sexytalk.CustomDialogs.SetBirthday;
import com.victor.sexytalk.sexytalk.Helper.ImageHelper;
import com.victor.sexytalk.sexytalk.Helper.RoundedTransformation;
import com.victor.sexytalk.sexytalk.Helper.UploadPicture;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


public class EditProfileActivity extends ActionBarActivity {
    protected Toolbar toolbar;
    protected FragmentEditProfileActivity fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fragment
        fragment = new FragmentEditProfileActivity();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }

    /*
    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    NACHALO NA FRAGMENTA S LIST
    !!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    public static class FragmentEditProfileActivity extends ListFragment  implements  DatePickerDialog.OnDateSetListener{
        public static int MEDIA_TYPE_IMAGE = 111;
        public static int CHOOSE_PHOTO_REQUEST = 222;
        public static int TAKE_PHOTO_REQUEST = 333;
        public static int SET_BIRTHDAY = 444;
        public static int CHANGE_PASSWORD = 555;
        public static int CHANGE_USERNAME= 666;
        protected String mMessageType;
        protected Uri mMediaUri;
        public static final int FILE_SIZE_LIMIT = 1024*1024*10;

        protected Boolean userAlreadyHasProfilePic;
        protected String existingProfilePicPath;

        protected ListView editProfileOptionsList;
        protected Toolbar toolbar;
        protected ImageView profilePicture;
        protected BackendlessUser mCurrentUser;
        protected Context mContext;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View inflatedView = inflater.inflate(R.layout.activity_edit_profile, container, false);
            profilePicture = (ImageView) inflatedView.findViewById(R.id.profilePicture);
            toolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            ((EditProfileActivity) getActivity()).setSupportActionBar(toolbar);
            mContext = getActivity();
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
                if( mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null ) {
                    //ako ima profile pic ia zarezdame s picaso
                    userAlreadyHasProfilePic = true;
                    //existingprofilePicPath se izpolzva i v sluchaite, kogato user si smenia profile pic
                    // togava kachvame na servera novata kartinka i izpolzvame tazi promenliva,
                    // za da iztriem starata profile pic ot servera
                    existingProfilePicPath = (String) mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH);
                    Picasso.with(getActivity())
                            .load(existingProfilePicPath)
                            .transform(new RoundedTransformation(Statics.PICASSO_ROUNDED_CORNERS, 0))
                            .into(profilePicture);
                } else {
                    userAlreadyHasProfilePic = false;
                }
            }


        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            UploadPicture help = new UploadPicture(getActivity());
            if (resultCode == RESULT_OK) {
                if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                    if ( data == null ) {
                        //ako e null i sme izbrali photo pokazvame error message
                        if(requestCode == CHOOSE_PHOTO_REQUEST) {
                            Toast.makeText(getActivity(), R.string.general_error_message, Toast.LENGTH_LONG).show();
                        }
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
                    //change sex
                    DialogFragment sexDialog = new MaleOrFemaleDialog();
                    sexDialog.show(getActivity().getFragmentManager(), "Welcome");
                    return;
                case 1:
                    //change date of birth

                    SetBirthday setBirthday = new SetBirthday();
                    setBirthday.setTargetFragment(FragmentEditProfileActivity.this,SET_BIRTHDAY);
                    setBirthday.show(getFragmentManager(),"Welcome");
                    return;
                case 2:
                    //change password

                    ChangePassword changePassword = new ChangePassword();
                    changePassword.setTargetFragment(FragmentEditProfileActivity.this, CHANGE_PASSWORD);
                    changePassword.show(getFragmentManager(),"Welcome");
                    return;
                case 3:
                    //change profile picture

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.menu_camera_alertdialog_title);
                    builder.setItems(R.array.camera_choices, mUploadPicture);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                case 4:
                    //change username
                    ChangeUsername changeUsername = new ChangeUsername();
                    changeUsername.setTargetFragment(FragmentEditProfileActivity.this, CHANGE_USERNAME);
                    changeUsername.show(getFragmentManager(),"Welcome");
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
            //3. ako zamestvame stara profile pic, iztrivame predishnata, za da ne zaema mistno na servera


            //sledvashtite 3 reda imat za cel da namaliat razmera na kartinkata
            //parvo go oravim v array, sled tova namaliavame razmera i sled tova go uploadvame v backendless
            byte[] imageBytes = ImageHelper.getByteArrayFromFile(getActivity(), mMediaUri);
            byte[] reducedImage = ImageHelper.reduceImageForUpload(imageBytes, Statics.SHORT_SIDE_TARGET_THUMBNAIL);
            Bitmap profilePictureBitmap = BitmapFactory.decodeByteArray(reducedImage, 0, reducedImage.length);
            //zavartame profile pic, ama ima nuzhda
            profilePictureBitmap = ImageHelper.rotateImageIfNeeded(mContext, mMediaUri, profilePictureBitmap);

            //Bitmap profilePictureBitmap = help.createThumbnail(mMediaUri);
            profilePicture.setImageBitmap(profilePictureBitmap);

            String fileName = "";

            if (mMediaUri != null && mMessageType.equals(Statics.TYPE_IMAGE_MESSAGE)) {
                    //Zadavame patia kam profile pic
                    fileName = ImageHelper.getFileName(getActivity(), mMediaUri, Statics.TYPE_IMAGE_MESSAGE);

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

                                            //3. iztrivame starata profile pic, ako ima takava
                                            if(userAlreadyHasProfilePic == true) {
                                                Backendless.Files.remove(existingProfilePicPath, new AsyncCallback<Void>() {
                                                    @Override
                                                    public void handleResponse(Void aVoid) {
                                                        //niama nuzda pa pravim nishto
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                        //ako ne ia izptriem samo zaemame izlishno miasnta na servera
                                                    }
                                                });

                                            }
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



        //onClick listener za uploadvane na snimka
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
                                    mMessageType = Statics.TYPE_IMAGE_MESSAGE;
                                    takePicture();
                                }
                                break;

                            case 1: //choose picture
                                mMessageType = Statics.TYPE_IMAGE_MESSAGE;
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


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        }

    }//Krai na internal list fragment

}//krai celia klas
