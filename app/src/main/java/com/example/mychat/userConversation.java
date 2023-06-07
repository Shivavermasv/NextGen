package com.example.mychat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class userConversation extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final String TAG = "MYTAG";
    private AppCompatImageView option_button;
    private AppCompatImageView logout_button;
    private  RecyclerView user_conversation;
    private ProgressBar progressBar;
    private TextView errorMsg;
    private FloatingActionButton newChat;
    private final User user = constants.logedInUser;
    private final String listenerID = "Listener1";
    @Override
    protected void onResume() {
        super.onResume ();
        runOnUiThread ( new Runnable () {
            @Override
            public void run() {
                retriveUserConversations ();
            }
        } );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_user_conversation );
        option_button = findViewById ( R.id.chat_options );
        logout_button = findViewById ( R.id.logout );
        user_conversation = findViewById ( R.id.user_conversationRecycleView );
        progressBar = findViewById ( R.id.prgressBar );
        errorMsg = findViewById ( R.id.textErrorMessage );
        newChat = findViewById ( R.id.fabNewChat );
        createNotificationChannel ();
        retriveUserConversations ();
        onLogoutButtonClick();
        onOptionClick();
        onNewChatClicked();
        if(user != null){
            MyFireBaseMessagingService.subscribeUserNotification ( user.getUid () );
            user.setStatus ( CometChatConstants.USER_STATUS_ONLINE );
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

    private void retriveUserConversations() {
        progressBar.setVisibility ( View.VISIBLE );
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(50)
                .withUserAndGroupTags(true)
                .build();
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                Log.d ("MYTAG","Conversation fteched" );
                user_conversation.setLayoutManager ( new LinearLayoutManager ( userConversation.this ) );
                user_conversation_fetch_adapter adapter = new user_conversation_fetch_adapter (  conversations , userConversation.this);
                user_conversation.setAdapter ( adapter );
                user_conversation.addItemDecoration ( new DividerItemDecoration ( userConversation.this, LinearLayoutManager.VERTICAL ) );
                progressBar.setVisibility ( View.INVISIBLE );
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","converstaion fetch failded "+ e.getMessage () );
                errorMsg.setVisibility ( View.VISIBLE );
            }
        });
    }

    private void onOptionClick() {
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
                user.setStatus ( CometChatConstants.USER_STATUS_OFFLINE );
                user.setLastActiveAt (System.currentTimeMillis ());
                progressBar.setVisibility ( View.VISIBLE );
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