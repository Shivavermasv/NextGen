package com.example.mychat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class groupList extends AppCompatActivity {
    FloatingActionButton button ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_group_list );
        button = findViewById ( R.id.fabNewChat );
        getGrouplist();
        button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (groupList.this,userActivity.class) );
                Toast.makeText ( groupList.this, "USER ACTIVITY STARTED ", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void getGrouplist() {

        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder ().setLimit ( 5 ).build ();
        groupsRequest.fetchNext ( new CometChat.CallbackListener<List<Group>> () {
            @Override
            public void onSuccess(List<Group> groups) {
                Log.d (TAG,"group fetched successfully !!"+groups.size ());
                updateUI(groups);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText ( groupList.this, "group fetch failed ", Toast.LENGTH_SHORT ).show ();
                Log.d (TAG,"group list fetching failed !!"+e.getMessage ());
            }
        } );
    }

    private void updateUI(List<Group> groups) {
        final RecyclerView recyclerView = findViewById ( R.id.grouprecycleview );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        grpupAdapter grpupAdapter = new grpupAdapter ( groups, this );
        recyclerView.setAdapter ( grpupAdapter );
    }
}