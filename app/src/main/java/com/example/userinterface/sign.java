package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivitySignBinding;

public class sign extends AppCompatActivity {

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignBinding binding = ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.back;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 아이디, 중복 확인, 비밀번호, 비밀번호 확인 모두 다 입력될 경우에만 돌아가게 코딩 필요
        binding.enrollback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}