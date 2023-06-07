package com.example.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class signup extends AppCompatActivity {

    private RoundedImageView layout_image;
    private Button signup;
    private EditText username;
    private EditText email;
    private EditText pass;
    private EditText repass;
    private TextView login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.signup );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        signup = findViewById ( R.id.signup_procced );
        username = findViewById ( R.id.username );
        email = findViewById ( R.id.email );
        pass = findViewById ( R.id.pass );
        repass = findViewById ( R.id.repass );
        login = findViewById ( R.id.loginAccount );
        layout_image = findViewById ( R.id.layoutimage );

        onLoginClicked();
        onSignUpClicked();
    }

    private void onSignUpClicked() {
        signup.setOnClickListener ( view -> {
            final String user_name = username.getText().toString ();
            final String Email = email.getText ().toString ();
            final String password = pass.getText ().toString ();

            if(!user_name.isEmpty ()) {
                if (!Email.isEmpty () && Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ()) {
                    if (password.length () >= 6 && password.equals ( repass.getText ().toString () )) {
                        User user = new User();
                        user.setUid(password); // Replace with the UID for the user to be created
                        user.setName(user_name); // Replace with the name of the user
                        setDefaultAvatar(user);

                        createUser(user);

                    } else if (password.length () < 6) {
                        pass.setError ( "Password must be above 6 or above" );
                    } else {
                        Toast.makeText ( signup.this, "Password should match !!", Toast.LENGTH_SHORT ).show ();
                    }
                } else if (Email.isEmpty ()) {
                    email.setError ( "Enter Email !!" );
                } else {
                    email.setError ( "Enter a valid Email !!" );
                }
            }
            else{
                username.setError ( "Enter valid username !!" );
            }
        } );
    }

    private void createUser(User user) {
        CometChat.createUser(user, AppKeys.COMETCHAT_AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d ( "MYTAG","User Created Sucessfully" );
                loginUser(user.getUid ());
            }
            @Override
            public void onError(CometChatException e) {
                Log.d ("MYTAG",e.getMessage ());
            }
        });
    }

    private void loginUser(String password) {
        CometChat.login (password, AppKeys.COMETCHAT_AUTH_KEY,new CometChat.CallbackListener<User> () {
            @Override
            public void onSuccess(User user) {
                startActivity ( new Intent (signup.this, userConversation.class) );
                finish ();
            }
            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG",e.getMessage () );
            }
        } );
    }

    private void onLoginClicked() {
        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (signup.this, com.example.mychat.login.class) );
                finishAffinity ();
            }
        } );
    }

    private void setDefaultAvatar(User user) {
        user.setAvatar ( "https://img.icons8.com/?size=512&id=ABBSjQJK83zf&format=png" );
        Picasso.get ().load ( "https://img.icons8.com/?size=512&id=ABBSjQJK83zf&format=png" ).into ( layout_image );
    }

}