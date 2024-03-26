package com.example.mychat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.adapter.user_list_fetch_adapter;

import java.util.List;

public class contact_list extends AppCompatActivity {

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
                finish ();
            }
        } );
    }
    private void retriveUser() {
        progressBar.setVisibility ( View.VISIBLE );

        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext ( new CometChat.CallbackListener<List<User>> () {
            @Override
            public void onSuccess(List<User> users) {
                Log.d ("MYTAG","User fetching started ");
                recyclerView.setLayoutManager ( new LinearLayoutManager ( contact_list.this ) );
                user_list_fetch_adapter adapter = new user_list_fetch_adapter (  users , contact_list.this);
                recyclerView.setAdapter ( adapter );
                recyclerView.addItemDecoration ( new DividerItemDecoration ( contact_list.this, LinearLayoutManager.VERTICAL ) );
                progressBar.setVisibility ( View.INVISIBLE );
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText ( contact_list.this, "SOME ERROR OCCURED", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }
}