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
        String message="";
        if(TYPE_MESSAGE.equals(Statics.TYPE_TEXTMESSAGE)) {
            message = context.getResources().getString(R.string.push_message_love_message);
        } else if(TYPE_MESSAGE.equals(Statics.TYPE_CALENDAR_UPDATE)) {
            message = context.getResources().getString(R.string.push_calendar_update);
        }
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(PublishOptions.ANDROID_TICKER_TEXT_TAG, message);
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TITLE_TAG, context.getResources().getString(R.string.app_name));
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TEXT_TAG, message);
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
        deliveryOptions.addPushSinglecast(deviceId);


        Backendless.Messaging.publish(channel, "Push message", publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus messageStatus) {
                Toast.makeText(context,context.getResources().
                        getString(R.string.calendar_update_sent),Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(context,context.getResources().
                        getString(R.string.general_server_error),Toast.LENGTH_LONG).show();
            }
        });

    }
}
