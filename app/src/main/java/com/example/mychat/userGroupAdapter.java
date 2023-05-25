package com.example.mychat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.User;
import com.example.mychat.oneononechat.oneoone_chat;
import java.util.List;


public class userGroupAdapter extends RecyclerView.Adapter<userGroupAdapter.MyViewHolder > {

    private final List<User> list;
    private  final Context context;

    public userGroupAdapter(List<User> list, Context context) {
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
        holder.bind (list.get ( position ));
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super ( itemView );
            this.textView = itemView.findViewById ( R.id.userNameTextView );
            this.linearLayout = itemView.findViewById ( R.id.linear_user_layout );
        }
        public void bind(User user){
            textView.setText ( user.getName () );
            linearLayout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    final String user_id = user.getUid ();
                    oneoone_chat.start ( context, user_id);
                }
            } );
        }
    }
}
