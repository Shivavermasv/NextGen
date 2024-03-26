package com.example.mychat.ui.chat_interface;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.constants.constants;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qifan.library.ChatTypingIndicatorView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.mychat.models.messageWrapper;

public class one_on_one_chat_interface extends AppCompatActivity {
    private AppCompatImageView back_button;
    private RoundedImageView reciever_image;
    private TextView reciever_name;
    private AppCompatImageView info;
    private MessagesListAdapter<messageWrapper> adapter;
    private FrameLayout imageView;
    private ChatTypingIndicatorView indicator;
    private MessageInput messageInput;
    private String user_id;
    private TextView user_pressence;
    private User user ;
    private MessagesList messagesList;
    private RoundedImageView chatIndiacatorAvatar;

    public static Intent start(Context context,User user){
        Intent starter = new Intent(context, one_on_one_chat_interface.class);
        constants.User = user;
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_oneoone_chat );
        user = constants.User;
        user_id = constants.User.getUid ();
        user_pressence = findViewById ( R.id.user_pressence );
        back_button = findViewById ( R.id.imageBack );
        reciever_name = findViewById ( R.id.reciver_name );
        reciever_image = findViewById ( R.id.reciver_layoutimage );
        info = findViewById ( R.id.info );
        imageView = findViewById ( R.id.userAvatarLayout );
        indicator = findViewById ( R.id.indicatorView );
        messageInput = findViewById ( R.id.sender_input );
        messagesList = findViewById ( R.id.sender_mesageList );
        chatIndiacatorAvatar = findViewById ( R.id.userAvatar2 );
        initViews ();
        addListner ();
        setUserImage ();
        ontyping ();
        onBack ();
        setText ();
        infoPressed ();
        setIndicator ();
        fetchPreviousMessages ();
        setUserPressence();
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
    private void setUserPressence() {
        if(user.getStatus ().equals ( CometChatConstants.USER_STATUS_ONLINE )){
            user_pressence.setText (R.string.online );
            user_pressence.setTextColor ( ContextCompat.getColor( one_on_one_chat_interface.this, R.color.green)  );
        }
        else{
            long lastActiveTimeMillis = user.getLastActiveAt();
            Date lastActiveDate = new Date(lastActiveTimeMillis);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            String formattedTime = dateFormat.format(lastActiveDate);
            String text = "Last Seen " + formattedTime;
            user_pressence.setText ( text );
            user_pressence.setTextColor ( ContextCompat.getColor( one_on_one_chat_interface.this, R.color.ivory) );
        }
    }

    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder ().setUID ( user_id ).build ();
        messagesRequest.fetchPrevious ( new CometChat.CallbackListener<List<BaseMessage>> () {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                if(baseMessages != null){
                    addMessages(baseMessages);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","Previous not msgs fetched" );
            }
        } );
    }

    private void addMessages(List<BaseMessage> baseMessages) {
        List<messageWrapper> list = new ArrayList<> ();
        for(BaseMessage message:baseMessages){
            if(message instanceof TextMessage){
                list.add ( new messageWrapper ( (TextMessage) message ) );
            }
        }
        adapter.addToEnd ( list,true );
        messagesList.smoothScrollToPosition ( 0 );
    }
    private void onBack(){
        back_button.setOnClickListener ( v -> {
           // startActivity ( new Intent (one_on_one_chat_interface.this, main_dashboard.class) );
            finish ();
        } );
    }
    private void setUserImage(){
        Picasso.get ().load ( user.getAvatar () ).into ( chatIndiacatorAvatar );
        Picasso.get ().load ( user.getAvatar () ).into ( reciever_image );
    }

    private void setText(){
        reciever_name.setText ( user.getUid () );
    }

    private void infoPressed(){
        info.setOnClickListener ( v -> Toast.makeText ( one_on_one_chat_interface.this, "Features will bw added soon !!", Toast.LENGTH_SHORT ).show () );
    }

    private void setIndicator(){
        indicator.setVisibility ( View.INVISIBLE );
        CometChat.addMessageListener("Listener 1", new CometChat.MessageListener() {
            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                indicator.setVisibility ( View.VISIBLE );
                imageView.setVisibility ( View.VISIBLE );
                Log.d("MYTAG", " Typing Started : " + typingIndicator.toString());
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                indicator.setVisibility ( View.GONE );
                imageView.setVisibility ( View.GONE );
                Log.d("MYTAG", " Typing Ended : " + typingIndicator.toString());
            }

        });
    }

    private void ontyping(){
        messageInput.setTypingListener ( new MessageInput.TypingListener () {
            @Override
            public void onStartTyping() {
                TypingIndicator typingIndicator = new TypingIndicator(user_id, CometChatConstants.RECEIVER_TYPE_USER);
                CometChat.startTyping(typingIndicator);
            }

            @Override
            public void onStopTyping() {
                TypingIndicator typingIndicator = new TypingIndicator(user_id, CometChatConstants.RECEIVER_TYPE_USER);
                CometChat.endTyping(typingIndicator);
            }
        } );
    }

    private void initViews() {
        messageInput.setInputListener ( input -> {
            sendMessage(input.toString ());
            return true;
        } );
        String senderId = constants.logedInUser.getUid ();
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get ().load ( url ).into ( imageView );
        adapter = new MessagesListAdapter<> ( senderId, imageLoader);
        messagesList.setAdapter ( adapter );
    }
    private void sendMessage(String message){
        TextMessage textMessage = new TextMessage ( user_id,message,CometChatConstants.RECEIVER_TYPE_USER );
        CometChat.sendMessage ( textMessage, new CometChat.CallbackListener<TextMessage> () {
            @Override
            public void onSuccess(TextMessage textMessage) {
                recieveMessage ( textMessage );
                Log.d ( "MYTAG","Message sent !!" );
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","Message not sent !!" );
            }
        } );
    }

    private void addListner(){
        String listnerID = "listner 1";
        CometChat.addMessageListener ( listnerID, new CometChat.MessageListener () {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                super.onTextMessageReceived ( textMessage );
               recieveMessage ( textMessage );
            }
        } );

    }
    private void recieveMessage(TextMessage textMessage){
        adapter.addToStart ( new messageWrapper ( textMessage ), true );
    }
}