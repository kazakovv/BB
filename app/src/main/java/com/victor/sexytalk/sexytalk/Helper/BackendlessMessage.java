package com.victor.sexytalk.sexytalk.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.BackendlessClasses.KissesCount;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;

/**
 * Created by Victor on 15/02/2015.
 */
public class BackendlessMessage {
    /*
    IZPRASHTANE NA PUSH MESSAGES
     */
    public static void sendPush(String deviceId, String channel, final Context context, String TYPE_MESSAGE) {
        String messagePush="";
        String messageToast="";
        if(TYPE_MESSAGE.equals(Statics.TYPE_TEXTMESSAGE)) {
            messagePush = context.getResources().getString(R.string.push_message_love_message);
            messageToast = context.getResources().getString(R.string.message_successfully_sent);
        } else if(TYPE_MESSAGE.equals(Statics.TYPE_CALENDAR_UPDATE)) {
            messagePush = context.getResources().getString(R.string.push_calendar_update);
            messageToast = context.getResources().getString(R.string.calendar_update_sent);
        } else if(TYPE_MESSAGE.equals(Statics.TYPE_KISS)) {
            messagePush =  context.getResources().getString(R.string.title_receive_a_kiss_message);
            //toast se izprashta ot main activity. Ako izprashtam niakolko kiss toast shte se pokazva neprekasnato
            //messageToast = context.getResources().getString(R.string.send_a_kiss_toast_successful);
        } else if( TYPE_MESSAGE.equals(Statics.KEY_PARTNER_REQUEST)) {
            messagePush = context.getResources().getString(R.string.new_partner_request_push);
        }
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(PublishOptions.ANDROID_TICKER_TEXT_TAG, messagePush);
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TITLE_TAG, context.getResources().getString(R.string.app_name));
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TEXT_TAG, messagePush);
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
        deliveryOptions.addPushSinglecast(deviceId);


        Backendless.Messaging.publish(channel, "Push message", publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus messageStatus) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });

    }//krai na send push
    /*
    IZPRASHTANE NA KISS
     */
    public static void sendKissMessage(final BackendlessUser mCurrentUser,
                                       final String recepientEmail,
                                       final String deviceId,
                                       final Context context){
        BackendlessUser recepientBackendlessUser = null;

        //probvame da namerim poluchatelia po emaila v spisaka na partniorite na currentUser
        if(mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
            BackendlessUser[] partners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
            for(BackendlessUser partner: partners) {
                if(partner.getEmail().equals(recepientEmail)) {
                    //namirame recepient kato Backendless user v spisakat ot partniori
                    recepientBackendlessUser = partner;
                }
            }

        }


        //message
        //1. parvo tarsim kolko celuvki sa izprateni veche
        //2. sastaviame kiss i go izprashtame
        //3. uvelichavame broia na izpratenite celuvki

        //1. TARSIM BROI CELUVKI, KOITO SA IZPRATENI DOSEGA
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = "senderEmail='" + mCurrentUser.getEmail() + "'" + " AND "
                + "receiverEmail='" + recepientEmail +"'";
        dataQuery.setWhereClause(whereClause);
        final BackendlessUser finalRecepientBackendlessUser = recepientBackendlessUser;
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
                kissMessage.setRecepientEmails(recepientEmail);
                kissMessage.setSederUsername((String) Backendless.UserService.CurrentUser().getProperty(Statics.KEY_USERNAME));
                kissMessage.setSender(Backendless.UserService.CurrentUser());
                kissMessage.setKissNumber(kissNumber);


                //i go izprashtame
                Backendless.Persistence.of(Messages.class).save(kissMessage, new AsyncCallback<Messages>() {
                    @Override
                    public void handleResponse(Messages messages) {
                        //send push message
                        //channel po koito izprashtame push e emailat na poluchatelia
                        BackendlessMessage.sendPush(deviceId,recepientEmail,context,Statics.TYPE_KISS);
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
                            message = message + " " + finalRecepientBackendlessUser.getProperty(Statics.KEY_USERNAME);
                        }
                        String title = context.getResources().getString(R.string.dialog_number_kisses_title);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();


                        //3. UPDATEVAME TABLICATA, CHE SME IZPRATILI OSHTE EDNA CELUVKA
                        KissesCount kissToUpdate;
                        if(kissesSent.getCurrentPage().size() == 0) {
                            //ako niama entry go sazdavame
                            kissToUpdate = new KissesCount();
                            kissToUpdate.setSender(mCurrentUser);
                            kissToUpdate.setSenderEmail(mCurrentUser.getEmail());
                            kissToUpdate.setReceiverEmail(recepientEmail);
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
}
