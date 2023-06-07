package com.example.mychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.User;
import com.example.mychat.chatgpt_conversation.oneoone_chatgpt;
import com.example.mychat.oneononechat.oneoone_chat;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class userListAdapter extends RecyclerView.Adapter<userListAdapter.MyViewHolder > {

    private final List<User> list;
    private  final Context context;

    public userListAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate(R.layout.raw_user_layout, parent,false);
        MyViewHolder vh = new MyViewHolder ( view );
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get ( position );
        holder.bind (user);
        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(user.getUid ().equals ( "chatgpt" )){
                    oneoone_chatgpt.start ( context,user );
                    ((userActivity)context).finish();
                }
                else{
                    oneoone_chat.start ( context,user.getUid (),user );
                    ((userActivity)context).finish();
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView user_name;
        private LinearLayout linearLayout;
        private TextView User_id;
        private RoundedImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super ( itemView );
            this.user_name = itemView.findViewById ( R.id.userNameTextView );
            this.linearLayout = itemView.findViewById ( R.id.linear_user_layout );
            this.User_id = itemView.findViewById ( R.id.usersidTextView );
            imageView = itemView.findViewById ( R.id.reciver_layoutimage );
        }
        public void bind(User user){
            user_name.setText ( user.getName () );
            Picasso.get ().load ( user.getAvatar () ).into ( imageView );
            User_id.setText ( user.getUid () );
        }
    }
}
