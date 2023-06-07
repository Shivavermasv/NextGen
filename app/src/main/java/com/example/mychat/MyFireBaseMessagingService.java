package com.example.mychat;


import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.example.mychat.oneononechat.oneoone_chat;
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

    public static final String ACTION_1 = "action_1";
    private static final String TAG = "MYTAG";
    private JSONObject json;
    private int count = 0;
    private Call call;
    public static String token;
    private static final int REQUEST_CODE = 12;

    private boolean isCall;
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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
            createNotificationChannel ();
            int m = (int) ((new Date ().getTime ()));
            String USER_ID = baseMessage.getSender ().getUid ();
//            Intent action1Intent = oneoone_chat.start ( this,baseMessage.getSender () )
//                    .setAction(ACTION_1);
            Intent action1Intent = new Intent(this, userConversation.class).setAction(ACTION_1);

            PendingIntent action1PendingIntent = PendingIntent.getService ( this, 0,
                    action1Intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE );
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
                    .addAction(new NotificationCompat.Action(R.drawable.google,
                            "Action 1", action1PendingIntent))
                    .setChannelId ( getString(R.string.app_name) )
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
                if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notificationManager.notify ( 5, builder.build () );
            }
            else {
                if(constants.notification_permissionEnabled){
                    notificationManager.notify(baseMessage.getId(), builder.build());
                    notificationManager.notify(0, summaryBuilder.build());
                }
                else{
                    Toast.makeText ( this, "Notification Service is Disabled", Toast.LENGTH_SHORT ).show ();
                }
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

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super ( NotificationActionService.class.getSimpleName () );
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction ();
            Log.d ( "MYTAG", "Received notification action: " + action );
            if (ACTION_1.equals ( action )) {
                // TODO: handle action 1.
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }

    }
}