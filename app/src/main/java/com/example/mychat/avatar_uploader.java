package com.example.mychat;

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
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class avatar_uploader extends AppCompatActivity {

    private RoundedImageView user_iamge;
    private TextView add_iamge;
    private String encodedimage;
    JSONObject body ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_avatar_uploader );

        user_iamge = findViewById ( R.id.layoutimage );
        add_iamge = findViewById ( R.id.addImage );
        Button upload = findViewById ( R.id.upload_avatar );
        TextView skip = findViewById ( R.id.skip );
        body = new JSONObject ();
        user_iamge.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                intent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                pickimage.launch ( intent );
                try {
                    body.put("avatar", "data:image/png;base64," + encodedimage);

//                    body.put("avatar", "data:image/png;base64,"+encodedimage);
                } catch (JSONException e) {
                    Log.d ( "MYTAG", e.getMessage () + "encode wala" );
                    throw new RuntimeException ( e );
                }
            }
        } );
        //TO skip avatar uploading
        skip.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent (avatar_uploader.this, groupList.class) );
                finish ();
            }
        } );

        //Uploaing avatar to cometchat user database
        upload.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                CometChat.callExtension("avatar", "POST", "/v1/upload",body,
                        new CometChat.CallbackListener < JSONObject > () {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                Log.d ( "MYTAG", "SUCCESS IMAGE UPLOAD" );
                                startActivity ( new Intent (avatar_uploader.this, groupList.class) );
                                finish ();
                            }
                            @Override
                            public void onError(CometChatException e) {
                                // Some error occured
                                e.printStackTrace ();
                                Log.d ( "MYTAG", e.getMessage () + " api wla");
                            }
                        });
            }
        } );

    }

    //image encoding
    public String encodeImage(Bitmap bitmap) {
//        int previewWidth = 150;
//        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
//        Bitmap previewbitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        Bitmap previewbitmap = Bitmap.createBitmap ( bitmap );
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewbitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    //method to extract uri of image
    private ActivityResultLauncher<Intent> pickimage = registerForActivityResult ( new ActivityResultContracts.StartActivityForResult (),
            result -> {
                if(result.getResultCode () == RESULT_OK){
                    if(result.getData () != null){
                        Uri imageuri = result.getData ().getData ();
                        try{
                            InputStream inputStream = getContentResolver ().openInputStream ( imageuri );
                            Bitmap bitmap = BitmapFactory.decodeStream ( inputStream );
                            encodedimage = encodeImage ( bitmap );
                            user_iamge.setImageBitmap ( bitmap );
                            add_iamge.setVisibility ( View.GONE );
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException ( e );
                        }
                    }
                }
            });
}