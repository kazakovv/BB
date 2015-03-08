package com.victor.sexytalk.sexytalk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessBroadcastReceiver;
import com.victor.sexytalk.sexytalk.BackendlessClasses.Messages;
import com.victor.sexytalk.sexytalk.UserInterfaces.FragmentLoveDays;
import com.victor.sexytalk.sexytalk.UserInterfaces.ViewImageActivity;
import com.victor.sexytalk.sexytalk.UserInterfaces.ViewKissActivity;
import com.victor.sexytalk.sexytalk.UserInterfaces.ViewTextMessageActivity;

public class PushReceiver extends BackendlessBroadcastReceiver
{
  @Override
  public boolean onMessage( Context context, Intent intent )
  {
    String tickerText = intent.getStringExtra( PublishOptions.ANDROID_TICKER_TEXT_TAG );
    String contentTitle = intent.getStringExtra( PublishOptions.ANDROID_CONTENT_TITLE_TAG );
    String contentText = intent.getStringExtra( PublishOptions.ANDROID_CONTENT_TEXT_TAG );
    String messageType = intent.getStringExtra(PublishOptions.MESSAGE_TAG); //tip saobstenie
    String subtopic = intent.getStringExtra( "message" );
    if( tickerText != null && tickerText.length() > 0 )
    {

        createNotification(context, messageType, intent, tickerText,contentTitle, contentText );
    }

    return false;
  }

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void createNotification(Context context, String messageType, Intent intent,
                                   String tickerText, String contentTitle,
                                   String contentText) {
        int appIcon = context.getApplicationInfo().icon;

        Intent notificationIntent;
        if(messageType.equals(Statics.TYPE_TEXTMESSAGE)) {
            notificationIntent = new Intent( context, ViewTextMessageActivity.class );

            String loveMessage = intent.getStringExtra(Statics.KEY_LOVE_MESSAGE);
            String usernameSender = intent.getStringExtra(Statics.KEY_USERNAME_SENDER);
            String messageId = intent.getStringExtra(Statics.KEY_MESSAGE_ID);

            notificationIntent.putExtra(Statics.KEY_MESSAGE_ID, messageId);
            notificationIntent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            notificationIntent.putExtra(Statics.KEY_USERNAME_SENDER, usernameSender);

        } else if(messageType.equals(Statics.TYPE_IMAGE_MESSAGE)) {
            notificationIntent = new Intent(context, ViewImageActivity.class);

            String loveMessage = intent.getStringExtra(Statics.KEY_LOVE_MESSAGE);
            String usernameSender = intent.getStringExtra(Statics.KEY_USERNAME_SENDER);
            String mediaUrl = intent.getStringExtra(Statics.KEY_URL);
            String messageId = intent.getStringExtra(Statics.KEY_MESSAGE_ID);

            notificationIntent.putExtra(Statics.KEY_MESSAGE_ID, messageId);
            notificationIntent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            notificationIntent.putExtra(Statics.KEY_USERNAME_SENDER, usernameSender);
            notificationIntent.putExtra(Statics.KEY_URL, mediaUrl);

        } else if(messageType.equals(Statics.TYPE_KISS)){
            notificationIntent = new Intent(context, ViewKissActivity.class);
            String loveMessage = intent.getStringExtra(Statics.KEY_LOVE_MESSAGE);
            String usernameSender = intent.getStringExtra(Statics.KEY_USERNAME_SENDER);
            String kissCount = intent.getStringExtra(Statics.KEY_NUMBER_OF_KISSES);

            notificationIntent.putExtra(Statics.KEY_LOVE_MESSAGE, loveMessage);
            notificationIntent.putExtra(Statics.KEY_USERNAME_SENDER, usernameSender);
            int numberOfKisses = Integer.valueOf(kissCount);
            notificationIntent.putExtra(Statics.KEY_NUMBER_OF_KISSES,numberOfKisses);

        } else {
            notificationIntent = new Intent(context, Main.class);
        }


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( context );
        notificationBuilder.setSmallIcon( appIcon );
        notificationBuilder.setTicker(tickerText);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setContentTitle(contentTitle);
        notificationBuilder.setContentText(contentText);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 0, notification );


        //!!!!!!!!!!!!!!!!!!!!!! TOVA RABOTI SAMO, AKO E OTVORENA PROGRAMATA. TOGAVA SE UPDATEVA LOVE BOX
        Intent intentRefresh = new Intent(Statics.KEY_REFRESH_FRAGMENT_LOVE_BOX);

        //put whatever data you want to send, if any
        //intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intentRefresh);
    }
}
                                            