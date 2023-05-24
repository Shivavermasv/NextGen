package com.example.mychat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login);
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Button login = findViewById ( R.id.login_proceed );
        final TextView forgot_pass = findViewById ( R.id.forgot_pass_field );
        final EditText username= findViewById ( R.id.usename_field );
        final EditText password= findViewById ( R.id.pass_field );

        String appID = "237720d53327b49d";
        String region = "us";
        AppSettings appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .setRegion ( region )
                .autoEstablishSocketConnection ( true )
                .build ();
        CometChat.init(this, appID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "Initialization completed successfully");
                Toast.makeText ( login.this, "success....", Toast.LENGTH_SHORT ).show ();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
        //login in user using come-to-chat API
        login.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View view) {
                if(CometChat.getLoggedInUser ()==null){
                    //String UID = username.getText ().toString ();
                    //String authKey = password.getText ().toString ();
                     String UID = "SUPERHERO1";
                     String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
                    CometChat.login (UID, authKey,new CometChat.CallbackListener<User> () {
                        @Override
                        public void onSuccess(User user) {
                            Toast.makeText ( login.this, "Login Success...", Toast.LENGTH_SHORT ).show ();
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Toast.makeText ( login.this, "Login Unsuccessfull", Toast.LENGTH_SHORT ).show ();
                        }
                    } );
                }
            }
        } );
    }

}