package com.example.mychat.ui;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.mychat.R;
import com.example.mychat.adapter.conversations_fetch_adapter;
import com.example.mychat.constants.AppKeys;
import com.example.mychat.constants.constants;
import com.example.mychat.services.MyFireBaseMessagingService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class main_dashboard extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final String TAG = "MYTAG";
    private RoundedImageView option_button;
    private RoundedImageView logout_button;
    private RecyclerView user_conversation;
    private TextView welcomeText;
    private FloatingActionButton newChat;
    private final User user = constants.logedInUser;
    private final String listenerID = "Listener1";

    private conversations_fetch_adapter adapter;
    private AppSettings appSettings;

    private void appSettinginit() {
        appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .subscribePresenceForFriends ()
                .setRegion ( AppKeys.APP_REGION )
                .autoEstablishSocketConnection ( true )
                .build ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        retrieveUserConversations ();

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
        RelativeLayout progres_bar_layout = findViewById ( R.id.progress_bar_layout );
        appSettinginit ();
        cometchatInit ();
        createNotificationChannel ();
        retrieveUserConversations ();
        onLogoutButtonClick ();
        onOptionClick ();
        onNewChatClicked ();
        checkNotificationPermission ();
        if (user != null) {
            MyFireBaseMessagingService.subscribeUserNotification ( user.getUid () );
            user.setStatus ( CometChatConstants.USER_STATUS_ONLINE );
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission ( this, Manifest.permission.POST_NOTIFICATIONS )
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions ( this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200 );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        if (requestCode == 200) {
            constants.notification_permissionEnabled = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString ( R.string.app_name );
            String description = getString ( R.string.channel_description );
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel ( "2", name, importance );
            channel.setDescription ( description );
            channel.enableVibration ( true );
            channel.setLockscreenVisibility ( Notification.VISIBILITY_PUBLIC );
            NotificationManager notificationManager = getSystemService ( NotificationManager.class );
            notificationManager.createNotificationChannel ( channel );
        }
    }

    private void onNewChatClicked() {
        newChat.setOnClickListener ( v -> startActivity ( new Intent ( main_dashboard.this, contact_list.class ) ) );
    }

    private void cometchatInit() {
        setProgressBarVisibility ( true );
        CometChat.init ( main_dashboard.this, AppKeys.APP_ID, appSettings, new CometChat.CallbackListener<String> () {
            @Override
            public void onSuccess(String successMessage) {
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( TAG, "Initialization failed with exception: " + e.getMessage () );
            }
        } );
    }

    private void retrieveUserConversations() {
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder ()
                .setLimit ( 50 )
                .withUserAndGroupTags ( true )
                .build ();
        conversationsRequest.fetchNext ( new CometChat.CallbackListener<List<Conversation>> () {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations.isEmpty ()) {
                    welcomeText.setVisibility ( View.VISIBLE );
                    setProgressBarVisibility ( false );
                } else {
                    welcomeText.setVisibility ( View.INVISIBLE );
                    user_conversation.setLayoutManager ( new LinearLayoutManager ( main_dashboard.this ) );
                    adapter = new conversations_fetch_adapter ( conversations, main_dashboard.this );
                    user_conversation.setAdapter ( adapter );
                    user_conversation.addItemDecoration ( new DividerItemDecoration ( main_dashboard.this, LinearLayoutManager.VERTICAL ) );
                    setProgressBarVisibility ( false );
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG", "Conversation fetch failed " + e.getMessage () );
            }
        } );
    }

    private void onOptionClick() {
        option_button.setOnClickListener ( this::showPopup );
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId () == R.id.new_group) {
            Toast.makeText ( this, "This section is under Process", Toast.LENGTH_SHORT ).show ();
            return true;
        } else if (item.getItemId () == R.id.change_avatar) {
            startActivity ( new Intent ( main_dashboard.this, avatar_uploader.class ) );
            return true;
        } else if (item.getItemId () == R.id.my_profile) {
            user_profile.start ( main_dashboard.this, CometChat.getLoggedInUser () );
            return true;
        } else if (item.getItemId () == R.id.about) {
            startActivity ( new Intent ( main_dashboard.this, about.class ) );
            return true;
        }
        return false;
    }

    private void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu ( this, v );
        popupMenu.setOnMenuItemClickListener ( this );
        popupMenu.inflate ( R.menu.drop_down_list );
        popupMenu.show ();
    }

    private void onLogoutButtonClick() {
        logout_button.setOnClickListener ( v -> CometChat.logout ( new CometChat.CallbackListener<String> () {
            @Override
            public void onSuccess(String s) {
                setProgressBarVisibility ( true );
                FirebaseAuth.getInstance ().signOut ();
                user.setLastActiveAt ( System.currentTimeMillis () );
                Toast.makeText ( main_dashboard.this, "Thanks for using MyChat !!", Toast.LENGTH_SHORT ).show ();
                Intent intent = new Intent ( main_dashboard.this, login.class );
                CometChat.removeUserListener ( listenerID );
                intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity ( intent );
                finishAffinity ();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG", "Logging out failed" );
            }
        } ) );
    }
}