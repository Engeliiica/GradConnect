package com.pbde.gradconnect.screens.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.pbde.gradconnect.R;

public class HomeFragment extends Fragment {

    private static class Carousel {

        private final String foregroundDrawable;

        Carousel(String foregroundDrawable, String label, String description) {
            this.foregroundDrawable = foregroundDrawable;
        }

        String getForegroundDrawable() { return foregroundDrawable; }
    }


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

        setupButtons(view);

        return view;
    }

    private void setupButtons(View view) {
       view.findViewById(R.id.signUpButton).setOnClickListener(v -> {
            navigateToRegister();
        });

        view.findViewById(R.id.signInButton).setOnClickListener(v -> {
            navigateToLogin();
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