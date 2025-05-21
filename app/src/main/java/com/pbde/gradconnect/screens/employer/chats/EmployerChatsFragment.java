package com.pbde.gradconnect.screens.employer.chats;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.screens.common.BaseChatListAdapter;
import com.pbde.gradconnect.util.AuthManager;

public class EmployerChatsFragment extends Fragment implements BaseChatListAdapter.OnChatClickListener {
    private EmployerChatsViewModel viewModel;
    private AuthManager authManager;
    private EmployerChatListAdapter adapter;
    private RecyclerView rvChats;
    private ProgressBar progressBar;
    private LinearLayout emptyStateView;
    private TextView errorView;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerChatsViewModel.class);
        
        authManager = AuthManager.getInstance();
        authManager.getCurrentUser().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                viewModel.loadChats(user);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_chats, container, false);
        
        // Initialize views
        rvChats = view.findViewById(R.id.rvEmployerChats);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        errorView = view.findViewById(R.id.errorView);
        
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new EmployerChatListAdapter(this);
        rvChats.setAdapter(adapter);

        setupObservers();

        // Set up error view click listener for retry
        errorView.setOnClickListener(v -> {
            if (currentUser != null) {
                viewModel.retry(currentUser);
            }
        });

        return view;
    }

    private void setupObservers() {
        // Observe chats data
        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            adapter.setChats(chats);
            updateUiVisibility(chats != null && !chats.isEmpty());
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                rvChats.setVisibility(View.GONE);
                emptyStateView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                errorView.setText(errorMessage);
                errorView.setVisibility(View.VISIBLE);
                rvChats.setVisibility(View.GONE);
                emptyStateView.setVisibility(View.GONE);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                errorView.setVisibility(View.GONE);
            }
        });
    }

    private void updateUiVisibility(boolean hasChats) {
        if (hasChats) {
            rvChats.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        } else {
            rvChats.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChatClick(Chat chat) {
        Intent intent = new Intent(requireContext(), EmployerChatActivity.class);
        intent.putExtra("chatId", chat.getId());
        startActivity(intent);
    }
}