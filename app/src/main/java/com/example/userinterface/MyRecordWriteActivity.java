package com.example.userinterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivityMyRecordWriteBinding;

public class MyRecordWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRecordWriteBinding binding = ActivityMyRecordWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}