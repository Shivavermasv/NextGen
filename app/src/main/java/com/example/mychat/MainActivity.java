package com.example.mychat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;

import java.security.AlgorithmParameterGenerator;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Button login = findViewById ( R.id.login_button );
        final Button signup = findViewById ( R.id.signup_button );
        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity ( new Intent (MainActivity.this, com.example.mychat.login.class) );
            }
        } );
        signup.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity ( new Intent (MainActivity.this, com.example.mychat.signup.class) );
            }
        } );

        // Replace with your App Region ("eu" or "us")
        String region = "us";
        AppSettings appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .setRegion ( region )
                .autoEstablishSocketConnection ( true )
                .build ();

        // Replace with your App ID
    }
}