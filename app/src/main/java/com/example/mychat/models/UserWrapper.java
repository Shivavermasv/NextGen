package com.example.mychat.models;

import androidx.annotation.Keep;

import com.cometchat.pro.models.User;
import com.stfalcon.chatkit.commons.models.IUser;
@Keep
public class UserWrapper implements IUser {
    private final User user;


    public UserWrapper(User user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getUid ();
    }

    @Override
    public String getName() {
        return user.getName ();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar ();
    }
}
