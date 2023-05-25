package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class userConversation extends AppCompatActivity {

    private AppCompatImageView option_button;
    private AppCompatImageView logout_button;
    private RecyclerView user_conversation;
    private ProgressBar progressBar;
    private TextView errorMsg;
    private FloatingActionButton newChat;
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
        retriveUserConversations();
        onLogoutButtonClick();
        onOptionClick();
        onNewChatClicked();
    }

    private void onNewChatClicked() {
        newChat.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (userConversation.this,userActivity.class) );
            }
        } );
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
                Log.d ("MYTAG",conversations.get ( 0 ).getConversationWith ().toString () );
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
        option_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText ( userConversation.this, "New features will be added soon !!", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void onLogoutButtonClick() {
        logout_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                CometChat.logout ( new CometChat.CallbackListener<String> () {
                    @Override
                    public void onSuccess(String s) {
                        Intent intent = new Intent (userConversation.this, login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity ( intent);
                        finish();
                    }
                    @Override
                    public void onError(CometChatException e) {
                        Log.d ( "MYTAG", "logging out failed" );
                    }
                } );
            }
        } );
    }

}