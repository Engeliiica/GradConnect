package com.pbde.gradconnect.ui.employer.chats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.repository.ChatsRepository;

import java.util.ArrayList;
import java.util.List;

public class EmployerChatsViewModel extends ViewModel {
    private final ChatsRepository chatsRepository;
    private final MutableLiveData<List<Chat>> _chats = new MutableLiveData<>();
    private final LiveData<List<Chat>> chats = _chats;
    
    // Add states for UI management
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> isLoading = _isLoading;
    
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final LiveData<String> errorMessage = _errorMessage;

    public EmployerChatsViewModel() {
        this.chatsRepository = new ChatsRepository();
    }

    public void loadChats(User user) {
        if (user == null) {
            _errorMessage.setValue("User not authenticated");
            return;
        }

        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        
        String currentUserId = user.getId();
        chatsRepository.getEmployerChats(currentUserId).observeForever(chatsList -> {
            _isLoading.setValue(false);
            
            if (chatsList != null) {
                _chats.setValue(chatsList);
            } else {
                _chats.setValue(new ArrayList<>());
                _errorMessage.setValue("Failed to load chats");
            }
        });
    }

    public LiveData<List<Chat>> getChats() {
        return chats;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void retry(User user) {
        loadChats(user);
    }
}
