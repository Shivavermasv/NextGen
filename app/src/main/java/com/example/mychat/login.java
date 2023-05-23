package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        //login in user using come-to-chat API

    }
}