package com.pbde.gradconnect.ui.candidate.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.repository.ChatsRepository;

import java.util.ArrayList;
import java.util.List;

public class CandidateChatsViewModel extends ViewModel {
    private final ChatsRepository chatsRepository;
    private final MutableLiveData<List<Chat>> _chats = new MutableLiveData<>();
    private final LiveData<List<Chat>> chats = _chats;

    public CandidateChatsViewModel() {
        this.chatsRepository = new ChatsRepository();
    }

    public void loadChats(User user) {
        String currentUserId = user.getId();
        chatsRepository.getCandidateChats(currentUserId).observeForever(chatsList -> {
            _chats.setValue(chatsList != null ? chatsList : new ArrayList<>());
        });
    }

    public LiveData<List<Chat>> getChats() {
        return chats;
    }
}
