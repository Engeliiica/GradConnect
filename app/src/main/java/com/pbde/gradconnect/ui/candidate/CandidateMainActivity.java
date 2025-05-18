package com.pbde.gradconnect.ui.candidate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.pbde.gradconnect.MainActivity;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.databinding.ActivityCandidateMainBinding;
import com.pbde.gradconnect.util.AuthManager;

public class CandidateMainActivity extends AppCompatActivity {
    private ActivityCandidateMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCandidateMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize AuthManager
        authManager = AuthManager.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_candidate_main);
        
        if (navHostFragment != null) {
            BottomNavigationView bottomNav = findViewById(R.id.menu_candidate_bottom_navigation);

            navController = navHostFragment.getNavController();
            appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_candidate_jobs,
                R.id.navigation_candidate_applications,
                R.id.navigation_candidate_chats,
                R.id.navigation_candidate_profile
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleLogout() {
        authManager.logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}