package com.pbde.gradconnect.screens.employer.chats;

import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.screens.common.BaseChatListAdapter;
import com.pbde.gradconnect.data.models.Chat;

public class EmployerChatListAdapter extends BaseChatListAdapter {

    public EmployerChatListAdapter(OnChatClickListener listener) {
        super(listener);
    }

    @Override
    protected void loadUserData(Chat chat, ChatViewHolder holder) {
        userRepository.getUserById(chat.getCandidateId())
            .addOnSuccessListener(user -> {
                if (user instanceof Graduate) {
                    Graduate graduates = (Graduate) user;
                    String fullName = user.getFullName();
                    holder.tvContactCharacter.setText(fullName.substring(0, 1).toUpperCase());
                    holder.tvEmployerName.setText(fullName);
                }
            });
    }
}
