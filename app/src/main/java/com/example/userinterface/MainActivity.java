package com.example.userinterface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

import java.util.Calendar;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase 초기화
        FirebaseApp.initializeApp(this);

        BottomNavigationView bottomNavigator = binding.bottomNavigator;

        // Intent를 통해 Fragment를 결정
        String showFragment = getIntent().getStringExtra("showFragment");

        if (showFragment != null && showFragment.equals("MenuFragment")) {
            // MenuFragment 표시 및 메뉴 아이템 선택
            bottomNavigator.setSelectedItemId(R.id.nav_menu);
            loadFragment(new MenuFragment());
        } else {
            // 기본 화면: HomeFragment
            bottomNavigator.setSelectedItemId(R.id.nav_home);
            loadFragment(new HomeFragment());
        }


        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new HomeFragment()).commit();

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigator.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_menu) {
                    selectedFragment = new MenuFragment();
                } else if (itemId == R.id.nav_chanllenge) {
                    selectedFragment = new ChallengeFragment();
                } else {
                    selectedFragment = new HomeFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, selectedFragment)
                        .commit();
                return true;
            }
        });

    }
    // Fragment 교체 메서드
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }
}