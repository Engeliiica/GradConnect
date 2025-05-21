package com.pbde.gradconnect.screens.graduate.chats;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.screens.common.BaseChatListAdapter;
import com.pbde.gradconnect.util.AuthManager;

public class GraduateChatsFragment extends Fragment implements BaseChatListAdapter.OnChatClickListener {
    private GraduateChatsViewModel viewModel;
    private AuthManager authManager;
    private GraduateChatListAdapter adapter;
    private RecyclerView rvChats;
    private TextView tvNoMessages;

    public GraduateChatsFragment() {
        // Required empty public constructor
    }

    public static GraduateChatsFragment newInstance() {
        GraduateChatsFragment fragment = new GraduateChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GraduateChatsViewModel.class);

        authManager = AuthManager.getInstance();
        authManager.getCurrentUser().observe(this, user -> {
            if (user != null) viewModel.loadChats(user);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graduate_chats, container, false);
        
        rvChats = view.findViewById(R.id.rvChats);
        tvNoMessages = view.findViewById(R.id.tvNoMessages);
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new GraduateChatListAdapter(this);
        rvChats.setAdapter(adapter);

        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null && !chats.isEmpty()) {
                adapter.setChats(chats);
                rvChats.setVisibility(View.VISIBLE);
                tvNoMessages.setVisibility(View.GONE);
            } else {
                rvChats.setVisibility(View.GONE);
                tvNoMessages.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(requireContext(), GraduateChatActivity.class);
        intent.putExtra("chatId", chat.getId());
        startActivity(intent);
    }
}