package com.example.mychat.oneononechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.chatActivity;
import com.example.mychat.constants;
import com.example.mychat.groupList;
import com.example.mychat.userConversation;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qifan.library.ChatTypingIndicatorView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

import models.messageWrapper;

public class oneoone_chat extends AppCompatActivity {

    private AppCompatImageView back_button;
    private RoundedImageView reciever_image;
    private TextView reciever_name;
    private AppCompatImageView info;
    private MessagesListAdapter<messageWrapper> adapter;
    private ChatTypingIndicatorView indicator;
    private MessageInput messageInput;
    private String user_id;

    private MessagesList messagesList;

    public static void start(Context context, String user_id){
        Intent starter = new Intent (context, oneoone_chat.class);
        starter.putExtra ( constants.USER_ID,user_id );
        context.startActivity ( starter );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_oneoone_chat );
        Intent intent = getIntent ();
        if(intent != null){
            this.user_id = intent.getStringExtra ( constants.USER_ID );
        }

        back_button = findViewById ( R.id.imageBack );
        reciever_name = findViewById ( R.id.reciver_name );
        reciever_image = findViewById ( R.id.reciver_layoutimage );
        info = findViewById ( R.id.info );
        indicator = findViewById ( R.id.indicatorView );
        messageInput = findViewById ( R.id.sender_input );
        messagesList = findViewById ( R.id.sender_mesageList );
        initViews ();
        addListner ();
        setUserImage ();
        ontyping ();
        onBack ();
        setText ();
        infoPressed ();
        setIndicator ();
        fetchPreviousMessages ();
    }
    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder ().setUID ( user_id ).build ();
        messagesRequest.fetchPrevious ( new CometChat.CallbackListener<List<BaseMessage>> () {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                Log.d ( "MYTAG","Previous msgs fetched" );
                addMessages(baseMessages);
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
    }
    private void onBack(){
        back_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (oneoone_chat.this, userConversation.class) );
                finish ();
            }
        } );
    }
    private void setUserImage(){
        CometChat.getUser ( user_id, new CometChat.CallbackListener<User> () {
            @Override
            public void onSuccess(User user) {
                Picasso.get ().load ( user.getAvatar () ).into ( reciever_image );
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG",e.getMessage () );
            }
        } );
    }

    private void setText(){
        reciever_name.setText ( user_id );
    }

    private void infoPressed(){
        info.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText ( oneoone_chat.this, "Features will bw added soon !!", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void setIndicator(){
        indicator.setVisibility ( View.INVISIBLE );
        CometChat.addMessageListener("Listener 1", new CometChat.MessageListener() {
            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                indicator.setVisibility ( View.VISIBLE );
                Log.d("MYTAG", " Typing Started : " + typingIndicator.toString());
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                indicator.setVisibility ( View.GONE );
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
        messageInput.setInputListener ( new MessageInput.InputListener () {
            @Override
            public boolean onSubmit(CharSequence input) {
                sendMessage(input.toString ());
                return true;
            }
        } );
        String senderId = CometChat.getLoggedInUser ().getUid ();
        ImageLoader imageLoader = new ImageLoader () {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get ().load ( url ).into ( imageView );
            }
        };
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
                Log.d ( "MYTAG" ,"message recived "+textMessage.getText ().toString ());
            }
        } );
    }
    private void recieveMessage(TextMessage textMessage){
        adapter.addToStart ( new messageWrapper ( textMessage ), true );
    }
}