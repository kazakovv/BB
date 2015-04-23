package com.victor.sexytalk.sexytalk.Helper;


import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.BackendlessClasses.KissesCount;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.CustomDialogs.CustomAlertDialog;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Victor on 15/02/2015.
 */
public class BackendlessMessage  {



    /*
        IZPRASHTANE NA PUSH MESSAGES
         */
    public static void sendPush(BackendlessUser sender, BackendlessUser recipient, Messages message,  final Context context, String TYPE_MESSAGE) {

        String deviceID = null;
        String senderUsername = (String) sender.getProperty(Statics.KEY_USERNAME);
        String channel =  recipient.getEmail();
        if(recipient.getProperty(Statics.KEY_DEVICE_ID) != null) {
            deviceID = (String) recipient.getProperty(Statics.KEY_DEVICE_ID);

        }

        String messagePush="";
        String messageType="";
        if(TYPE_MESSAGE.equals(Statics.TYPE_TEXTMESSAGE)) {
            messagePush = senderUsername + " " +  context.getResources().getString(R.string.push_message_love_message);
            messageType = Statics.TYPE_TEXTMESSAGE;
        } if(TYPE_MESSAGE.equals(Statics.TYPE_IMAGE_MESSAGE)) {
            messagePush = senderUsername + " " +  context.getResources().getString(R.string.push_message_love_message);
            messageType = Statics.TYPE_IMAGE_MESSAGE;
        } else if(TYPE_MESSAGE.equals(Statics.TYPE_CALENDAR_UPDATE)) {
            messagePush = senderUsername + " " +  context.getResources().getString(R.string.push_calendar_update);
            messageType = Statics.TYPE_CALENDAR_UPDATE;
        } else if(TYPE_MESSAGE.equals(Statics.TYPE_KISS)) {
            messagePush = senderUsername + " " + context.getResources().getString(R.string.push_receive_a_kiss);
            messageType = Statics.TYPE_KISS;
            //toast se izprashta ot main activity. Ako izprashtam niakolko kiss toast shte se pokazva neprekasnato
            //messageToast = context.getResources().getString(R.string.send_a_kiss_toast_successful);
        } else if( TYPE_MESSAGE.equals(Statics.KEY_PARTNER_REQUEST)) {
            messagePush = senderUsername + " " + context.getResources().getString(R.string.new_partner_request_push);
            messageType = Statics.KEY_PARTNER_REQUEST;
        } else if( TYPE_MESSAGE.equals(Statics.KEY_PARTNER_REQUEST_APPROVED)) {
            messagePush = senderUsername + " " + context.getResources().getString(R.string.partner_request_approved);
            messageType = Statics.KEY_PARTNER_REQUEST_APPROVED;

        } else if( TYPE_MESSAGE.equals(Statics.KEY_UPDATE_SEXY_STATUS)) {
            //zadavame saobshtenieto
            if(sender.getProperty(Statics.KEY_MALE_OR_FEMALE).equals(Statics.SEX_FEMALE)){
                messagePush = sender.getProperty(Statics.KEY_USERNAME) + " " +
                        context.getString(R.string.push_sexy_status_updated_female);
            } else {
                messagePush = sender.getProperty(Statics.KEY_USERNAME) + " " +
                        context.getString(R.string.push_sexy_status_updated_male);
            }
            messageType = Statics.KEY_UPDATE_SEXY_STATUS;
        }
        PublishOptions publishOptions = new PublishOptions();

        publishOptions.putHeader(PublishOptions.ANDROID_TICKER_TEXT_TAG, messagePush);
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TITLE_TAG, context.getResources().getString(R.string.app_name));
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TEXT_TAG, messagePush);
        publishOptions.putHeader(PublishOptions.MESSAGE_TAG,messageType);

        //dobaviame info za message. Tova se pravi, za da moze kato caknem na push notificationa da se otvori
        if(message != null){
            if(TYPE_MESSAGE.equals(Statics.TYPE_TEXTMESSAGE)) {
                publishOptions.putHeader(Statics.KEY_LOVE_MESSAGE, message.getLoveMessage());
                publishOptions.putHeader(Statics.KEY_USERNAME_SENDER, message.getSenderUsername());
                publishOptions.putHeader(Statics.KEY_MESSAGE_ID, message.getObjectId());

            } else if(TYPE_MESSAGE.equals(Statics.TYPE_IMAGE_MESSAGE)) {
                publishOptions.putHeader(Statics.KEY_URL, message.getMediaUrl());
                publishOptions.putHeader(Statics.KEY_LOVE_MESSAGE, message.getLoveMessage());
                publishOptions.putHeader(Statics.KEY_USERNAME_SENDER, message.getSenderUsername());
                publishOptions.putHeader(Statics.KEY_MESSAGE_ID, message.getObjectId());

            } else if(TYPE_MESSAGE.equals(Statics.TYPE_KISS)){
                publishOptions.putHeader(Statics.KEY_LOVE_MESSAGE, message.getLoveMessage());
                publishOptions.putHeader(Statics.KEY_USERNAME_SENDER, message.getSenderUsername());
                publishOptions.putHeader(Statics.KEY_NUMBER_OF_KISSES, String.valueOf(message.getKissNumber()));
            }
        }


        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
        if(deviceID !=null) {
            deliveryOptions.addPushSinglecast(deviceID);


            Backendless.Messaging.publish(channel, TYPE_MESSAGE, publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus messageStatus) {

                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
        }//krai na check dali deviceId e null
    }//krai na send push
    /*
    IZPRASHTANE NA KISS
     */
    public static void sendKissMessage(final BackendlessUser mCurrentUser,
                                       final String recipientEmail,
                                       final String deviceId,
                                       final Context context,
                                       final Activity activity ){

        final BackendlessUser recipientBackendlessUser = BackendlessMessage.findBackendlessUserByEmail(mCurrentUser,recipientEmail );

        //message
        //1. parvo tarsim kolko celuvki sa izprateni veche
        //2. sastaviame kiss i go izprashtame
        //3. uvelichavame broia na izpratenite celuvki

        //1. TARSIM BROI CELUVKI, KOITO SA IZPRATENI DOSEGA
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = "senderEmail='" + mCurrentUser.getEmail() + "'" + " AND "
                + "receiverEmail='" + recipientEmail +"'";
        dataQuery.setWhereClause(whereClause);
        final BackendlessUser finalRecepientBackendlessUser = recipientBackendlessUser;
        Backendless.Data.of(KissesCount.class).find(dataQuery, new AsyncCallback<BackendlessCollection<KissesCount>>() {
            @Override
            public void handleResponse(final BackendlessCollection<KissesCount> kissesSent) {

                int kissesSentAlready = 0;
                //check dali veche sme prashtali celuvki
                if(kissesSent.getCurrentPage().size() > 0) {
                    kissesSentAlready  = kissesSent.getCurrentPage().get(0).getNumberOfKisses();
                }
                final int kissNumber;
                if (kissesSentAlready > 0) {
                    kissNumber = kissesSentAlready + 1;
                } else {
                    kissNumber = 1;
                }

                //2. IZPRASHTAME KISS SAOBSHTENIETO

                String someoneSendsYouAKiss = mCurrentUser.getProperty(Statics.KEY_USERNAME) +
                        " " + context.getResources().getString(R.string.send_a_kiss_message);

                //sazdavame saobshtenieto
                final Messages kissMessage = new Messages();
                kissMessage.setMessageType(Statics.TYPE_KISS);
                kissMessage.setLoveMessage(someoneSendsYouAKiss);
                kissMessage.setRecepientEmails(recipientEmail);
                kissMessage.setSederUsername((String) Backendless.UserService.CurrentUser().getProperty(Statics.KEY_USERNAME));
                kissMessage.setSender(Backendless.UserService.CurrentUser());
                kissMessage.setKissNumber(kissNumber);


                //i go izprashtame
                Backendless.Persistence.of(Messages.class).save(kissMessage, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //send push message
                        //channel po koito izprashtame push e emailat na poluchatelia
                        if(recipientBackendlessUser != null) {
                            BackendlessMessage.sendPush(mCurrentUser,recipientBackendlessUser, kissMessage, context,Statics.TYPE_KISS);
                        }

                        String message;

                        //pokazvame alerdialog s broia na izpratenite doesga celuvki
                        if(kissNumber > 1) {
                            //kiss v mnozhestveno chislo
                            message = context.getResources().getString(R.string.number_of_kisses_sent_plural, kissNumber);

                        } else {
                            //kiss v edinstveno chislo
                            message = context.getResources().getString(R.string.number_of_kisses_sent_singular);
                        }

                        //dobaviame username na polucahtelia kam message
                        if(finalRecepientBackendlessUser !=null) {
                            message = message + " "
                                    + finalRecepientBackendlessUser.getProperty(Statics.KEY_USERNAME)
                                    +".";
                        }
                        String title = context.getResources().getString(R.string.dialog_number_kisses_title);

                        CustomAlertDialog kissDialog = new CustomAlertDialog();
                        Bundle dialogContent = new Bundle();
                        dialogContent.putString(Statics.ALERTDIALOG_TITLE, title);
                        dialogContent.putString(Statics.ALERTDIALOG_MESSAGE,message);
                        kissDialog.setArguments(dialogContent);
                        kissDialog.show(activity.getFragmentManager(),"tag_alert_dialog");
                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();*/

                        //3. UPDATEVAME TABLICATA, CHE SME IZPRATILI OSHTE EDNA CELUVKA
                        KissesCount kissToUpdate;
                        if(kissesSent.getCurrentPage().size() == 0) {
                            //ako niama entry go sazdavame
                            kissToUpdate = new KissesCount();
                            kissToUpdate.setSender(mCurrentUser);
                            kissToUpdate.setSenderEmail(mCurrentUser.getEmail());
                            kissToUpdate.setReceiverEmail(recipientEmail);
                            //dobaviame i receiver kato Backendless user
                            if(finalRecepientBackendlessUser != null) {
                             kissToUpdate.setReceiver(finalRecepientBackendlessUser);
                            }

                        } else {
                            kissToUpdate = kissesSent.getCurrentPage().get(0);
                        }
                        kissToUpdate.setNumberOfKisses(kissNumber);

                        Backendless.Data.of(KissesCount.class).save(kissToUpdate, new AsyncCallback<KissesCount>() {
                            @Override
                            public void handleResponse(KissesCount kissesCount) {
                                //uspeshno sme updatenali celukvata
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                //ne e uspeshno updatnata, shte izleze s 1 po-malko, no kakvo da se pravi
                            }
                        });//krai na updatevane na broi na veche izprateni celuvki

                    }//krai na uspeshtanata send a kiss

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        String error = backendlessFault.getMessage();
                        Toast.makeText(context, context.getResources()
                                .getString(R.string.send_a_kiss_toast_unsuccessful), Toast.LENGTH_LONG).show();

                    }//krai na nesupeshnata send a kiss
                });//krai na send a kiss


            } //krai na upseshno data quaery za tarsene na broi izprateni celuvki

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //error finind number of kiss messages already sent
                String error = backendlessFault.getMessage();
                //tova e v sluchai, che niama sazdadena KissTable
                if(backendlessFault.getCode().equals(Statics.BACKENDLESS_TABLE_NOT_FOUND_CODE)) {
                    BackendlessHelper.createTables(mCurrentUser, mCurrentUser);
                }
                Toast.makeText(context, context.getResources()
                        .getString(R.string.send_a_kiss_toast_unsuccessful), Toast.LENGTH_LONG).show();
            } //krai na neuspeshnata data query za tarsene na broi celuvki
        }); //krai na cialata data query za tarsene na broi celuvki



    }//krai na send kiss

    //namirane na backendless user v spisaka na partniorite na tekushtia potrebitel po email

    public static BackendlessUser findBackendlessUserByEmail(BackendlessUser currentUser, String emailOfPartner) {
        BackendlessUser recipientBackendlessUser = null;
        //probvame da namerim poluchatelia po emaila v spisaka na partniorite na currentUser
        if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            BackendlessUser[] partners = (BackendlessUser[]) currentUser.getProperty(Statics.KEY_PARTNERS);
            for(BackendlessUser partner: partners) {
                if(partner.getEmail().equals(emailOfPartner)) {
                    //namirame recepient kato Backendless user v spisakat ot partniori
                    recipientBackendlessUser = partner;
                }
            }

        }
        return recipientBackendlessUser;
    }

    public static void registerDeviceForPush(final BackendlessUser currentUser){

        final String channel = currentUser.getEmail();


        Backendless.Messaging.registerDevice(Statics.GOOGLE_PROJECT_ID, channel, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                //Get registration and re-register....
                Backendless.Messaging.getRegistrations(new AsyncCallback<DeviceRegistration>() {
                    @Override
                    public void handleResponse(final DeviceRegistration deviceRegistration) {
                        String token = deviceRegistration.getDeviceToken();
                        List<String> channels = new ArrayList<String>();
                        channels.add(channel);
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.YEAR, 10);
                        Date expiration = c.getTime();
                        Backendless.Messaging.registerDeviceOnServer(token, channels, expiration.getTime(), new AsyncCallback<String>() {
                            @Override
                            public void handleResponse(String s) {

                                currentUser.setProperty(Statics.KEY_DEVICE_ID,deviceRegistration.getDeviceId());


                                Backendless.UserService.update(currentUser, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        Log.d("Vic","good");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        Log.d("Vic","good");

                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Log.d("Vic","good");

                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO handle fault
                        Log.d("Vic","good");

                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //TODO handle fault
                String s = backendlessFault.toString();
                Log.d("Vic","good");

            }
        });

    }

    public static void findMessageAndSetDateOpened(String messageID){
        String whereClause = "objectId='" + messageID +"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        //find message
        Backendless.Data.of(Messages.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Messages>>() {
            @Override
            public void handleResponse(BackendlessCollection<Messages> message) {

                if(message.getCurrentPage().size() > 0) {
                    //namereno e saobshtenieto
                    //updatevame koga e otvoreno

                    Messages messageToUpdate = message.getCurrentPage().get(0);//tr da ima samo 1 saobstehnie
                    if(messageToUpdate.getOpened() == null) { }
                    //ne e zadadeno koga e bilo otvoreno
                    Calendar c = Calendar.getInstance();
                    messageToUpdate.setOpened(c.getTime());

                    //zapazvame go na servera
                    Backendless.Data.of(Messages.class).save(messageToUpdate, new AsyncCallback<Messages>() {
                        @Override
                        public void handleResponse(Messages messages) {

                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                        }
                    });

                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //nishto ne moze da se napravi,
            }
        });

    }
}
