package com.example.mychat.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.ui.chat_screen;
import com.example.mychat.ui.chat_interface.gpt_chat_interface;
import com.example.mychat.ui.chat_interface.one_on_one_chat_interface;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class conversations_fetch_adapter extends RecyclerView.Adapter<conversations_fetch_adapter.MyViewholder> {

    private final List<Conversation> conversations;
    private final Context context;
    public conversations_fetch_adapter(List<Conversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
    }

    @NonNull
    @Override
    public conversations_fetch_adapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate( R.layout.converstaion_layout, parent,false);
        return new MyViewholder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull conversations_fetch_adapter.MyViewholder holder, int position) {
        try {
            holder.bind ( conversations.get ( position ) );
        } catch (JSONException e) {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size ();
    }
    public class MyViewholder extends RecyclerView.ViewHolder{
        private final TextView user_converstaion_view ;
        private final RoundedImageView image;
        private final AppCompatImageView user_status;
        private final LinearLayout layout;
        private final TextView last_message;

        public MyViewholder(@NonNull View itemView) {
            super ( itemView );
            user_status = itemView.findViewById ( R.id.user_status );
            user_converstaion_view = itemView.findViewById ( R.id.userConversationTextView );
            layout = itemView.findViewById ( R.id.conversation_layout );
            image = itemView.findViewById ( R.id.reciver_layoutimage );
            last_message = itemView.findViewById ( R.id.last_msg );
        }

        public void bind(Conversation conversation) throws JSONException {
            if(conversation.getConversationType ().equals ( CometChatConstants.CONVERSATION_TYPE_USER )){
                User user = (User)conversation.getConversationWith ();
                user_converstaion_view.setText ( user.getUid () );
                if(user.getAvatar ()!=null){
                    Picasso.get ().load ( user.getAvatar () ).into ( image );
                }
                JSONObject jsonObject = (JSONObject)conversation.getLastMessage ().getRawMessage ().get ( "data" );
                last_message.setText ( jsonObject.get ( "text" ).toString () );
                setUserStatus(user);
            }
            else{
                Group group = (Group) conversation.getConversationWith ();
                user_converstaion_view.setText ( group.getName () );
                if(group.getIcon ()!=null){
                    Picasso.get ().load ( group.getIcon () ).into ( image );
                }
                JSONObject jsonObject = (JSONObject)conversation.getLastMessage ().getRawMessage ().get ( "data" );
                String last_msg_from = conversation.getLastMessage ().getSender ().getName ();
                try{
                    last_message.setText ( String.format ( "%s: %s", last_msg_from, jsonObject.get ( "text" ) ) );
                }catch (JSONException e){
                    Log.d ( "MYTAG",e.getMessage () );
                }
                user_status.setVisibility ( View.GONE );
            }

            layout.setOnClickListener ( v -> {
                if(conversation.getConversationType ().equals ( CometChatConstants.CONVERSATION_TYPE_USER )){
                    User user = (User) conversation.getConversationWith ();
                    if(user.getUid ().equals ( context.getString( R.string.chatgpt) )){
                        context.startActivity ( gpt_chat_interface.start (context,user));
                    }
                    else{
                        context.startActivity ( one_on_one_chat_interface.start ( context, user ) );
                    }
                }
                else{
                    Group grp = (Group) conversation.getConversationWith ();
                    chat_screen.start(context,grp.getGuid (), grp );
                }
            } );
        }
        private void setUserStatus(User user){
            Drawable vectorDrawable = user_status.getDrawable();
            if(user.getUid ().equals ( "chatgpt" )){
                int color = ContextCompat.getColor(itemView.getContext(), R.color.green);
                vectorDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                user_status.setImageDrawable(vectorDrawable);

            }
            else if(user.getStatus ().equals ( CometChatConstants.USER_STATUS_ONLINE )){
                int color = ContextCompat.getColor(itemView.getContext(), R.color.green);
                vectorDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                user_status.setImageDrawable(vectorDrawable);

            }
            else{
                int color = ContextCompat.getColor(itemView.getContext(), R.color.red);
                vectorDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                user_status.setImageDrawable(vectorDrawable);

            }
        }
    }
}
