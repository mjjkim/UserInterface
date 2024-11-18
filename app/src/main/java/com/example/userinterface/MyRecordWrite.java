package com.example.userinterface;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityMyRecordBinding;
import com.example.userinterface.databinding.ActivityMyRecordWriteBinding;

public class MyRecordWrite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRecordWriteBinding binding = ActivityMyRecordWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}