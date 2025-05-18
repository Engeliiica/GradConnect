package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import com.pbde.gradconnect.data.models.enums.UserRole;
import java.util.Date;

public class Message extends BaseModel {
    @NonNull
    private String chatId;
    @NonNull
    private String senderId;
    @NonNull
    private UserRole senderRole;
    @NonNull
    private String content;
    @NonNull
    private Date timestamp;

    public Message(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt,
                  @NonNull String chatId, @NonNull String senderId, @NonNull UserRole senderRole,
                  @NonNull String content, @NonNull Date timestamp) {
        super(id, createdAt, updatedAt);
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getChatId() {
        return chatId;
    }

    public void setChatId(@NonNull String chatId) {
        this.chatId = chatId;
    }

    @NonNull
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(@NonNull String senderId) {
        this.senderId = senderId;
    }

    @NonNull
    public UserRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(@NonNull UserRole senderRole) {
        this.senderRole = senderRole;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Date timestamp) {
        this.timestamp = timestamp;
    }
}
