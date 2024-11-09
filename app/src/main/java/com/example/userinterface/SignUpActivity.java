package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignUpActivity extends AppCompatActivity {

    private Button back;
    private FirebaseAuth mAuth;
    private boolean isEmailAvailable = false; // 이메일 중복 여부 저장
    private boolean isDuplicateCheckDone = false; // 중복 확인 완료 여부

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

        // 중복 확인 버튼 기능 추가
        binding.duplicateCheck.setOnClickListener(v -> {
            EditText emailLogin = findViewById(R.id.email);
            String email = emailLogin.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase를 사용하여 이메일 중복 여부 확인
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task ->  {
                isDuplicateCheckDone = true; // 중복 확인 완료 표시

                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().isEmpty()) {
                        // 이메일이 이미 사용 중일 때
                        isEmailAvailable = false;
                        isDuplicateCheckDone = true;
                        Toast.makeText(SignUpActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                        emailLogin.requestFocus();
                    }
                    else {
                        // 이메일이 이미 사용 가능할 때
                        isEmailAvailable = true;
                        isDuplicateCheckDone = true;
                        Toast.makeText(SignUpActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // 중복 확인 실패
                    isEmailAvailable = false;
                    isDuplicateCheckDone = false;
                    Toast.makeText(SignUpActivity.this, "중복 확인에 실패했습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            });
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

            if(!isDuplicateCheckDone) {
                Toast.makeText(SignUpActivity.this, "이메일 중복 확인을 먼저 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isEmailAvailable) {
                Toast.makeText(SignUpActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
            }

            if(!password.equals(checkpassword)) {
                // 비밀번호와 비밀번호 확인히 일치하지 않는 경우
                Toast.makeText(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                // 비밀번호 입력란으로 포커스 이동
                passwordLogin.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("UI", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                            else {
                                // 회원 가입 실패, 중복된 이메일 확인
                                if (task.getException() != null && task.getException().getMessage().contains("email address is already in use")) {
                                    Toast.makeText(SignUpActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                                    emailLogin.requestFocus();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("UI", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "회원 가입 실패;", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // 현재 로그인되어 있는지 확인
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {
    }
}