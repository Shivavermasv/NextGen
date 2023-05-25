package com.example.mychat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

import models.messageWrapper;

public class chatActivity extends AppCompatActivity {
    private String groupid;
    private MessagesListAdapter<messageWrapper> adapter;

    public static void start(Context context, String group_id){
        Intent starter = new Intent (context,chatActivity.class);
        starter.putExtra ( constants.GROUP_ID,group_id );
        context.startActivity ( starter );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );
        Intent intent = getIntent ();
        if(intent != null){
            groupid = intent.getStringExtra ( constants.GROUP_ID );
        }
        initViews();
        addListner();
        fetchPreviousMessages();
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
        MessageInput inputview = findViewById ( R.id.input );
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