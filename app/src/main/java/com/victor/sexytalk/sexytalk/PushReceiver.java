package com.victor.sexytalk.sexytalk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessBroadcastReceiver;
import com.victor.sexytalk.sexytalk.UserInterfaces.FragmentLoveDays;

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

        createNotification(context, "test",tickerText,contentTitle, contentText );
    }

    return false;
  }

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void createNotification(Context context, String message,
                                   String tickerText, String contentTitle,
                                   String contentText) {
        int appIcon = context.getApplicationInfo().icon;


        Intent notificationIntent = new Intent( context, Main.class );

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


        //!!!!!!!!!!!!!!!!!!!!!!
        Intent intent = new Intent(Statics.KEY_REFRESH_FRAGMENT_LOVE_BOX);

        //put whatever data you want to send, if any
        //intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }
}
                                            