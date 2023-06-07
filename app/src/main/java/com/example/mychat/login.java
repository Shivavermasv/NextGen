package com.example.mychat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class login extends AppCompatActivity {
    private Button login;
    private EditText username;
    private EditText password;
    private TextView signup;
    private String token;
    private ScrollView view;
    private ImageView googleButton;
    private ImageView facebookButton;
    private ImageView twitterButton;
    private RelativeLayout progress_bar_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.signin);
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        login = findViewById ( R.id.login_proceed );
        username= findViewById ( R.id.username_field );
        password= findViewById ( R.id.pass_field );
        signup = findViewById ( R.id.textCreateNewAccount );
        view = findViewById ( R.id.scroll_view );
        progress_bar_layout = findViewById ( R.id.progress_bar_layout );
        googleButton = findViewById ( R.id.google_logo );
        facebookButton = findViewById ( R.id.facebook_logo );
        twitterButton = findViewById ( R.id.twitter_logo );
        ontwitterClicked ();
        onFacebookClicked ();
        onGoogleClicked ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if(FirebaseAuth.getInstance ().getCurrentUser ()!=null && CometChat.getLoggedInUser () != null ){
            Log.d("MYTAG","loged in user " + FirebaseAuth.getInstance ().getCurrentUser ());
            constants.logedInUser = CometChat.getLoggedInUser ();
            startActivity ( new Intent (login.this, userConversation.class) );
            finish ();
        }
        else{
            onSignUpClicked();
            onLoginClicked();
            ontwitterClicked ();
            onFacebookClicked ();
            onGoogleClicked ();
        }
    }

    private void setProgressbarView(boolean var){
        if(var){
            progress_bar_layout.setVisibility ( View.VISIBLE );
        }
        else{
            progress_bar_layout.setVisibility ( View.GONE );
        }
    }
    @Override
    protected void onStart() {
        super.onStart ();
        if(FirebaseAuth.getInstance ().getCurrentUser ()!=null && CometChat.getLoggedInUser () != null ){
            Log.d("MYTAG","loged in user " + FirebaseAuth.getInstance ().getCurrentUser ());
            constants.logedInUser = CometChat.getLoggedInUser ();
            startActivity ( new Intent (login.this, userConversation.class) );
            finish ();
        }
        else{
            onSignUpClicked();
            onLoginClicked();
            ontwitterClicked ();
            onFacebookClicked ();
            onGoogleClicked ();
        }
    }

    private void hideKeyboard(boolean var){
        if(var){
            InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        else{
            InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput ( view,0 );
        }
    }
    private void onLoginClicked() {
        //login in user using come-to-chat API
        login.setOnClickListener ( view -> {
            String email = username.getText().toString ();
            String pass = password.getText().toString ();
            if(!email.isEmpty () && Patterns.EMAIL_ADDRESS.matcher ( email).matches (  ) ) {
                if(!pass.isEmpty () && pass.length ()>=6) {
                    hideKeyboard (true);
                    setProgressbarView ( true );
                    loginFirebase ( email,pass );
                }
                else{
                    hideKeyboard ( false );
                    password.setError ( "Enter a valid password !!" );
                }
            }
            else if(email.isEmpty ()){
                hideKeyboard ( false );
                username.setError ( "Email must be filled !!" );
            }
            else if(pass.length ()<6){
                hideKeyboard ( false );
                password.setError ( "Password must have length 6 or above " );
            }
            else{
                hideKeyboard ( false );
                username.setError ( "Enter a valid Email !!" );
            }
        } );
    }
    private void onFacebookClicked(){
        facebookButton.setOnClickListener ( v -> Toast.makeText ( login.this, "Facebook Login Feature will be added soon", Toast.LENGTH_SHORT ).show () );
    }
    private void onGoogleClicked(){
        googleButton.setOnClickListener ( v -> Toast.makeText ( login.this, "Google Login Feature will be added soon", Toast.LENGTH_SHORT ).show () );
    }
    private void ontwitterClicked(){
        twitterButton.setOnClickListener ( v -> Toast.makeText ( login.this, "Twitter Login Feature will be added soon", Toast.LENGTH_SHORT ).show () );
    }
    private void onSignUpClicked() {
        signup.setOnClickListener ( v -> {
            startActivity ( new Intent (login.this, com.example.mychat.signup.class) );
            finish ();
        } );
    }
    private void loginFirebase(String email, String pass){
        Log.d ( "MYTAG", email + pass );
        FirebaseAuth.getInstance ()
                .signInWithEmailAndPassword ( email,pass )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful ()){
                            String userId = Objects.requireNonNull ( FirebaseAuth.getInstance ().getCurrentUser () ).getUid ();
                            getUserFromFirebaseDatabase(userId, new OnUserLoadedListener() {
                                @Override
                                public void onUserLoaded(User user) {
                                    if (user != null) {
                                       loginCometchat ( user );
                                    } else {
                                        Log.d ( "MYTAG","user not fetched from database" );
                                    }
                                }
                            });
                        }
                        else{
                            setProgressbarView ( false );
                        }
                    }
                }
               );
    }
    private void getUserFromFirebaseDatabase(String userId, final OnUserLoadedListener listener) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        // Notify the listener that the user data has been loaded
                        listener.onUserLoaded(user);
                    }
                } else {
                    // User data does not exist
                    Log.d("MYTAG", "User not found");

                    // Notify the listener with a null user
                    listener.onUserLoaded(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method is called if there is an error while retrieving the data.
                Log.e("MYTAG", "Error fetching data: " + databaseError.getMessage());

                // Notify the listener with a null user
                listener.onUserLoaded(null);
            }
        });
    }

    // Define an interface to handle the user data loading
    interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

    private void loginCometchat(User user){
        CometChat.login (user.getUid (), AppKeys.COMETCHAT_AUTH_KEY,new CometChat.CallbackListener<User> () {
            @Override
            public void onSuccess(User user) {
                constants.logedInUser = user;
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
                            setProgressbarView ( false );
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

}