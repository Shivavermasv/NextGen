package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.signin);
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Button login = findViewById ( R.id.login_proceed );
        final EditText username= findViewById ( R.id.username_field );
        final EditText password= findViewById ( R.id.pass_field );
        final TextView signup = findViewById ( R.id.textCreateNewAccount );
        signup.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (login.this, com.example.mychat.signup.class) );
                finish ();
            }
        } );
        //login in user using come-to-chat API
        login.setOnClickListener ( view -> {
            //auth = FirebaseAuth.getInstance ();
            String email = username.getText().toString ();
            String pass = password.getText().toString ();
            if(!email.isEmpty () || Patterns.EMAIL_ADDRESS.matcher ( email).matches (  ) ) {
               if(!pass.isEmpty () && pass.length ()>=6) {
                  // Toast.makeText ( login.this, "Login Successfull..", Toast.LENGTH_SHORT ).show ();
                           //String UID = "SUPERHERO1";
                           String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
                           CometChat.login (pass, authKey,new CometChat.CallbackListener<User> () {
                               @Override
                               public void onSuccess(User user) {
                                   if(CometChat.getLoggedInUser ().getAvatar () !=null){
                                       startActivity ( new Intent (login.this, userConversation.class) );
                                       //startActivity ( new Intent (login.this, groupList.class) );
                                       finish ();
                                   }
                                   else{
                                       startActivity ( new Intent (login.this, avatar_uploader.class) );
                                       finish ();
                                   }
                               }
                               @Override
                               public void onError(CometChatException e) {
                                   Toast.makeText ( login.this, "Login Unsuccessfull", Toast.LENGTH_SHORT ).show ();
                                   Toast.makeText ( login.this, email, Toast.LENGTH_SHORT ).show ();
                               }
                           } );

                       //startActivity ( new Intent (login.this, groupList.class) );
                       //finish ();
//                   auth.signInWithEmailAndPassword ( email, pass ).addOnSuccessListener ( authResult -> {
//                       Toast.makeText ( login.this, "Login Successfull..", Toast.LENGTH_SHORT ).show ();
//                       if(CometChat.getLoggedInUser ()==null){
//                           //String UID = "SUPERHERO1";
//                           String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
//                           CometChat.login (email, authKey,new CometChat.CallbackListener<User> () {
//                               @Override
//                               public void onSuccess(User user) {
//                                   startActivity ( new Intent (login.this, groupList.class) );
//                                   Toast.makeText ( login.this, "Login Success...", Toast.LENGTH_SHORT ).show ();
//                                   finish ();
//                               }
//                               @Override
//                               public void onError(CometChatException e) {
//                                   Toast.makeText ( login.this, "Login Unsuccessfull", Toast.LENGTH_SHORT ).show ();
//                               }
//                           } );
//                       }
//                       startActivity ( new Intent (login.this, groupList.class) );
//                       finish ();
//                       //startActivity ( login.this,  );
//                   } ).addOnFailureListener ( e -> Toast.makeText ( login.this, "Login Failed..", Toast.LENGTH_SHORT ).show () );
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

}