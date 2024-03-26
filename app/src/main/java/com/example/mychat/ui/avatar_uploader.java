package com.example.mychat.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.mychat.R;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class avatar_uploader extends AppCompatActivity {

    private RoundedImageView user_image;
    private TextView add_image;
    private TextView skip;
    private Button upload;
    private String image;
    private RelativeLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_avatar_uploader );

        user_image = findViewById ( R.id.layoutimage );
        add_image = findViewById ( R.id.addImage );
        upload = findViewById ( R.id.upload_avatar );
        skip = findViewById ( R.id.skip );
        progressLayout = findViewById ( R.id.progress_bar_layout );
        onUserImageClicked();
        onSkipClicked();
    }
    private void setProgressView(boolean var){
        if(var){
            progressLayout.setVisibility ( View.VISIBLE );
        }
        else{
            progressLayout.setVisibility ( View.INVISIBLE );
        }
    }

    private void onUploadClicked(JSONObject body) {
        if(body != null){
            upload.setOnClickListener ( v -> uploadImage ( body ) );
        }
    }

    private void uploadImage(JSONObject body) {
        setProgressView ( true );
        CometChat.callExtension("avatar", "POST", "/v1/upload",body,
                new CometChat.CallbackListener < JSONObject > () {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Log.d ( "MYTAG", "SUCCESS IMAGE UPLOAD" );
                        setProgressView ( false );
                        finish ();
                    }
                    @Override
                    public void onError(CometChatException e) {
                        // Some error occured
                        e.printStackTrace ();
                        Log.d ( "MYTAG", e.getMessage () + "API respose");
                    }
                });
    }

    private void onSkipClicked() {
        skip.setOnClickListener ( v -> {
            startActivity ( new Intent (avatar_uploader.this, main_dashboard.class) );
            finish ();
        } );
    }

    private void onUserImageClicked() {
        user_image.setOnClickListener ( v -> {
            Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            intent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            pickimage.launch ( intent );
        } );

    }

    //image encoding
    public String encodeImage(Bitmap bitmap) {
        Log.d ( "MYTAG","encoding started" );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }


    //method to extract uri of image
    private final ActivityResultLauncher<Intent> pickimage = registerForActivityResult ( new ActivityResultContracts.StartActivityForResult (),
            result -> {
                if(result.getResultCode () == RESULT_OK){
                    if(result.getData () != null){
                        Uri imageuri = result.getData ().getData ();
                        try{
                            InputStream inputStream = getContentResolver ().openInputStream ( imageuri );
                            if (inputStream != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                if (bitmap != null) {
                                    image = encodeImage ( bitmap );
                                    user_image.setImageBitmap ( bitmap );
                                    add_image.setVisibility ( View.INVISIBLE );
                                } else {
                                    Log.d("MYTAG", "Failed to decode bitmap from input stream");
                                }
                            } else {
                                Log.d("MYTAG", "Input stream is null");
                            }
                        } catch (FileNotFoundException e) {
                            Log.d ( "MYTAG",e.getMessage () );
                            throw new RuntimeException ( e );
                        }
                        JSONObject body = new JSONObject ();
                        try {
                            body.put("avatar", "data:image/jpeg;base64," + image);
                            onUploadClicked ( body );
//                    body.put("avatar", "data:image/png;base64,"+encodedimage);
                        } catch (JSONException e) {
                            Log.d ( "MYTAG", e.getMessage ()  );
                            throw new RuntimeException ( e );
                        }
                    }
                }
            });
}