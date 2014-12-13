package com.victor.sexytalk.sexytalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SendMessage extends Activity {
    EditText messageToSend;
    ImageView mUploadMedia;
    TextView mSendMessageTo;
    String mMessageType;

    ArrayList<String> backendlessUserNames; //spisak s Usernames na poluchatelite na saobshtenieto
    ArrayList<String> backendlessRecepientEmails; //spisak s emails na poluchatelite na saobshtenieto

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int CHOOSE_VIDEO_REQUEST =3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int ACTIVITY_SEND_TO = 11;

    protected Uri mMediaUri;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //1024*1024 = 1MB

    public static final String TAG = SendMessage.class.getSimpleName();

    //onCLick listener za uload na picture ili video
    protected DialogInterface.OnClickListener mUploadPictureOrVideo =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //take picture
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //tova e metod, koito e definiran po-dolu
                            if (mMediaUri == null) {
                                Toast.makeText(SendMessage.this, R.string.error_message_toast_external_storage, Toast.LENGTH_LONG).show();
                            } else {
                                mMessageType = Statics.TYPE_IMAGE;
                                takePicture();
                            }
                            break;
                        case 1: //take video
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); //tova e metod, koito e definiran po-dolu
                            if (mMediaUri == null) {
                                Toast.makeText(SendMessage.this, R.string.error_message_toast_external_storage, Toast.LENGTH_LONG).show();
                            } else {
                                mMessageType = Statics.TYPE_VIDEO;
                                takeVideo();
                            }
                            break;
                        case 2: //choose picture
                            mMessageType = Statics.TYPE_IMAGE;
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent,CHOOSE_PHOTO_REQUEST);
                            break;
                        case 3: //choose video
                            mMessageType = Statics.TYPE_VIDEO;
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVideoIntent.setType("video/*");
                            Toast.makeText(SendMessage.this,R.string.warning_max_video_size,Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
                            break;
                    }
                }

                //tuk zapochvat vatreshni helper metodi za switch statementa

                private Uri getOutputMediaFileUri(int mediaType) {
                    //parvo triabva da se proveri dali ima external storage

                    if (isExternalStorageAvailable()) {

                        //sled tova vrashtame directoriata za pictures ili ia sazdavame
                        //1.Get external storage directory
                        String appName = SendMessage.this.getString(R.string.app_name);
                        String environmentDirectory; //
                        //ako snimame picture zapismave v papkata za kartiniki, ako ne v papkata za Movies

                        if(mediaType == MEDIA_TYPE_IMAGE) {
                            environmentDirectory = Environment.DIRECTORY_PICTURES;
                        } else {
                            environmentDirectory = Environment.DIRECTORY_MOVIES;
                        }
                        File mediaStorageDirectory = new File(
                                Environment.getExternalStoragePublicDirectory(environmentDirectory),
                                appName);

                        //2.Create subdirectory if it does not exist
                        if (! mediaStorageDirectory.exists()) {
                            if (!mediaStorageDirectory.mkdirs()) {
                                Log.e(TAG, "failed to create directory");
                                return null;
                            }
                        }

                        //3.Create file name
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File mediaFile;
                        if (mediaType == MEDIA_TYPE_IMAGE) {
                            mediaFile = new File(mediaStorageDirectory.getPath() + File.separator +
                                    "IMG_" + timeStamp + ".jpg");
                        } else if (mediaType == MEDIA_TYPE_VIDEO) {
                            mediaFile = new File(mediaStorageDirectory.getPath() + File.separator +
                                    "MOV_" + timeStamp + ".mp4");
                        } else {
                            return null;
                        }
                        //4.Return the file's URI
                        Log.d(TAG, "File path: " + Uri.fromFile(mediaFile));
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
                private void takePicture() {
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                }

                private void takeVideo() {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                    startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                }
            };


    @Override
    //metod koito se vika kogato niakoi Intent varne rezultat
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            //tuk se obrabotva rezultata ot sendTo activity (na kogo izprashtame saobshtenieto)
            //Ima problem, zashtoto sled tova preprashta kam kraia na metoda i dava general error message

            if (requestCode == ACTIVITY_SEND_TO) {

                backendlessUserNames = data.getStringArrayListExtra(Statics.KEY_USERNAME);
                backendlessRecepientEmails = data.getStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS);
                String message = constructListOfRecepeintsAsStringTo(backendlessUserNames);
                mSendMessageTo.setText(message);
                Log.d("Vic","the message is "+message);
                return; //ne prodalzhavame natatak s metoda

            }


            //tova obrabotva rezultata ot snimane ili kachvane na file
            if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == CHOOSE_VIDEO_REQUEST) {
                //tova e sluchaia v koito izbirame photo ili video ot galeriata
                if (data == null) {
                    Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                }


                if (requestCode == CHOOSE_VIDEO_REQUEST) {
                    //proveriavame dali file size > 10MB

                    if (checkFileSizeExceedsLimit(FILE_SIZE_LIMIT) == true) {
                        Toast.makeText(SendMessage.this, R.string.error_file_too_large, Toast.LENGTH_LONG).show();
                        mMediaUri = null;
                        return; //prekratiavame metoda tuk.
                    }

                }
            } else {

                //dobaviame snimkata ili videoto kam galeriata
                //tova e v sluchaite v koito sme snimali neshto


                //parvo proveriavame razmera
                if (checkFileSizeExceedsLimit(FILE_SIZE_LIMIT) == true) {
                    Toast.makeText(SendMessage.this, R.string.error_file_too_large, Toast.LENGTH_LONG).show();
                    mMediaUri = null;
                    return; //prekratiavame metoda tuk.
                } else {

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); //broadcast intent
                    mediaScanIntent.setData(mMediaUri);
                    sendBroadcast(mediaScanIntent); //broadcast intent

                }
            }

            createThumbnail(requestCode);
        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this,R.string.general_error_message,Toast.LENGTH_LONG).show();
        }

    }

    protected boolean checkFileSizeExceedsLimit(int fileSizeLimit) {
        int fileSize = 0;
        InputStream inputStream = null;
        boolean fileSizeExceedsLimit = true;

        try {
            //potvariame izbranoto video i proveriavame kolko e goliamo
            inputStream = getContentResolver().openInputStream(mMediaUri);
            fileSize = inputStream.available();

            if (fileSize > fileSizeLimit) {
                fileSizeExceedsLimit = true;
            } else {
                fileSizeExceedsLimit = false;
            }

        } catch (Exception e) {
            Toast.makeText(SendMessage.this, R.string.error_selected_file, Toast.LENGTH_LONG).show();


        } finally {
            try {
                inputStream.close();

            } catch (IOException e) {
                //blank
            }
        }


        return fileSizeExceedsLimit;
    }

    protected void createThumbnail(int requestCode) {
        //create a thumbnail preview of the image/movie that was selected

        Bitmap bitmap = null;
        Bitmap thumbnail;
        if(requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mMediaUri);
            } catch (Exception e) {
                //handle exception here
                Toast.makeText(SendMessage.this,R.string.error_loading_thumbnail, Toast.LENGTH_LONG).show();
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

        } else { //ako ne e photo triabva da e video

            thumbnail = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(
                    mMediaUri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND), 800, 500);
        }

        ImageView imageViewForThumbnailPreview = (ImageView) findViewById(R.id.thumbnailPreview);


        imageViewForThumbnailPreview.setImageBitmap(thumbnail);
        if(thumbnail ==null) {
            //ako thumbnail e null zadavame default kartinka
            imageViewForThumbnailPreview.setImageResource(R.drawable.ic_action_picture);
        }
    }

    protected String constructListOfRecepeintsAsStringTo(ArrayList<String> users) {
        String message;
        String listOfUsers = "";

        int size = users.size();
        if(size == 0) {
            message = getString(R.string.send_message_to);
        } else {

            int i = 0;
            for (String user : users) {
                listOfUsers = listOfUsers + " " + user;
                i++;
                if (i != size) {
                    listOfUsers = listOfUsers + ","; //slagame zapetaika m/u users osven sled poslednia
                }
            }
            message = getString(R.string.send_message_to_add_users) + listOfUsers;
        }
        return message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mUploadMedia = (ImageView) findViewById(R.id.uploadPictureOrMovie);

        mUploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
                builder.setTitle(R.string.menu_camera_alertdialog_title);
                builder.setItems(R.array.camera_choices, mUploadPictureOrVideo);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //Izbirane na poluchateli na saobshtenieto
        mSendMessageTo = (TextView) findViewById(R.id.sendTo);
        mSendMessageTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendMessage.this, SendTo.class);
                startActivityForResult(intent,ACTIVITY_SEND_TO);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        messageToSend = (EditText) findViewById(R.id.messageToSend);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_send) {


            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            String whereClause = constructWhereClause();
            dataQuery.setWhereClause(whereClause);
            final List<BackendlessUser> recepients = new ArrayList<BackendlessUser>();

            Backendless.Data.of(BackendlessUser.class).find(dataQuery,
                    new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                @Override
                public void handleResponse(BackendlessCollection<BackendlessUser> collection) {
                    int numberOfUsers = collection.getCurrentPage().size();

                    for (int i = 0; i < numberOfUsers ; i++) {
                        recepients.add(collection.getCurrentPage().get(i));
                    }

                    Messages message = new Messages();
                    message.setSender(Backendless.UserService.CurrentUser());
                    message.setLoveMessage(messageToSend.getText().toString());
                    message.setRecepients(recepients);

                    //zadavame tipa na saobshtenieto, ako ne e zadadeno veche, triabva da e samo text
                    if(mMessageType == null) {
                        mMessageType = Statics.TYPE_TEXTMESSAGE;
                    }
                    message.setMessageType(mMessageType);

                    //izprashtame saobshtenieto
                    Backendless.Persistence.save(message, new AsyncCallback<Messages>() {
                        @Override
                        public void handleResponse(Messages messages) {
                            Toast.makeText(SendMessage.this,R.string.message_successfully_sent,Toast.LENGTH_LONG).show();
                            //izprashtame push notification, che ima novo saobshtenie
                            //!!

                            //Message sent.Switch to main screen.
                            Intent intent = new Intent(SendMessage.this,Main.class);
                            //dobaviame flagove, za da ne moze usera da se varne pak kam toya ekran
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Log.d("Vic","error sending message " + backendlessFault.toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
                            //builder.setMessage(R.string.error_sending_file)
                            builder.setMessage(R.string.error_sending_message)
                                    .setTitle(R.string.error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                Log.d("Vic","Fault");
                }
            });

        }



        return super.onOptionsItemSelected(item);
    }

    protected String constructWhereClause() {
    String whereClause  = "";

        int numberOfRecepients = backendlessRecepientEmails.size();

        for(int i=0; i < numberOfRecepients; i++) {


            whereClause = whereClause + "email=";
            whereClause = whereClause + "'" + backendlessRecepientEmails.get(i) + "'";

            if(i < numberOfRecepients -1) {
            whereClause = whereClause + " OR ";
            }
        }

        return whereClause;
    }

}
