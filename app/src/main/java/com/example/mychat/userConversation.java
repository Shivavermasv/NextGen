package com.example.mychat;

import static androidx.core.app.ActivityCompat.requestPermissions;

import static com.example.mychat.MyFireBaseMessagingService.ACTION_1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class userConversation extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final String TAG = "MYTAG";
    private RoundedImageView option_button;
    private RoundedImageView logout_button;
    private  RecyclerView user_conversation;
    private TextView welcomeText;
    private FloatingActionButton newChat;
    private final User user = constants.logedInUser;
    private final String listenerID = "Listener1";
    private AppSettings appSettings;
    private RelativeLayout progres_bar_layout;
    private void appSettinginit(){
        appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .subscribePresenceForFriends ()
                .setRegion ( AppKeys.APP_REGION )
                .autoEstablishSocketConnection ( true )
                .build ();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.user_dashboard );
        option_button = findViewById ( R.id.chat_options );
        logout_button = findViewById ( R.id.logout );
        user_conversation = findViewById ( R.id.user_conversationRecycleView );
        welcomeText = findViewById ( R.id.welcomeText );
        newChat = findViewById ( R.id.fabNewChat );
        progres_bar_layout = findViewById ( R.id.progress_bar_layout );
        appSettinginit ();
        cometchatInit ();
        createNotificationChannel ();
        retriveUserConversations ();
        onLogoutButtonClick();
        onOptionClick();
        onNewChatClicked();
//        String [] permission = {"android.Manifest.permission.POST_NOTIFICATIONS"};
//        requestPermissions( permission,200);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            } else {
                constants.notification_permissionEnabled = false;
            }
        }
        if(user != null){
            MyFireBaseMessagingService.subscribeUserNotification ( user.getUid () );
            user.setStatus ( CometChatConstants.USER_STATUS_ONLINE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            // Check if the permission request was granted
            constants.notification_permissionEnabled = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void setProgressBarVisibilty(boolean var){
        if(var){
            progres_bar_layout.setVisibility ( View.VISIBLE );
        }
        else{
            progres_bar_layout.setVisibility ( View.GONE );
        }
    }
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
    private void onNewChatClicked() {
        newChat.setOnClickListener ( v -> startActivity ( new Intent (userConversation.this,userActivity.class) ) );
    }

    private void cometchatInit(){
        setProgressBarVisibilty ( true );
        CometChat.init(userConversation.this, AppKeys.APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
    }
    private void retriveUserConversations() {
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(50)
                .withUserAndGroupTags(true)
                .build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if(conversations.size ()==0){
                    welcomeText.setVisibility ( View.VISIBLE );
                }
                else{
                    Log.d ("MYTAG","Conversation fteched" );
                    user_conversation.setLayoutManager ( new LinearLayoutManager ( userConversation.this ) );
                    user_conversation_fetch_adapter adapter = new user_conversation_fetch_adapter (  conversations , userConversation.this);
                    user_conversation.setAdapter ( adapter );
                    user_conversation.addItemDecoration ( new DividerItemDecoration ( userConversation.this, LinearLayoutManager.VERTICAL ) );
                    setProgressBarVisibilty ( false );
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","converstaion fetch failded "+ e.getMessage () );
            }
        });
    }

    private void onOptionClick() {
        option_button.getRootView ().setOnTouchListener ( new View.OnTouchListener () {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction ()){
                    case MotionEvent.ACTION_DOWN:{
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        } );
        option_button.setOnClickListener ( this::showPopup );
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
       if(item.getItemId () == R.id.new_group){
           Toast.makeText ( this, "This section is under Process", Toast.LENGTH_SHORT ).show ();
           return true;
       }
       else if(item.getItemId () == R.id.change_avatar){
           startActivity ( new Intent (userConversation.this, avatar_uploader.class) );
           return true;
       }
       else if(item.getItemId () == R.id.my_profile){
           user_profile.start (userConversation.this, CometChat.getLoggedInUser () );
       }
       else if(item.getItemId () == R.id.about){
           startActivity ( new Intent (userConversation.this, about.class ));
       }
           return true;
    }
    private void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu ( this,v );
        popupMenu.setOnMenuItemClickListener ( this );
        popupMenu.inflate ( R.menu.drop_down_list );
        popupMenu.show ();
    }


    private void onLogoutButtonClick() {
        logout_button.setOnClickListener ( v -> CometChat.logout ( new CometChat.CallbackListener<String> () {
            @Override
            public void onSuccess(String s) {
                setProgressBarVisibilty ( true );
                FirebaseAuth.getInstance ().signOut ();
                user.setLastActiveAt (System.currentTimeMillis ());
                Toast.makeText ( userConversation.this, "Thanks for using MyChat !!", Toast.LENGTH_SHORT ).show ();
                Intent intent = new Intent (userConversation.this, login.class);
                CometChat.removeUserListener(listenerID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity ( intent);
                finishAffinity();
            }
            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG", "logging out failed" );
            }
        } ) );
    }
}