package com.example.mychat.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.mychat.R;
import com.example.mychat.constants.AppKeys;

public class splash_screen extends AppCompatActivity {

    private Intent intent;
    private AppSettings appSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView ( R.layout.activity_launcher );
        appSettinginit ();
        cometchatInit ();
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
    private void appSettinginit(){
        appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .subscribePresenceForFriends ()
                .setRegion ( AppKeys.APP_REGION )
                .autoEstablishSocketConnection ( true )
                .build ();

    }
    private void cometchatInit(){
        CometChat.init( splash_screen.this, AppKeys.APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "Initialization completed successfully");
                intent = new Intent( splash_screen.this , login.class);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
    }
}
