package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView ( R.layout.activity_launcher );
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1200);
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(launcher.this , MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}
