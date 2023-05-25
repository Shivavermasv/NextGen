package com.example.mychat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class signup extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.signup );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Button signup = findViewById ( R.id.signup_procced );
        final EditText username = findViewById ( R.id.username );
        final EditText email = findViewById ( R.id.email );
        final EditText pass = findViewById ( R.id.pass );
        final EditText repass = findViewById ( R.id.repass );
        final TextView login = findViewById ( R.id.loginAccount );


        login.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (signup.this, com.example.mychat.login.class) );
                finish ();
            }
        } );
        signup.setOnClickListener ( view -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance ();
            String user_name = username.getText().toString ();
            String Email = email.getText ().toString ();
            String password = pass.getText ().toString ();

            if(!user_name.isEmpty ()) {
                if (!Email.isEmpty () && Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ()) {
                    if (password.length () >= 6 && password.equals ( repass.getText ().toString () )) {
                        User user = new User();
                        String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
                        user.setUid(password); // Replace with the UID for the user to be created
                        user.setName(Email); // Replace with the name of the user
                        CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                String authKey = "c467d5dd210f0048b95dae46b075ab87efc70f8b";
                                CometChat.login (password, authKey,new CometChat.CallbackListener<User> () {
                                    @Override
                                    public void onSuccess(User user) {
                                        if(CometChat.getLoggedInUser ().getAvatar () !=null){
                                            startActivity ( new Intent (signup.this, groupList.class) );
                                            finish ();
                                        }
                                        else{
                                            startActivity ( new Intent (signup.this, avatar_uploader.class) );
                                            finish ();
                                        }
                                    }
                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.d ( "MYTAG",e.getMessage () );
                                    }
                                } );
                            }
                            @Override
                            public void onError(CometChatException e) {
                                Log.d ("MYTAG",e.getMessage ());
                              //  Toast.makeText ( signup.this, "user creation failed !!", Toast.LENGTH_SHORT ).show ();
                            }
                        });
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

}