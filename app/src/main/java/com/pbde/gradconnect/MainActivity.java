package com.pbde.gradconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.pbde.gradconnect.util.AuthManager;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.models.enums.UserRole;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private AppBarConfiguration appBarConfig;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Initialize AuthManager
        authManager = AuthManager.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            BottomNavigationView bottomNav = findViewById(R.id.menu_home_bottom_navigation);
            appBarConfig = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_login,
                R.id.navigation_register
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Observe authentication state
            authManager.getCurrentUser().observe(this, user -> {
                if (user != null) {
                    NavDestination currentDest = navController.getCurrentDestination();
                    if (currentDest == null) {
                        // If NavController is not ready, navigate to appropriate dashboard directly
                        navigateToUserDashboard(user.getRole());
                        return;
                    }
                    
                    int currentDestId = currentDest.getId();
                    navigateBasedOnDestination(currentDestId, user.getRole());
                }
            });
        }
    }

    private void navigateBasedOnDestination(int currentDestId, UserRole role) {
        int actionId;
        if (role == UserRole.CANDIDATE) {
            actionId = currentDestId == R.id.navigation_home ?
                R.id.action_navigation_home_to_activity_graduate_main :
                currentDestId == R.id.navigation_login ?
                    R.id.action_navigation_login_to_activity_graduate_main :
                    R.id.action_navigation_register_to_activity_graduate_main;
        } else {
            actionId = currentDestId == R.id.navigation_home ?
                R.id.action_navigation_home_to_activity_employer_main :
                currentDestId == R.id.navigation_login ?
                    R.id.action_navigation_login_to_activity_employer_main :
                    R.id.action_navigation_register_to_activity_employer_main;
        }
        
        try {
            navController.navigate(actionId);
        } catch (Exception e) {
            // Handle navigation error
            e.printStackTrace();
        }
    }

    private void navigateToUserDashboard(UserRole role) {
        try {
            if (role == UserRole.CANDIDATE) {
                navController.navigate(R.id.action_navigation_home_to_activity_graduate_main);
            } else {
                navController.navigate(R.id.action_navigation_home_to_activity_employer_main);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfig)
                || super.onSupportNavigateUp();
    }
}