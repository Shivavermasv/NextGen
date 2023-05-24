package com.example.mychat;

import android.os.Bundle;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_signup );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Button signup = findViewById ( R.id.signup_procced );
        final EditText username = findViewById ( R.id.username );
        final EditText email = findViewById ( R.id.email );
        final EditText pass = findViewById ( R.id.pass );
        final EditText repass = findViewById ( R.id.repass );
        signup.setOnClickListener ( view -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance ();
            String user_name = username.getText().toString ();
            String Email = email.getText ().toString ();
            String password = pass.getText ().toString ();
            if(!user_name.isEmpty ()) {
                if (!Email.isEmpty () && Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ()) {
                    if (password.length () >= 6 && password.equals ( repass.getText ().toString () )) {
                        auth.createUserWithEmailAndPassword ( Email, password ).addOnSuccessListener ( authResult -> {
                            Toast.makeText ( signup.this, "User created successfully ", Toast.LENGTH_SHORT ).show ();
                            finish ();
                        } ).addOnFailureListener ( e -> Toast.makeText ( signup.this, "user creation failed !!", Toast.LENGTH_SHORT ).show () );
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