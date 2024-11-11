package com.example.userinterface;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.userinterface.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigator = binding.bottomNavigator;

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
                } else if (itemId == R.id.nav_mypage) {
                    selectedFragment = new MypageFragment();
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
}