package com.example.mychat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

import java.util.List;

public class groupList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_group_list );
        getGrouplist();

    }

    private void getGrouplist() {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder ().build ();
        groupsRequest.fetchNext ( new CometChat.CallbackListener<List<Group>> () {
            @Override
            public void onSuccess(List<Group> groups) {
                Log.d (TAG,"group fetched successfully !!"+groups.size ());
                updateUI(groups);
            }

            @Override
            public void onError(CometChatException e) {
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