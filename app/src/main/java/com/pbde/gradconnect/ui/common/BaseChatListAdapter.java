package com.pbde.gradconnect.ui.common;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseChatListAdapter extends RecyclerView.Adapter<BaseChatListAdapter.ChatViewHolder> {
    protected List<Chat> chats = new ArrayList<>();
    protected final UserRepository userRepository;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    protected final OnChatClickListener listener;

    public BaseChatListAdapter(OnChatClickListener listener) {
        this.listener = listener;
        this.userRepository = new UserRepository();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = getChatAt(position);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(chat);
            }
        });
        
        // Display last message if available
        if (chat.getLastMessage() != null && !chat.getLastMessage().isEmpty()) {
            holder.tvLastMessage.setText(chat.getLastMessage());
            
            // Format and display timestamp
            if (chat.getLastMessageAt() != null) {
                long timeMillis = (chat.getLastMessageAt()).getTime(); // Convert Timestamp to Date first
                holder.tvLastMessageTime.setText(com.pbde.gradconnect.utils.DateUtils.formatTimestamp(timeMillis));
            } else {
                holder.tvLastMessageTime.setText("");
            }
        } else {
            holder.tvLastMessage.setText("No messages yet");
            holder.tvLastMessageTime.setText("");
        }
        
        // Load user data
        loadUserData(chat, holder);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    protected abstract void loadUserData(Chat chat, ChatViewHolder holder);

    public Chat getChatAt(int position) {
        return chats.get(position);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvContactCharacter;
        public final TextView tvEmployerName;
        public final TextView tvLastMessage;
        public final TextView tvLastMessageTime;

        public ChatViewHolder(View itemView) {
            super(itemView);
            tvContactCharacter = itemView.findViewById(R.id.tvContactCharacter);
            tvEmployerName = itemView.findViewById(R.id.tvEmployerName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvLastMessageTime = itemView.findViewById(R.id.tvLastMessageTime);
        }
    }
}
