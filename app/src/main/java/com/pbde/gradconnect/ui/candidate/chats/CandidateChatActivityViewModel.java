package com.pbde.gradconnect.ui.candidate.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.models.Message;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.repository.ChatsRepository;
import com.pbde.gradconnect.util.AuthManager;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CandidateChatActivityViewModel extends ViewModel {
    private final ChatsRepository chatsRepository;
    private final AuthManager authManager;
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private String currentChatId;

    public CandidateChatActivityViewModel() {
        this.chatsRepository = new ChatsRepository();
        this.authManager = AuthManager.getInstance();
    }

    public void loadMessages(String chatId) {
        this.currentChatId = chatId;
        chatsRepository.getChatMessages(chatId).observeForever(messages::setValue);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void sendMessage(String content) {
        User currentUser = authManager.getCurrentUser().getValue();
        if (currentUser == null || currentChatId == null) return;

        Date now = new Date();
        Message message = new Message(
            UUID.randomUUID().toString(),
            now,
            now,
            currentChatId,
            currentUser.getUid(),
            currentUser.getRole(),
            content,
            now
        );

        chatsRepository.createMessage(message).observeForever(newMessage -> {
            if (newMessage != null) {
                List<Message> currentMessages = messages.getValue();
                if (currentMessages != null) {
                    currentMessages.add(newMessage);
                    messages.setValue(currentMessages);
                }
            }
        });
    }
}