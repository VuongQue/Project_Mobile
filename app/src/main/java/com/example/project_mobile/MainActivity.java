package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.project_mobile.adapter.ViewPager2Adapter;
import com.example.project_mobile.databinding.ActivityMainBinding;
import com.example.project_mobile.databinding.BottomNavBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BottomNavBinding bottomNavBinding;
    private ViewPager2Adapter viewPager2Adapter;
    private int selectedTab = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavBinding = BottomNavBinding.bind(binding.bottomNav.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager2Adapter = new ViewPager2Adapter(fragmentManager, getLifecycle());
        binding.viewPager2.setAdapter(viewPager2Adapter);

        BottomNavigationBarProcess();
    }

    private void BottomNavigationBarProcess() {
        LinearLayout[] layouts = {
                bottomNavBinding.homeLayout,
                bottomNavBinding.stateLayout,
                bottomNavBinding.profileLayout,
                bottomNavBinding.settingsLayout
        };

        TextView[] texts = {
                bottomNavBinding.homeTxt,
                bottomNavBinding.stateTxt,
                bottomNavBinding.profileTxt,
                bottomNavBinding.settingsTxt
        };

        for (int i = 0; i < layouts.length; i++) {
            final int index = i; //
            final int selectedIndex = i + 1; //

            layouts[i].setOnClickListener(view -> {
                if (selectedTab != selectedIndex) {
                    resetTabs(texts, layouts);
                    texts[index].setVisibility(View.VISIBLE);
                    layouts[index].setBackgroundResource(R.drawable.selected_icon);
                    animateView(layouts[index]);
                    selectedTab = selectedIndex;

                    binding.viewPager2.setCurrentItem(index, true);
                }
            });
        }
    }
    private void resetTabs(TextView[] texts, LinearLayout[] layouts) {
        for (int i = 0; i < layouts.length; i++) {
            texts[i].setVisibility(View.GONE);
            layouts[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
    private void animateView(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.8f, 1.0f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        view.startAnimation(scaleAnimation);
    }
}
