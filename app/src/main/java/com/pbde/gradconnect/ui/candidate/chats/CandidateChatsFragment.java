package com.pbde.gradconnect.ui.candidate.chats;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.ui.common.BaseChatListAdapter;
import com.pbde.gradconnect.util.AuthManager;

public class CandidateChatsFragment extends Fragment implements BaseChatListAdapter.OnChatClickListener {
    private CandidateChatsViewModel viewModel;
    private AuthManager authManager;
    private CandidateChatListAdapter adapter;
    private RecyclerView rvChats;

    public CandidateChatsFragment() {
        // Required empty public constructor
    }

    public static CandidateChatsFragment newInstance() {
        CandidateChatsFragment fragment = new CandidateChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CandidateChatsViewModel.class);

        authManager = AuthManager.getInstance();
        authManager.getCurrentUser().observe(this, user -> {
            if (user != null) viewModel.loadChats(user);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate_chats, container, false);
        
        rvChats = view.findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new CandidateChatListAdapter(this);
        rvChats.setAdapter(adapter);

        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null && !chats.isEmpty()) {
                adapter.setChats(chats);
                rvChats.setVisibility(View.VISIBLE);
            } else {
                rvChats.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(requireContext(), CandidateChatActivity.class);
        intent.putExtra("chatId", chat.getId());
        startActivity(intent);
    }
}