package com.example.mychat;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Objects;


public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MYTAG";
    private JSONObject json;
    private int count = 0;
    private Call call;
    public static String token;
    private static final int REQUEST_CODE = 12;

    private boolean isCall;

    public static void subscribeUserNotification(String UID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppKeys.APP_ID + "_"+ CometChatConstants.RECEIVER_TYPE_USER +"_" +
                UID).addOnSuccessListener( aVoid -> Log.e(TAG, UID+ " Subscribed Success") );
    }
    @Override
    public void onNewToken(@NonNull String s) {
        token = s;
        Log.d ( TAG, "onNewToken: " + s );
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            count++;
            json = new JSONObject ( remoteMessage.getData () );
            BaseMessage baseMessage = CometChatHelper.processMessage ( new JSONObject
                    ( Objects.requireNonNull ( remoteMessage.getData ().get ( "message" ) ) ) );
            if (baseMessage instanceof Call) {
                call = (Call) baseMessage;
                isCall = true;
            }
            showNotifcation ( baseMessage );
        } catch (JSONException e) {
            e.printStackTrace ();
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        if (strURL != null) {
            try {
                URL url = new URL ( strURL );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
                connection.setDoInput ( true );
                connection.connect ();
                InputStream input = connection.getInputStream ();
                return BitmapFactory.decodeStream ( input );
            } catch (IOException e) {
                e.printStackTrace ();
                return null;
            }
        } else {
            return null;
        }
    }

    private void showNotifcation(BaseMessage baseMessage) {

        try {
            int m = (int) ((new Date ().getTime ()));
            String USER_ID = baseMessage.getSender ().getUid ();
            NotificationCompat.Builder builder = new NotificationCompat.Builder ( this, "2" )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setContentTitle ( json.getString ( "title" ) )
                    .setContentText ( json.getString ( "alert" ) )
                    .setPriority ( NotificationCompat.PRIORITY_HIGH )
                    .setColor ( ContextCompat.getColor( MyFireBaseMessagingService.this, R.color.ivory) )
                    .setLargeIcon ( getBitmapFromURL ( baseMessage.getSender ().getAvatar () ) )
                    .setGroup ( USER_ID )
                    .setCategory ( NotificationCompat.CATEGORY_MESSAGE )
                    .setVisibility ( NotificationCompat.VISIBILITY_PUBLIC )
                    .setSound ( RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION ) );

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder ( this, "2" )
                    .setContentTitle ( "CometChat" )
                    .setContentText ( count + " messages" )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setGroup ( USER_ID )
                    .setGroupSummary ( true );
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from ( this );

            if (isCall) {
                builder.setGroup ( USER_ID + "Call" );
                if (json.getString ( "alert" ).equals ( "Incoming audio call" ) ||
                        json.getString ( "alert" ).equals ( "Incoming video call" )) {
                    builder.setOngoing ( true );
                    builder.setPriority ( NotificationCompat.PRIORITY_HIGH );
                    builder.setSound ( RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_RINGTONE ) );
                    builder.addAction ( 0, "Answers",
                            PendingIntent.getBroadcast ( getApplicationContext (),
                                    REQUEST_CODE,
                                    getCallIntent ( "Answers" ),
                                    PendingIntent.FLAG_UPDATE_CURRENT ) );
                    builder.addAction ( 0, "Decline",
                            PendingIntent.getBroadcast ( getApplicationContext (), 1,
                                    getCallIntent ( "Decline" ),
                                    PendingIntent.FLAG_UPDATE_CURRENT ) );
                }
                if (ActivityCompat.checkSelfPermission ( this, android.Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManager.notify ( 5, builder.build () );
            }
            else {
                notificationManager.notify(baseMessage.getId(), builder.build());
                notificationManager.notify(0, summaryBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Intent getCallIntent(String title){
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(constants.StringContract,call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }
}
