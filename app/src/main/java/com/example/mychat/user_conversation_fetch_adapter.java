package com.example.mychat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.mychat.oneononechat.oneoone_chat;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class user_conversation_fetch_adapter extends RecyclerView.Adapter<user_conversation_fetch_adapter.MyViewholder> {

    private final List<Conversation> conversations;
    private final Context context;
    public user_conversation_fetch_adapter(List<Conversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
    }

    @NonNull
    @Override
    public user_conversation_fetch_adapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate(R.layout.converstaion_layout, parent,false);
        return new MyViewholder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull user_conversation_fetch_adapter.MyViewholder holder, int position) {
        holder.bind ( conversations.get ( position ) );
    }

    @Override
    public int getItemCount() {
        return conversations.size ();
    }
    public class MyViewholder extends RecyclerView.ViewHolder{
        private final TextView user_converstaion_view ;
        private final RoundedImageView image;
        private final LinearLayout layout;

        public MyViewholder(@NonNull View itemView) {
            super ( itemView );
            user_converstaion_view = itemView.findViewById ( R.id.userConversationTextView );
            layout = itemView.findViewById ( R.id.conversation_layout );
            image = itemView.findViewById ( R.id.reciver_layoutimage );
        }

        public void bind(Conversation conversation){
            String user_data = conversation.getConversationWith ().toString ();
            String redirect = null;
            User user = null;
            Group group = null;
            if(user_data.charAt ( 0 ) == 'U'){
                user = dataFetcher ( user_data );
                redirect = user.getUid ();
                user_converstaion_view.setText (user.getName () );
                if(user.getAvatar ()!=null){
                    Picasso.get ().load ( user.getAvatar () ).into ( image );
                }
            }
            else{
                group = dataFetcherg ( user_data );
                redirect = group.getGuid ();
                user_converstaion_view.setText (group.getName () );
                if(group.getIcon ()!=null){
                    Picasso.get ().load ( group.getIcon () ).into ( image );
                }
            }
            String finalRedirect = redirect;
            User finalUser = user;
            Group finalGroup = group;
            layout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if(user_data.charAt ( 0 )=='U'){
                        oneoone_chat.start ( context, finalRedirect, finalUser );
                    }
                    else{
                        chatActivity.start(context,finalRedirect, finalGroup );
                    }
                }
            } );
        }
        private User dataFetcher(String data){
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(data);
            User user = new User ();
            if (matcher.find())
            {
                user.setUid ( matcher.group (1) );
            }
            data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
            if (matcher.find())
            {
               user.setName ( matcher.group (1) );
            }
            data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
                while(matcher.find () && !URLUtil.isValidUrl ( matcher.group (1) ))
                {
                    data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
                }

            if(!matcher.hitEnd () && URLUtil.isValidUrl ( matcher.group (1) )){
                user.setAvatar ( matcher.group (1) );
            }
            return user;
        }
        private Group dataFetcherg(String data){
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(data);
            Group grp = new Group ();
            if (matcher.find())
            {
                grp.setGuid ( matcher.group (1) );
            }
            data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
            if (matcher.find())
            {
                grp.setName ( matcher.group (1) );
            }
            data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
            while(matcher.find () && !URLUtil.isValidUrl ( matcher.group (1) ))
            {
                data = data.replace( Objects.requireNonNull ( matcher.group ( 1 ) ),"");
            }

            if(!matcher.hitEnd () && URLUtil.isValidUrl ( matcher.group (1) )){
                grp.setIcon ( matcher.group (1) );
            }
            return grp;
        }
    }
}
