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

public class MainActivity extends AppCompatActivity {

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

        SwitchActivity();

        BottomNavigationBarProcess();
    }

    private void SwitchActivity() {
        final LinearLayout payment = findViewById(R.id.payment);
        final LinearLayout booking = findViewById(R.id.booking);

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ParkingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void BottomNavigationBarProcess() {
        final LinearLayout homeLayout = findViewById(R.id.homeLayout);
        final LinearLayout stateLayout = findViewById(R.id.stateLayout);
        final LinearLayout profileLayout = findViewById(R.id.profileLayout);
        final LinearLayout settingsLayout = findViewById(R.id.settingsLayout);

        final ImageView homeImg = findViewById(R.id.homeImg);
        final ImageView stateImg = findViewById(R.id.stateImg);
        final ImageView profileImg = findViewById(R.id.profileImg);
        final ImageView settingsImg = findViewById(R.id.settingsImg);

        final TextView homeTxt = findViewById(R.id.homeTxt);
        final TextView stateTxt = findViewById(R.id.stateTxt);
        final TextView profileTxt = findViewById(R.id.profileTxt);
        final TextView settingsTxt = findViewById(R.id.settingsTxt);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 1) {
                    stateTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);
                    settingsTxt.setVisibility(View.GONE);

                    stateLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    homeTxt.setVisibility(View.VISIBLE);
                    homeLayout.setBackgroundResource(R.drawable.selected_icon);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    homeLayout.startAnimation(scaleAnimation);

                    selectedTab = 1;
                }
            }
        });

        stateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 2) {
                    homeTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);
                    settingsTxt.setVisibility(View.GONE);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    stateTxt.setVisibility(View.VISIBLE);
                    stateLayout.setBackgroundResource(R.drawable.selected_icon);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    stateLayout.startAnimation(scaleAnimation);

                    selectedTab = 2;
                }
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 3) {
                    stateTxt.setVisibility(View.GONE);
                    homeTxt.setVisibility(View.GONE);
                    settingsTxt.setVisibility(View.GONE);

                    stateLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    profileTxt.setVisibility(View.VISIBLE);
                    profileLayout.setBackgroundResource(R.drawable.selected_icon);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    profileLayout.startAnimation(scaleAnimation);

                    selectedTab = 3;
                }
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 4) {
                    stateTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);
                    homeTxt.setVisibility(View.GONE);

                    stateLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    settingsTxt.setVisibility(View.VISIBLE);
                    settingsLayout.setBackgroundResource(R.drawable.selected_icon);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    settingsLayout.startAnimation(scaleAnimation);

                    selectedTab = 4;
                }
            }
        });
    }
}
