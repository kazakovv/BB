package com.victor.sexytalk.sexytalk.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Victor on 31/01/2015.
 */
public class UploadPicture {
   protected static int CHOOSE_PHOTO_REQUEST = 11;
   protected Context context;


    public UploadPicture(Context context) {
    this.context = context;
    }

                //tuk zapochvat vatreshni helper metodi za switch statementa

                public Uri getOutputMediaFileUri() {
                    //parvo triabva da se proveri dali ima external storage

                    if (isExternalStorageAvailable()) {

                        //sled tova vrashtame directoriata za pictures ili ia sazdavame
                        //1.Get external storage directory
                        String appName = context.getResources().getString(R.string.app_name);
                        String environmentDirectory; //
                        //ako snimame picture zapismave v papkata za kartiniki, ako ne v papkata za Movies


                        environmentDirectory = Environment.DIRECTORY_PICTURES;
                        File mediaStorageDirectory = new File(
                                Environment.getExternalStoragePublicDirectory(environmentDirectory),
                                appName);

                        //2.Create subdirectory if it does not exist
                        if (! mediaStorageDirectory.exists()) {
                            if (!mediaStorageDirectory.mkdirs()) {
                                Log.e("Vic", "failed to create directory");
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
                        Log.d("Vic","no external strogage, mediaUri si null");
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


    public Bitmap createThumbnail(Uri mMediaUri) {
        //create a thumbnail preview of the image/movie that was selected

        Bitmap bitmap = null;
        Bitmap thumbnail;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mMediaUri);
            } catch (Exception e) {
                //handle exception here
                Toast.makeText(context,R.string.error_loading_thumbnail, Toast.LENGTH_LONG).show();
                Log.d("Vic","Error loading thumbnail" + e.toString());
            }
            int initialWidth = bitmap.getWidth();
            int initalHeight = bitmap.getHeight();
            int newWidth = 0;
            int newHeight = 0;

            //izchisliavame novite proporcii
            float ratio =(float) initialWidth  / initalHeight;
            newWidth = 800 ;
            newHeight = (int) (800 * ratio) ;
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, newWidth, newHeight);

        return thumbnail; //tr da se proveri dali ne e null

    }

    public boolean checkFileSizeExceedsLimit(int fileSizeLimit, Uri mMediaUri) {
        int fileSize = 0;
        InputStream inputStream = null;
        boolean fileSizeExceedsLimit = true;

        try {
            //potvariame izbranoto video i proveriavame kolko e goliamo
            inputStream = context.getContentResolver().openInputStream(mMediaUri);
            fileSize = inputStream.available();

            if (fileSize > fileSizeLimit) {
                fileSizeExceedsLimit = true;
            } else {
                fileSizeExceedsLimit = false;
            }

        } catch (Exception e) {
            Toast.makeText(context, R.string.error_selected_file, Toast.LENGTH_LONG).show();


        } finally {
            try {
                inputStream.close();

            } catch (IOException e) {
                //blank
            }
        }


        return fileSizeExceedsLimit;
    }

    public static Bitmap resizeBitMapImage(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }


    //uploadvane na profile pic v Backendless i updatevane na profila na protrebitelia
    // s patia kam profile kartinkata

    public void uploadProfilePicInBackendless(Uri mMediaUri, final BackendlessUser mCurrentUser) {
        boolean userAlreadyHasProfilePic = false;
        String existingProfilePicPath = null;
        //proveriavame dali usera ima profile pic i ako da updatevame gornite 2 promenlivi
        if(mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH) != null){
            userAlreadyHasProfilePic = true;
            existingProfilePicPath = (String) mCurrentUser.getProperty(Statics.KEY_PROFILE_PIC_PATH);
        }
        //1. uploadvame profile picture v backendless
        //2. updatevame user s patia kam profile picture
        //3. ako zamestvame stara profile pic, iztrivame predishnata, za da ne zaema mistno na servera


        //sledvashtite 3 reda imat za cel da namaliat razmera na kartinkata
        //parvo go oravim v array, sled tova namaliavame razmera i sled tova go uploadvame v backendless
        byte[] imageBytes = ImageHelper.getByteArrayFromFile(context, mMediaUri);
        byte[] reducedImage = ImageHelper.reduceImageForUpload(imageBytes, Statics.SHORT_SIDE_TARGET_THUMBNAIL);
        Bitmap profilePictureBitmap = BitmapFactory.decodeByteArray(reducedImage, 0, reducedImage.length);
        //zavartame profile pic, ama ima nuzhda
        profilePictureBitmap = ImageHelper.rotateImageIfNeeded(context, mMediaUri, profilePictureBitmap);


        String fileName = "";

        if (mMediaUri != null ) {
            //Zadavame patia kam profile pic
            fileName = ImageHelper.getFileName(context, mMediaUri, Statics.TYPE_IMAGE_MESSAGE);

        }

        //1. uploadvame profile picture v backendles
        final String uploadingFileMessage = context.getResources().getString(R.string.uploading_file_message);
        final boolean finalUserAlreadyHasProfilePic = userAlreadyHasProfilePic;
        final String finalExistingProfilePicPath = existingProfilePicPath;
        Backendless.Files.Android.upload(profilePictureBitmap,
                Bitmap.CompressFormat.PNG, 50 ,fileName,"profilePictures",
                new DefaultCallback<BackendlessFile>(context,uploadingFileMessage) {
                    @Override
                    public void handleResponse(BackendlessFile backendlessFile) {
                        super.handleResponse(backendlessFile);

                        //2. zapazvame patia kam profile pic v properties na current user
                        String profilePictureUrl = backendlessFile.getFileURL();
                        mCurrentUser.setProperty(Statics.KEY_PROFILE_PIC_PATH, profilePictureUrl);
                        Backendless.UserService.setCurrentUser(mCurrentUser);
                        Backendless.UserService.update(mCurrentUser,
                                new DefaultCallback<BackendlessUser>(context,uploadingFileMessage) {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        super.handleResponse(backendlessUser);
                                        Toast.makeText(context,
                                                R.string.profile_pic_uploaded_successfully,Toast.LENGTH_LONG).show();

                                        //3. iztrivame starata profile pic, ako ima takava
                                        if(finalUserAlreadyHasProfilePic == true) {
                                            Backendless.Files.remove(finalExistingProfilePicPath, new AsyncCallback<Void>() {
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




}
