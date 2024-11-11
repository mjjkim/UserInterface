package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 회원 가입 클릭시 회원가입 창으로 이동
        binding.tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity (new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        // 로그인 클릭 시 메인 창으로 이동
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity (new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
}