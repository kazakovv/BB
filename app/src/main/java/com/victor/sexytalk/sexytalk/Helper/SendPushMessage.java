package com.victor.sexytalk.sexytalk.Helper;

import android.content.Context;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;

/**
 * Created by Victor on 15/02/2015.
 */
public class SendPushMessage {

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

    }
}
