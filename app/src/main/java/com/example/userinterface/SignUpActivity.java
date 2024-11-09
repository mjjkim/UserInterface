package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private Button back;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 회원 가입 버튼 구현 중
        Button enrollBack = findViewById(R.id.enrollback);
        enrollBack.setOnClickListener(v -> {
            EditText emailLogin = findViewById(R.id.email);
            EditText passwordLogin = findViewById(R.id.password);
            EditText checkpasswordLogin = findViewById(R.id.checkpassword);

            String email = emailLogin.getText().toString();
            String password = passwordLogin.getText().toString();
            String checkpassword = checkpasswordLogin.getText().toString();
        });

    }

    // 현재 로그인되어 있는지 확인
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            reload();
        }
    }
}