package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.ui.chat_interface.gpt_chat_interface;
import com.example.mychat.ui.chat_interface.one_on_one_chat_interface;
import com.example.mychat.ui.contact_list;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class user_list_fetch_adapter extends RecyclerView.Adapter<user_list_fetch_adapter.MyViewHolder > {

    private final List<User> list;
    private  final Context context;

    public user_list_fetch_adapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate( R.layout.raw_user_layout, parent,false);
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
                    context.startActivity ( gpt_chat_interface.start ( context,user ));
                    ((contact_list)context).finish();
                }
                else{
                    context.startActivity ( one_on_one_chat_interface.start ( context,user ));
                    ((contact_list)context).finish();
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView user_name;
        private final TextView User_id;
        private final RoundedImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super ( itemView );
            this.user_name = itemView.findViewById ( R.id.userNameTextView );
            LinearLayout linearLayout = itemView.findViewById ( R.id.linear_user_layout );
            this.User_id = itemView.findViewById ( R.id.usersidTextView );
            imageView = itemView.findViewById ( R.id.reciver_layoutimage );
        }
        public void bind(User user){
            user_name.setText ( user.getUid () );
            Picasso.get ().load ( user.getAvatar () ).into ( imageView );
            User_id.setText ( user.getName () );
        }
    }
}
