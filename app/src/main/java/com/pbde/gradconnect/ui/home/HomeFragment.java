package com.pbde.gradconnect.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pbde.gradconnect.R;

public class HomeFragment extends Fragment {

    private static class FeatureData {
        private final String title;
        private final String description;

        FeatureData(String title, String description) {
            this.title = title;
            this.description = description;
        }

        String getTitle() { return title; }
        String getDescription() { return description; }
    }

    private static class Carousel {

        private final String foregroundDrawable;
        private final String label;
        private final String description;

        Carousel(String foregroundDrawable, String label, String description) {
            this.foregroundDrawable = foregroundDrawable;
            this.label = label;
            this.description = description;
        }

        String getForegroundDrawable() { return foregroundDrawable; }
        String getLabel() { return label; }
        String getDescription() { return description; }
    }

    private FeatureData[] features = {
        new FeatureData("For Candidates", 
            "Access exclusive opportunities tailored for candidates and recent graduates"),
        new FeatureData("For Employers",
            "Connect with talented candidates and graduates for your organization"),
        new FeatureData("Direct Communication",
            "Seamless communication between candidates and employers")
    };

    private Carousel[] carousels = {
        new Carousel("man_in_black_academic_gown_standing_on_brown_brick_floor_during_daytime.jpg",
            "Graduate Success",
            "Join thousands of successful graduates finding their dream careers"),
        new Carousel("a_man_sitting_in_front_of_a_laptop_computer.jpg",
            "Remote Opportunities",
            "Access remote and flexible work opportunities"),
        new Carousel("woman_in_gray_and_yellow_pussybow_top.jpg",
            "Professional Growth",
            "Build your professional network and grow your career"),
        new Carousel("a_man_in_a_cap_and_gown_standing_in_front_of_a_brick_wall.jpg",
            "Academic Excellence",
            "Excellence in education and career development"),
        new Carousel("a_woman_sitting_at_a_table_with_a_smile_on_her_face.jpg",
            "Career Satisfaction",
            "Find fulfilling career opportunities that match your ambitions"),
        new Carousel("man_in_blue_red_and_white_plaid_dress_shirt_holding_yellow_plastic_bowl.jpg",
            "Diverse Opportunities",
            "Access diverse career paths across multiple industries"),
        new Carousel("man_in_yellow_and_black_zip_up_jacket.jpg",
            "Industry Connection",
            "Connect with leading industry professionals")
    };

    private static final long AUTO_SCROLL_DELAY = 3000;
    private final Handler autoScrollHandler = new Handler();
    private Runnable autoScrollRunnable;
    private ViewPager2 viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupFeatures(view);
        setupButtons(view);

        TextView carouselTitle = view.findViewById(R.id.carousel_title);
        TextView carouselDescription = view.findViewById(R.id.carousel_description);
        viewPager = view.findViewById(R.id.viewPager);
        CarouselAdapter adapter = new CarouselAdapter(carousels);
        viewPager.setAdapter(adapter);

        // Set initial overlay text
        carouselTitle.setText(carousels[0].getLabel());
        carouselDescription.setText(carousels[0].getDescription());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Carousel item = carousels[position];
                carouselTitle.setText(item.getLabel());
                carouselDescription.setText(item.getDescription());
            }
        });

        TabLayout carouselIndicator = view.findViewById(R.id.carouselIndicator);
        new TabLayoutMediator(carouselIndicator, viewPager,
            (tab, position) -> {}
        ).attach();

        autoScrollRunnable = () -> {
            int current = viewPager.getCurrentItem();
            int next = (current + 1) % carousels.length;
            viewPager.setCurrentItem(next, true);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }


    private void setupFeatures(View view) {
        LinearLayout featuresContainer = view.findViewById(R.id.featuresContainer);

        for (FeatureData feature : features) {
            MaterialCardView card = (MaterialCardView) getLayoutInflater()
                .inflate(R.layout.component_home_feature_card, featuresContainer, false);

            TextView titleView = card.findViewById(R.id.feature_title);
            TextView descriptionView = card.findViewById(R.id.feature_description);

            titleView.setText(feature.getTitle());
            descriptionView.setText(feature.getDescription());

            featuresContainer.addView(card);
        }
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.getStartedButton).setOnClickListener(v -> {
            navigateToRegister();
        });

        view.findViewById(R.id.signInButton).setOnClickListener(v -> {
            navigateToLogin();
        });

        view.findViewById(R.id.ctaButton).setOnClickListener(v -> {
            navigateToRegister();
        });
    }

    private void navigateToRegister() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_home_to_navigation_register);
    }

    private void navigateToLogin() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_home_to_navigation_login);
    }

    private static class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
        private final Carousel[] items;

        CarouselAdapter(Carousel[] items) {
            this.items = items;
        }

        @NonNull
        @Override
        public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new CarouselViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
            // Convert string filename to drawable or load using a library
            String nameWithoutExt = items[position].getForegroundDrawable().replace(".jpg", "");
            int resId = holder.itemView.getContext().getResources().getIdentifier(
                nameWithoutExt, "drawable", holder.itemView.getContext().getPackageName());
            holder.imageView.setImageResource(resId);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        static class CarouselViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;

            CarouselViewHolder(@NonNull ImageView view) {
                super(view);
                this.imageView = view;
            }
        }
    }
}