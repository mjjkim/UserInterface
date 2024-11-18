package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 전역 예외 처리기 설정
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log.e("UInterface", "App crashed: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) {
            Toast.makeText(this, "Firebase 인증 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        // 회원 가입 클릭시 회원가입 창으로 이동
        binding.signUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity (new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        // 로그인 클릭 시 메인 창으로 이동
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = binding.idLogin.getText().toString();
                String password = binding.passwordLogin.getText().toString();

                Log.d("UInterface", "ID: " + ID + ", Password: " + password);

                if (ID.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase 인증을 통한 로그인 처리
                mAuth.signInWithEmailAndPassword(ID, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 로그인 성공 시 메인 액티비티로 이동
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                                else {
                                    // 로그인 실패 시 에러 메시지 출력
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "알 수 없는 오류";
                                    Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    Log.e("UInterface", "Login failed: " + errorMessage);  // 로그에 실패 이유 출력
                                }
                            }
                        });
            }
        });
    }
}