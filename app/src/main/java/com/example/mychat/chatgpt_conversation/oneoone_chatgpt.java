package com.example.mychat.chatgpt_conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.constants;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qifan.library.ChatTypingIndicatorView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Retrofit.ChatCompletionResponse;
import Retrofit.RetrofitInstance;
import models.messageWrapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class oneoone_chatgpt extends AppCompatActivity {
    private TextView welcome_text;
    private AppCompatImageView back_button;
    private RoundedImageView gpt_avatar;
    private TextView gpt_name;
    private AppCompatImageView info;
    private MessagesListAdapter<messageWrapper> adapter;
    private ChatTypingIndicatorView indicator;
    private MessageInput messageInput;
    private TextView gpt_pressence;
    private User user;
    private User logedinuser;
    private MessagesList messagesList;
    public static void start(Context context, User user){
        Intent starter = new Intent (context, oneoone_chatgpt.class);
        constants.User = user;
        context.startActivity ( starter );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_oneoone_chatgpt );
        Intent intent = getIntent ();
        if(intent != null){
            user = constants.User;
        }
        logedinuser = CometChat.getLoggedInUser ();
        back_button = findViewById ( R.id.imageBack );
        gpt_avatar = findViewById ( R.id.chatgpt_avatar );
        gpt_name = findViewById ( R.id.chat_gpt_name );
        info = findViewById ( R.id.info );
        indicator = findViewById ( R.id.indicatorView );
        messageInput = findViewById ( R.id.sender_input );
        messagesList = findViewById ( R.id.mesageList );
        gpt_pressence = findViewById ( R.id.user_pressence );
        welcome_text = findViewById ( R.id.welcome_text );
        initView ();
        setgptPressence ();
        setUserImage ();
        setText ();
        fetchPreviousMessages ();
        onBack ();
        infoPressed ();
        setIndicator ( false );
        setWelcomeTextViewVisibity ( true );
    }

    private void initView(){
        messageInput.setInputListener ( input -> {
            String msg = input.toString ();
            sendRequest (msg);
            sendMessage ( msg );
            setIndicator ( true );
            return true;
        } );
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get ().load ( url ).into ( gpt_avatar );
        adapter = new MessagesListAdapter<> ( logedinuser.getUid (), imageLoader);
        messagesList.setAdapter ( adapter );
    }

    private void sendRequest(String query) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put ( "model","gpt-3.5-turbo" );
            JSONArray jsonArray = new JSONArray ();
            JSONObject jsonObject1 = new JSONObject ();
            jsonObject1.put ( "role","system" );
            jsonObject1.put ( "content",query );
            jsonArray.put ( 0, jsonObject1);
            jsonObject.put("messages",jsonArray);

        } catch (JSONException e) {
            Log.d ( "MYTAG",e.getMessage () );
            throw new RuntimeException ( e );
        }
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(jsonObject.toString());
            }
        };
        RetrofitInstance.getInstance ().apiInterface.getData (requestBody).enqueue ( new retrofit2.Callback<ChatCompletionResponse> () {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ChatCompletionResponse> call, @NonNull retrofit2.Response<ChatCompletionResponse> response) {
                if(response.isSuccessful ()){
                    assert response.body () != null;
                    String content = response.body ().getChoices ()[0].getMessage ().getContent ();
                    TextMessage textMessage = new TextMessage ( user.getUid (),content, CometChatConstants.RECEIVER_TYPE_USER );
                    textMessage.setSender ( user );
                    addMessage ( textMessage );
                    setIndicator ( false );
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ChatCompletionResponse> call, @NonNull Throwable t) {
                Log.d ( "MYTAG","failurse occued" + t.getMessage () );
                String msg = "Request failed !!";
                TextMessage textMessage = new TextMessage ( logedinuser.getUid (),msg, CometChatConstants.RECEIVER_TYPE_USER );
                textMessage.setSender ( logedinuser );
                addMessage ( textMessage );
            }
        } );
    }

    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder ().setUID ( user.getUid () ).build ();
        messagesRequest.fetchPrevious ( new CometChat.CallbackListener<List<BaseMessage>> () {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        addMessagesbase(baseMessages);
                    }
                } );
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","Previous not msgs fetched" );
            }
        } );
    }
    private void addMessagesbase(List<BaseMessage> baseMessages) {
        List<messageWrapper> list = new ArrayList<> ();
        for(BaseMessage message:baseMessages){
            if(message instanceof TextMessage){
                list.add ( new messageWrapper ( (TextMessage) message ) );
            }
        }
        adapter.addToEnd ( list,true );
        messagesList.smoothScrollToPosition (list.size ());
    }

    private void setgptPressence(){
        gpt_pressence.setText ( R.string.online );
        gpt_pressence.setTextColor ( ContextCompat.getColor(oneoone_chatgpt.this, R.color.green)  );
    }
    private void onBack(){
        back_button.setOnClickListener ( v -> {
            finish ();
        } );
    }
    private void setUserImage(){
        Picasso.get ().load ( user.getAvatar () ).into ( gpt_avatar );
    }

    private void setText(){
        gpt_name.setText ( user.getName () );
    }

    private void infoPressed(){
        info.setOnClickListener ( v -> Toast.makeText ( oneoone_chatgpt.this, "Features will bw added soon !!", Toast.LENGTH_SHORT ).show () );
    }

    private void setWelcomeTextViewVisibity(boolean var) {
        runOnUiThread ( () -> {
            if(var){
                welcome_text.setVisibility ( View.VISIBLE );
            }
            else{
                welcome_text.setVisibility ( View.GONE );
            }
        } );
    }
    private void setIndicator(boolean var){
        runOnUiThread ( () -> {
            if(var){
                indicator.setVisibility ( View.VISIBLE );
            }
            else{
                indicator.setVisibility ( View.GONE );
            }
        } );
    }
    private void sendMessage(String message){
        TextMessage textMessage = new TextMessage ( user.getUid (),message,CometChatConstants.RECEIVER_TYPE_USER );
        CometChat.sendMessage ( textMessage, new CometChat.CallbackListener<TextMessage> () {
            @Override
            public void onSuccess(TextMessage textMessage) {
                addMessage ( textMessage );
                Log.d ( "MYTAG","Message sent !!" );
            }

            @Override
            public void onError(CometChatException e) {
                Log.d ( "MYTAG","Message not sent !!" );
            }
        } );
    }
    private void addMessage(TextMessage textMessage){
        setWelcomeTextViewVisibity ( false );
        runOnUiThread ( () -> {
            adapter.addToStart ( new messageWrapper ( textMessage ), true );
        } );
    }
}