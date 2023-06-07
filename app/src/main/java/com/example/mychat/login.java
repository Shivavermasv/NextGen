package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.firebase.messaging.FirebaseMessaging;

public class login extends AppCompatActivity {
    private Button login;
    private EditText username;
    private EditText password;
    private TextView signup;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.signin);
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        login = findViewById ( R.id.login_proceed );
        username= findViewById ( R.id.username_field );
        password= findViewById ( R.id.pass_field );
        signup = findViewById ( R.id.textCreateNewAccount );
        if(CometChat.getLoggedInUser () != null){
            startActivity ( new Intent (login.this, userConversation.class) );
            finish ();
        }
        else{
            onSignUpClicked();
            onLoginClicked();
        }
    }


    private void onLoginClicked() {
        //login in user using come-to-chat API
        login.setOnClickListener ( view -> {
            //auth = FirebaseAuth.getInstance ();
            String email = username.getText().toString ();
            String pass = password.getText().toString ();
            if(!email.isEmpty () || Patterns.EMAIL_ADDRESS.matcher ( email).matches (  ) ) {
                if(!pass.isEmpty () && pass.length ()>=6) {
                    final String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
                    CometChat.login (pass, authKey,new CometChat.CallbackListener<User> () {
                        @Override
                        public void onSuccess(User user) {
                            FirebaseMessaging.getInstance ().getToken ().addOnCompleteListener ( task -> {
                                if(!task.isSuccessful ()){
                                    return;
                                }
                                token = task.getResult ();
                                CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        constants.token = token;
                                        Log.d("MYTAG",s);
                                        MyFireBaseMessagingService.subscribeUserNotification ( user.getUid () );
                                        startActivity ( new Intent (login.this, userConversation.class) );
                                        finish ();
                                    }
                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.d("MYTAG",e.getMessage ());
                                    }
                                });

                            } );
                        }
                        @Override
                        public void onError(CometChatException e) {
                            Log.d ("MYTAG","Login Unsucessfull");
                        }
                    } );
                }
                else{
                    password.setError ( "Enter a valid password !!" );
                }
            }
            else if(pass.length ()<6){
                username.setError ( "Password must have length 6 or above " );
            }
            else{
                username.setError ( "Enter a valid Email !!" );
            }
        } );
    }

    private void onSignUpClicked() {
        signup.setOnClickListener ( v -> {
            startActivity ( new Intent (login.this, com.example.mychat.signup.class) );
            finish ();
        } );
    }

}