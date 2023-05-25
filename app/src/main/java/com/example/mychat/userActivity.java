package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import java.util.List;

public class userActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AppCompatImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_user );
        recyclerView = findViewById ( R.id.usersRecycleView );
        progressBar = findViewById ( R.id.prgressBar );
        back_button = findViewById ( R.id.imageBack );
        onBackButtonPressed();
        retriveUser();
    }

    private void onBackButtonPressed() {
        back_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (userActivity.this, userConversation.class) );
                finish ();
            }
        } );
    }

    private void retriveUser() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext ( new CometChat.CallbackListener<List<User>> () {
            @Override
            public void onSuccess(List<User> users) {
                Toast.makeText ( userActivity.this, "user fetching started...", Toast.LENGTH_SHORT ).show ();
                recyclerView.setLayoutManager ( new LinearLayoutManager ( userActivity.this ) );
                userGroupAdapter adapter = new userGroupAdapter (  users , userActivity.this);
                recyclerView.setAdapter ( adapter );
                recyclerView.addItemDecoration ( new DividerItemDecoration ( userActivity.this, LinearLayoutManager.VERTICAL ) );
                progressBar.setVisibility ( View.INVISIBLE );
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText ( userActivity.this, "SOME ERROR OCCURED", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }
}