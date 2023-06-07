package models;

import androidx.annotation.Keep;

import com.cometchat.pro.models.TextMessage;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

@Keep
public class messageWrapper implements IMessage {

    private final TextMessage textMessage;

    public messageWrapper(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    @Override
    public String getId() {
        return textMessage.getMuid ();
    }

    @Override
    public String getText() {
        return textMessage.getText ();
    }

    @Override
    public IUser getUser() {
        return new UserWrapper ( textMessage.getSender () );
    }

    @Override
    public Date getCreatedAt() {
        return new Date (textMessage.getSentAt () * 1000);
    }
}
