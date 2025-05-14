package com.example.project_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_mobile.adapter.SwitchFragmentAdapter;
import com.example.project_mobile.databinding.ActivityMainBinding;
import com.example.project_mobile.databinding.BottomNavBinding;
import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.SetUp;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavBinding bottomNavBinding;
    private SwitchFragmentAdapter switchFragmentAdapter;
    private PreferenceManager preferenceManager;
    private SetUp setUp;
    private int selectedTab = 1;
    private boolean isGuest;

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

        preferenceManager = new PreferenceManager(this);
        setUp = new SetUp(this);

        setUp.loadLocale();
        setUp.loadTheme();

        isGuest = GuestManager.isGuest(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        switchFragmentAdapter = new SwitchFragmentAdapter(fragmentManager, getLifecycle());
        binding.viewPager2.setAdapter(switchFragmentAdapter);

        // Lấy tab đã chọn trước khi recreate
        SharedPreferences tabPref = getSharedPreferences("tabState", MODE_PRIVATE);
        selectedTab = tabPref.getInt("currentTab", 1);

        // Nếu là Guest và đang ở Notification hoặc Profile, chuyển về Home
        if (isGuest && (selectedTab == 2 || selectedTab == 3)) {
            selectedTab = 1;
        }

        binding.viewPager2.setCurrentItem(selectedTab - 1, false);
        setupBottomNavigation();
        setupViewPagerListener(); // Thêm phương thức này
    }

    /**
     * Điều hướng đến LoginActivity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Kiểm soát khi người dùng kéo ViewPager2
     */
    private void setupViewPagerListener() {
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int selectedIndex = position + 1;

                // Nếu là Guest và chuyển sang Notification hoặc Profile
                if (isGuest && (selectedIndex == 2 || selectedIndex == 3)) {
                    binding.viewPager2.setCurrentItem(0, false); // Trở về Home
                    navigateToLogin();
                } else {
                    selectedTab = selectedIndex;
                    updateTabState(selectedIndex);
                }
            }
        });
    }

    /**
     * Cập nhật trạng thái tab khi người dùng kéo ViewPager2
     */
    private void updateTabState(int selectedIndex) {
        LinearLayout[] layouts = {
                bottomNavBinding.homeLayout,
                bottomNavBinding.notificationLayout,
                bottomNavBinding.profileLayout,
                bottomNavBinding.settingsLayout
        };

        TextView[] texts = {
                bottomNavBinding.homeTxt,
                bottomNavBinding.notificationTxt,
                bottomNavBinding.profileTxt,
                bottomNavBinding.settingsTxt
        };

        resetTabs(texts, layouts);

        if (selectedIndex >= 1 && selectedIndex <= layouts.length) {
            texts[selectedIndex - 1].setVisibility(View.VISIBLE);
            layouts[selectedIndex - 1].setBackgroundResource(R.drawable.selected_icon);
            animateView(layouts[selectedIndex - 1]);
        }
    }

    /**
     * Thiết lập sự kiện click cho bottom navigation
     */
    private void setupBottomNavigation() {
        LinearLayout[] layouts = {
                bottomNavBinding.homeLayout,
                bottomNavBinding.notificationLayout,
                bottomNavBinding.profileLayout,
                bottomNavBinding.settingsLayout
        };

        TextView[] texts = {
                bottomNavBinding.homeTxt,
                bottomNavBinding.notificationTxt,
                bottomNavBinding.profileTxt,
                bottomNavBinding.settingsTxt
        };

        for (int i = 0; i < layouts.length; i++) {
            final int index = i;
            final int selectedIndex = i + 1;

            layouts[i].setOnClickListener(view -> {
                if (isGuest && (selectedIndex == 2 || selectedIndex == 3)) {
                    navigateToLogin();
                    return;
                }

                if (selectedTab != selectedIndex) {
                    resetTabs(texts, layouts);
                    texts[index].setVisibility(View.VISIBLE);
                    layouts[index].setBackgroundResource(R.drawable.selected_icon);
                    animateView(layouts[index]);

                    selectedTab = selectedIndex;

                    // Lưu trạng thái tab
                    SharedPreferences.Editor editor = getSharedPreferences("tabState", MODE_PRIVATE).edit();
                    editor.putInt("currentTab", selectedTab);
                    editor.apply();

                    binding.viewPager2.setCurrentItem(index, true);
                }
            });
        }
    }

    /**
     * Reset trạng thái của các tab
     */
    private void resetTabs(TextView[] texts, LinearLayout[] layouts) {
        for (int i = 0; i < layouts.length; i++) {
            texts[i].setVisibility(View.GONE);
            layouts[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    /**
     * Hiệu ứng khi click vào tab
     */
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
