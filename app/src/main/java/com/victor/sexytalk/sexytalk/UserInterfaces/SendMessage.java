package com.victor.sexytalk.sexytalk.UserInterfaces;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.CustomDialogs.AttachPictureToMessage;
import com.victor.sexytalk.sexytalk.CustomDialogs.CustomAlertDialog;
import com.victor.sexytalk.sexytalk.CustomDialogs.OneLoveMessageDialog;
import com.victor.sexytalk.sexytalk.Helper.ImageHelper;
import com.victor.sexytalk.sexytalk.Helper.BackendlessMessage;
import com.victor.sexytalk.sexytalk.Helper.SharedPrefsHelper;
import com.victor.sexytalk.sexytalk.Main;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SendMessage extends ActionBarActivity implements AttachPictureToMessage.Listener {
    protected BackendlessUser mCurrentUser;
    protected Context mContext;

    protected EditText messageToSend;
    protected TextView mSendMessageTo;
    public String mMessageType;
    protected Toolbar toolbar;

    protected ArrayList<String> backendlessUserNames; //spisak s Usernames na poluchatelite na saobshtenieto
    protected ArrayList<String> backendlessRecipientEmails; //spisak s emails na poluchatelite na saobshtenieto

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int ACTIVITY_SEND_TO = 11;
    //mMediaUri e statichna promenliva, za da moze da e dostapna ot AttachPictureToMessage dialog, kadeto
    //ia zadavame, kogato iskame da snimame s kamerata
    protected  Uri mMediaUri;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; //1024*1024 = 1MB

    protected MenuItem mRotateLeft;
    protected MenuItem mRotateRight;
    protected ImageView imageViewForThumbnailPreview;


    @Override
    //metod koito se vika kogato niakoi Intent varne rezultat
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            //tuk se obrabotva rezultata ot sendTo activity (na kogo izprashtame saobshtenieto)
            //Ima problem, zashtoto sled tova preprashta kam kraia na metoda i dava general error message

            if (requestCode == ACTIVITY_SEND_TO) {

                backendlessUserNames = data.getStringArrayListExtra(Statics.KEY_USERNAME);
                backendlessRecipientEmails = data.getStringArrayListExtra(Statics.KEY_RECEPIENT_EMAILS);
                String message = constructListOfRecepeintsAsStringTo(backendlessUserNames);
                mSendMessageTo.setText(message);
                return; //ne prodalzhavame natatak s metoda

            }


            //tova obrabotva rezultata ot snimane ili kachvane na file
            if (requestCode == CHOOSE_PHOTO_REQUEST) {
                mMessageType = Statics.TYPE_IMAGE_MESSAGE;

                //pokazvame dvete vratki za snimkite
                mRotateRight.setVisible(true);
                mRotateLeft.setVisible(true);

                //tova e sluchaia v koito izbirame photo ot galeriata
                if (data == null) {

                    Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();

                }

            } else {
                mMessageType = Statics.TYPE_IMAGE_MESSAGE;

                //pokazvame dvete vratki za snimkite
                mRotateRight.setVisible(true);
                mRotateLeft.setVisible(true);


                //dobaviame snimkata kam galeriata
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
            Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_LONG).show();
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
        if (requestCode == CHOOSE_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mMediaUri);
            } catch (Exception e) {
                //handle exception here
                Toast.makeText(SendMessage.this, R.string.error_loading_thumbnail, Toast.LENGTH_LONG).show();
                Log.d("Vic", "Error loading thumbnail" + e.toString());
            }
            int initialWidth = bitmap.getWidth();
            int initalHeight = bitmap.getHeight();
            int newWidth = 0;
            int newHeight = 0;

            //izchisliavame novite proporcii
            float ratio = (float) initialWidth / initalHeight;
            newWidth = 800;
            newHeight = (int) (800 * ratio);

            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, newWidth, newHeight);

        } else { //ako ne e photo triabva da e video

            thumbnail = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(
                    mMediaUri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND), 800, 500);
        }

        imageViewForThumbnailPreview = (ImageView) findViewById(R.id.thumbnailPreview);

        //zavartame thumbnail, ako ima nuzda
        if(thumbnail != null && mMediaUri !=null) {
            thumbnail = ImageHelper.rotateImageIfNeeded(mContext, mMediaUri, thumbnail);
        }
        imageViewForThumbnailPreview.setImageBitmap(thumbnail);
        if (thumbnail == null) {
            //ako thumbnail e null zadavame default kartinka
            imageViewForThumbnailPreview.setImageResource(R.drawable.ic_action_picture);
        }
    }

    protected String constructListOfRecepeintsAsStringTo(ArrayList<String> users) {
        String message;
        String listOfUsers = "";

        int size = users.size();
        if (size == 0) {
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
        //setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        //toolbar.setLogo(R.drawable.launch_icon);
        setSupportActionBar(toolbar);
        mContext = SendMessage.this;
        if (Backendless.UserService.CurrentUser() != null) {
            mCurrentUser = Backendless.UserService.CurrentUser();
        }


        //Izbirane na poluchateli na saobshtenieto
        mSendMessageTo = (TextView) findViewById(R.id.sendTo);
        mSendMessageTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SendMessage.this, SendTo.class);
                intent.putExtra(Statics.TYPE_TEXTMESSAGE, true);
                startActivityForResult(intent, ACTIVITY_SEND_TO);
            }
        });



        //pokazvame saobshtenie che moze da se izprashta 1 sabshtenie na den
        if(SharedPrefsHelper.displayOneLoveMessagePerDayDialog(mContext) != true) {
            DialogFragment oneLoveMessageDialog = new OneLoveMessageDialog();
            oneLoveMessageDialog.show(getSupportFragmentManager(), "Welcome");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        messageToSend = (EditText) findViewById(R.id.messageToSend);
        mRotateLeft = menu.findItem(R.id.action_rotate_left);
        mRotateRight = menu.findItem(R.id.action_rotate_right);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int rotationAngle;
        if (id == R.id.action_rotate_left) {
            rotationAngle = (int) imageViewForThumbnailPreview.getRotation();
            imageViewForThumbnailPreview.setRotation(rotationAngle - 90);
        }
        if (id == R.id.action_rotate_right) {
            rotationAngle = (int) imageViewForThumbnailPreview.getRotation();
            imageViewForThumbnailPreview.setRotation(rotationAngle + 90);
        }


        if (id == R.id.photoMenu) {
            AttachPictureToMessage attachPictureToMessage = new AttachPictureToMessage();
            //izpolzvame interface za prehvarliane na outputUri mezhdu AttachPictureToMessage dialog i SendMessage activity
            attachPictureToMessage.setListener(this);
            attachPictureToMessage.show(getSupportFragmentManager(),"tag_attach");
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
            builder.setTitle(R.string.menu_camera_alertdialog_title);
            builder.setItems(R.array.camera_choices, mUploadPicture);
            AlertDialog dialog = builder.create();
            dialog.show();
            */
        }


        if (id == R.id.action_send) {
            //proverka dali ima recepients i dali ima text
            if (messageToSend.getText().toString().equals("")) {
                Toast.makeText(this, R.string.toast_no_love_message, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }

            //proveriavame dali poleto za tova do kogo da izpratim saobsthenieto e prazno
            //Ako e ravno na parvonachalnata stoinost To:___, znachi ne sa izprani poluchateli
            String toMessage = mSendMessageTo.getText().toString();
            String test = getResources().getString(R.string.send_message_to);
            if (toMessage.equals(test)) {
                Toast.makeText(this, R.string.toast_no_recepients, Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }

            final List<BackendlessUser> recipients = new ArrayList<BackendlessUser>();
            for(String email : backendlessRecipientEmails) {
                recipients.add(BackendlessMessage.findBackendlessUserByEmail(mCurrentUser,email));

            }

            //izprashtame message
            final Messages message = new Messages();
            message.setSender(Backendless.UserService.CurrentUser());
            message.setLoveMessage(messageToSend.getText().toString());
            message.setRecepients(recipients);
            message.setSederUsername((String) mCurrentUser.getProperty(Statics.KEY_USERNAME));
            message.setRecepientEmails(backendlessRecipientEmails.get(0)); //ima samo 1 poluchatel.
            //zadavame tipa na saobshtenieto, ako ne e zadadeno veche, triabva da e samo text
            if (mMessageType == null) {
                mMessageType = Statics.TYPE_TEXTMESSAGE;
            }
            message.setMessageType(mMessageType);

            //ako saobshtenieto e Image ili Video, go izprashtame
            //Parvo uploadvame file, posle izprashteme i saboshtenieto
            //uploadvame file, ako ima takav
            if (mMessageType.equals(Statics.TYPE_IMAGE_MESSAGE)) {
                //ako image uploadvame file na servera
                //razbiva fila na array ot bitove, za da go smalim i kachim na servera

                byte[] imageToUpload = ImageHelper.getByteArrayFromFile(SendMessage.this, mMediaUri);
                String path = "";

                //ako e image go smaliavame
                if (imageToUpload != null && mMessageType.equals(Statics.TYPE_IMAGE_MESSAGE)) {
                    imageToUpload = ImageHelper.reduceImageForUpload(imageToUpload, Statics.SHORT_SIDE_TARGET_PIC);
                    //zavartame image, ako ima nuzda
                    Bitmap temp = BitmapFactory.decodeByteArray(imageToUpload, 0, imageToUpload.length);
                    temp = ImageHelper.rotateImageIfNeeded(mContext,mMediaUri,temp);
                    imageToUpload = ImageHelper.convertBitmapToByteArray(temp);
                    path = "/pics/" +
                            ImageHelper.getFileName(SendMessage.this, mMediaUri, Statics.TYPE_IMAGE_MESSAGE);
                }


                //kachvame file na servera
                final String sendingMessage = mContext.getString(R.string.sending_message);
                final String finalPath = path;//kopirame path kam image
                Backendless.Files.saveFile(path, imageToUpload, true, new DefaultCallback<String>(mContext, sendingMessage) {
                    @Override
                    public void handleResponse(String s) {
                        super.handleResponse(s);
                        message.setMediaUrl(s);
                        message.setBackendlessFilePath(finalPath);
                        //filat e kachen na servera. izprashtame saobshtenieto

                        Backendless.Persistence.save(message, new DefaultCallback<Messages>(mContext, sendingMessage) {
                            @Override
                            public void handleResponse(Messages messages) {
                                super.handleResponse(messages);
                                //Toast se pokazva ot helper metoda SendPushMessage
                                //Toast.makeText(SendMessage.this,
                                //      R.string.message_successfully_sent, Toast.LENGTH_LONG).show();

                                //izprashtame push message
                                for (BackendlessUser recipient : recipients) {
                                    //ako ne sa prazni izprashtame push message
                                    BackendlessMessage.sendPush(mCurrentUser, recipient, message,
                                            mContext, Statics.TYPE_IMAGE_MESSAGE);
                                    switchToMainScreen();
                                }//krai na send push

                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                super.handleFault(backendlessFault);
                                Log.d("Vic", "error sending message " + backendlessFault.toString());
                                String title = getResources().getString(R.string.error_title);
                                String message = getResources().getString(R.string.error_sending_message);
                                CustomAlertDialog dialogError = new CustomAlertDialog();
                                Bundle dialogContent = new Bundle();
                                dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                                dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                                dialogError.setArguments(dialogContent);
                                dialogError.show(getFragmentManager(),"tag_alert_dialog");
                                /*
                                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
                                builder.setMessage(R.string.error_sending_message)
                                        .setTitle(R.string.error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                */
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        super.handleFault(backendlessFault);

                        String title = getResources().getString(R.string.error_title);
                        String message = getResources().getString(R.string.error_sending_message);
                        CustomAlertDialog dialogError = new CustomAlertDialog();
                        Bundle dialogContent = new Bundle();
                        dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                        dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                        dialogError.setArguments(dialogContent);
                        dialogError.show(getFragmentManager(),"tag_alert_dialog");
                        /*
                        Log.d("Vic", "error sending message " + backendlessFault.toString());
                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
                        builder.setMessage(R.string.error_sending_message)
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        */
                    }
                });
            } //krai na send image message


            //izprashtame saobshtenieto
            final String sendingMessage = mContext.getString(R.string.sending_message);
            if (mMessageType.equals(Statics.TYPE_TEXTMESSAGE)) {
                Backendless.Persistence.save(message, new DefaultCallback<Messages>(mContext, sendingMessage) {
                    @Override
                    public void handleResponse(Messages messages) {
                        super.handleResponse(messages);
                        //Toast se pokazva ot helper metoda send push message
                        //Toast.makeText(mContext, R.string.message_successfully_sent, Toast.LENGTH_LONG).show();


                        //izprashtame push message
                        for (BackendlessUser recipient : recipients) {
                            //ako ne sa prazni izprashtame push message
                            BackendlessMessage.sendPush(mCurrentUser, recipient, message, mContext, Statics.TYPE_TEXTMESSAGE);
                            switchToMainScreen();
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        super.handleFault(backendlessFault);
                        Log.d("Vic", "error sending message " + backendlessFault.toString());
                        String title = getResources().getString(R.string.error_title);
                        String message = getResources().getString(R.string.error_sending_message);
                        CustomAlertDialog dialogError = new CustomAlertDialog();
                        Bundle dialogContent = new Bundle();
                        dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                        dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                        dialogError.setArguments(dialogContent);
                        dialogError.show(getFragmentManager(),"tag_alert_dialog");

                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
                        //builder.setMessage(R.string.error_sending_file)
                        builder.setMessage(R.string.error_sending_message)
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        */
                    }
                });
            }//krai na send text message

        }//krai na send koda


        return super.onOptionsItemSelected(item);
    }

     //Interface
    //vrashta rezultata ot dialogovata kutia za zakachane na snimka ot galeriata ili snimane s kamerata
    @Override
    public void returnData(int result, Uri mMediaOutputUri) {
        //vzimame output Uri ot dialog box.
        //problemat e che kogato se snima s kamerata outputUri, koeto se dobavia kam activity e null
        //zatova triabva da go zapazim kato promenliva kato izberam da snimame i posle da prehvarim taya promenliva kam activity
        //kogato izbirame kartinka ot galeriata outputUri moze da se prehvarlia s intent
        if(result == TAKE_PHOTO_REQUEST) {
            mMediaUri = mMediaOutputUri;
        }
    }

    /*
            Helper metodi
     */



    protected String constructWhereClause() {
        String whereClause = "";
        int numberOfRecepients = backendlessRecipientEmails.size();
        for (int i = 0; i < numberOfRecepients; i++) {
            whereClause = whereClause + "email=";
            whereClause = whereClause + "'" + backendlessRecipientEmails.get(i) + "'";

            if (i < numberOfRecepients - 1) {
                whereClause = whereClause + " OR ";
            }
        }

        return whereClause;
    }

    protected Bitmap createBitmapFromUri() {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mMediaUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void switchToMainScreen() {
        //Switch to main screen while waiting for the message to be sent.
        Intent intent = new Intent(SendMessage.this, Main.class);
        //dobaviame flagove, za da ne moze usera da se varne pak kam toya ekran
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
