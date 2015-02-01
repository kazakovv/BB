package com.victor.sexytalk.sexytalk.Helper;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.victor.sexytalk.sexytalk.DefaultCallback;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

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


}
