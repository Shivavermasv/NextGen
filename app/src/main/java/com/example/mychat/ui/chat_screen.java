package com.example.mychat.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.example.mychat.R;
import com.example.mychat.constants.constants;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qifan.library.ChatTypingIndicatorView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

import com.example.mychat.models.messageWrapper;

public class chat_screen extends AppCompatActivity {
    private String groupid;
    private TextView groupName;
    private AppCompatImageView back_button;
    private RoundedImageView grp_icon;
    private MessagesListAdapter<messageWrapper> adapter;
    private Group group;
    private ChatTypingIndicatorView indicator;
    private MessageInput inputview;
    private TextView text_indicator;

    public static void start(Context context, String group_id, Group group){
        Intent starter = new Intent (context, chat_screen.class);
        starter.putExtra ( constants.GROUP_ID,group_id );
        context.startActivity ( starter );
        constants.group = group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );
        Intent intent = getIntent ();
        if(intent != null){
            groupid = intent.getStringExtra ( constants.GROUP_ID );
            this.group = constants.group;
        }
        groupName = findViewById ( R.id.group_name );
        back_button = findViewById ( R.id.imageBack );
        grp_icon = findViewById ( R.id.group_layoutimage );
        indicator = findViewById ( R.id.indicatorView );
        text_indicator = findViewById ( R.id.sender_typing_name );

        groupName.setText ( group.getName () );
        Picasso.get ().load ( group.getIcon () ).into ( grp_icon );
        onBackClicked ();
        initViews();
        addListner();
        fetchPreviousMessages();
        setIndicator ();
        ontyping ();
    }
    private void setIndicator(){
        indicator.setVisibility ( View.INVISIBLE );
        CometChat.addMessageListener("Listener 1", new CometChat.MessageListener() {
            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                text_indicator.setText ( String.format ( "%s...", typingIndicator.getSender ().getName () ) );
                text_indicator.setVisibility ( View.VISIBLE );
                indicator.setVisibility ( View.VISIBLE );
                Log.d("MYTAG", " Typing Started : " + typingIndicator.toString());
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                indicator.setVisibility ( View.GONE );
                indicator.setVisibility ( View.GONE );
                Log.d("MYTAG", " Typing Ended : " + typingIndicator.toString());
            }

        });
    }

    private void ontyping(){
        inputview.setTypingListener ( new MessageInput.TypingListener () {
            @Override
            public void onStartTyping() {
                TypingIndicator typingIndicator = new TypingIndicator(groupid, CometChatConstants.RECEIVER_TYPE_USER);
                CometChat.startTyping(typingIndicator);
            }

            @Override
            public void onStopTyping() {
                TypingIndicator typingIndicator = new TypingIndicator(groupid, CometChatConstants.RECEIVER_TYPE_USER);
                CometChat.endTyping(typingIndicator);
            }
        } );
    }

    private void onBackClicked() {
        back_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent ( chat_screen.this, main_dashboard.class) );
                finish ();
            }
        } );
    }
    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder ().setGUID ( groupid ).build ();
        messagesRequest.fetchPrevious ( new CometChat.CallbackListener<List<BaseMessage>> () {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                addMessages(baseMessages);
            }

            @Override
            public void onError(CometChatException e) {

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

    private void addListner() {
        String listnerID = "listner 1";
        CometChat.addMessageListener ( listnerID, new CometChat.MessageListener () {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                super.onTextMessageReceived ( textMessage );
                addMessage ( textMessage );
            }
        } );
    }

    private void initViews() {
        inputview = findViewById ( R.id.input );
        MessagesList messagesList = findViewById ( R.id.mesageList );
        inputview.setInputListener ( new MessageInput.InputListener () {
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

    private void sendMessage(String message) {
        TextMessage textMessage = new TextMessage (groupid, message, CometChatConstants.RECEIVER_TYPE_GROUP);

        CometChat.sendMessage ( textMessage, new CometChat.CallbackListener<TextMessage> () {
            @Override
            public void onSuccess(TextMessage textMessage) {
                addMessage(textMessage);
            }

            @Override
            public void onError(CometChatException e) {

            }
        } );
    }

    private void addMessage(TextMessage textMessage) {
        adapter.addToStart ( new messageWrapper ( textMessage ), true );
    }
}