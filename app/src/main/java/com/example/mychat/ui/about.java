package com.example.mychat.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mychat.R;

public class about extends AppCompatActivity {
    private AppCompatImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_about );
        back_button = findViewById ( R.id.imageBack );
        onBackClicked();
    }

    private void onBackClicked() {
        back_button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (about.this, main_dashboard.class) );
                finish ();
            }
        } );
    }
}