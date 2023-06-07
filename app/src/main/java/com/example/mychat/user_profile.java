package com.example.mychat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class user_profile extends AppCompatActivity {
    private RoundedImageView profile_image;
    private TextView user_id;
    private TextView user_name;
    private  String image;
    private AppCompatImageView back_button;
    private User user;
    public static void start(Context context, User user){
        Intent starter = new Intent (context, user_profile.class);
        constants.User = user;
        context.startActivity ( starter );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_user_profile );
        profile_image = findViewById ( R.id.layoutimage );
        user_id = findViewById ( R.id.user_id );
        user_name = findViewById ( R.id.user_name );
        back_button = findViewById ( R.id.imageBack );
        Intent intent = getIntent ();
        if(intent!=null){
            this.user = constants.User;
        }
        getUserAvatar();
        setUserName();
        setUserAvatar();
        SetUserId();
        onBackClicked ();
    }

    private void uploadImage(JSONObject body) {
        CometChat.callExtension("avatar", "POST", "/v1/upload",body,
                new CometChat.CallbackListener < JSONObject > () {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Log.d ( "MYTAG", "SUCCESS IMAGE UPLOAD" );
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

    private void onBackClicked() {
        back_button.setOnClickListener ( v -> {
            startActivity ( new Intent (user_profile.this, userConversation.class) );
            finish ();
        } );
    }
    private void getUserAvatar() {
        Picasso.get ().load ( user.getAvatar () ).into ( profile_image );
    }

    private void SetUserId() {
        user_id.setText ( String.format ( "User ID :%s", user.getUid () ) );
    }

    private void setUserAvatar() {
        profile_image.setOnClickListener ( v -> {
            Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            intent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            pickimage.launch ( intent );
        } );
    }

    private void setUserName() {
        user_name.setText ( String.format ( "User Name :%s", user.getName () ) );
    }
    public String encodeImage(Bitmap bitmap) {
        Log.d ( "MYTAG","encoding started" );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
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
                                    profile_image.setImageBitmap ( bitmap );
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
                            uploadImage (body);
//                    body.put("avatar", "data:image/png;base64,"+encodedimage);
                        } catch (JSONException e) {
                            Log.d ( "MYTAG", e.getMessage ()  );
                            throw new RuntimeException ( e );
                        }
                    }
                }
            });

}