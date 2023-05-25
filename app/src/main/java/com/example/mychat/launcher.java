package com.example.mychat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;

public class launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView ( R.layout.activity_launcher );
        String appID = "237720d53327b49d";
        String region = "us";
        AppSettings appSettings = new AppSettings.AppSettingsBuilder ()
                .subscribePresenceForAllUsers ()
                .setRegion ( region )
                .autoEstablishSocketConnection ( true )
                .build ();
        CometChat.init(launcher.this, appID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "Initialization completed successfully");
                Toast.makeText ( launcher.this, "success....", Toast.LENGTH_SHORT ).show ();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1300);
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    //startActivity ( new Intent (launcher.this, groupList.class) );
                    Intent intent = new Intent(launcher.this , MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}
