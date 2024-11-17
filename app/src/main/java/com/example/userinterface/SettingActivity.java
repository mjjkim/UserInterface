package com.example.userinterface;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    // 닉네임 변경, 비밀번호 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 뒤로 가기 버튼 클릭
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == binding.backButton){
                    finish();
                }
            }
        });

        // 알람 설정 버튼 클릭
        binding.alramSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 닉네임 변경 클릭
        binding.nicknameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 비밀 번호 변경 클릭
        binding.passwordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 프로필 변경 클릭
        binding.profileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }
}