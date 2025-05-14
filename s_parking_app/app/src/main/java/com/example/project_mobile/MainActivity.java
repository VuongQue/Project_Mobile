package com.example.project_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.project_mobile.adapter.SwitchFragmentAdapter;
import com.example.project_mobile.databinding.ActivityMainBinding;
import com.example.project_mobile.databinding.BottomNavBinding;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.SetUp;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BottomNavBinding bottomNavBinding;
    private SwitchFragmentAdapter switchFragmentAdapter;
    private PreferenceManager preferenceManager;
    static public SetUp setUp;
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
        preferenceManager = new PreferenceManager(this);

        setUp = new SetUp(this);

        // Load ngôn ngữ đã lưu từ SharedPreferences
        setUp.loadLocale();
        setUp.loadTheme();

        FragmentManager fragmentManager = getSupportFragmentManager();
        switchFragmentAdapter = new SwitchFragmentAdapter(fragmentManager, getLifecycle());
        binding.viewPager2.setAdapter(switchFragmentAdapter);
        // Lấy tab đã chọn trước khi recreate
        SharedPreferences tabPref = getSharedPreferences("tabState", MODE_PRIVATE);
        selectedTab = tabPref.getInt("currentTab", 1); // mặc định là tab 1 (Home)
        binding.viewPager2.setCurrentItem(selectedTab - 1, false); // ViewPager index từ 0


        BottomNavigationBarProcess();
    }
    // Hàm thay đổi ngôn ngữ


    private void BottomNavigationBarProcess() {
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