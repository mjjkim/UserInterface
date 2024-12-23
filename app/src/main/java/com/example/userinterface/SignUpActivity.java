package com.example.userinterface;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton back;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isEmailAvailable = false; // 이메일 중복 여부 저장
    private boolean isEmailDuplicateCheck = false; // 중복 확인 완료 여부
    private boolean isNicknameAvailable = false;
    private boolean isNicknameDuplicateCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Firestore 인스턴스 초기화
        db = FirebaseFirestore.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 이메일 중복 확인 버튼
        binding.duplicateEmailCheck.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim().toLowerCase();

            if (email.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 이메일 중복 확인 플래그 초기화
            isEmailAvailable = false;
            isEmailDuplicateCheck = false;

            // 이메일 형식 확인
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUpActivity.this, "유효한 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // 이메일이 이미 존재하는 경우
                                isEmailAvailable = false;
                                Toast.makeText(SignUpActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                                binding.enrollback.setEnabled(false); // 이미 사용 중인 이메일이라면 회원가입 버튼 비활성화
                            } else {
                                // 사용 가능한 이메일인 경우
                                isEmailAvailable = true;
                                Toast.makeText(SignUpActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                                binding.enrollback.setEnabled(true); // 이메일 중복 확인 후 회원가입 버튼 활성화
                            }
                        } else {
                            // Firestore 오류 처리
                            isEmailAvailable = false;
                            Toast.makeText(SignUpActivity.this, "이메일 중복 확인 실패", Toast.LENGTH_SHORT).show();
                        }
                        isEmailDuplicateCheck = true;  // 중복 확인 완료
                    });
        });

        // 닉네임 중복 확인 버튼
        binding.duplicateNickNameCheck.setOnClickListener(view -> {
            String nickname = binding.nickname.getText().toString().trim().toLowerCase();

            if (nickname.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "닉네임이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            isNicknameAvailable = false;
            isNicknameDuplicateCheck = false;

            db.collection("users")
                    .whereEqualTo("nickname", nickname.toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                //닉네임이 이미 존재하는 경우
                                isNicknameAvailable = false;
                                Toast.makeText(SignUpActivity.this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                binding.enrollback.setEnabled(false);
                            }
                            else {
                                isNicknameAvailable = true;
                                Toast.makeText(SignUpActivity.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                binding.enrollback.setEnabled(true);
                            }
                        }
                        else {
                            isNicknameAvailable = false;
                            Toast.makeText(SignUpActivity.this, "닉네임 중복 확인 실패", Toast.LENGTH_SHORT).show();
                        }
                        isNicknameDuplicateCheck = true;
                    });

        });

        // 회원 가입 버튼
        binding.enrollback.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String nickname = binding.nickname.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            String checkpassword = binding.checkpassword.getText().toString().trim();

            if(!isEmailDuplicateCheck) {
                Toast.makeText(SignUpActivity.this, "이메일 중복 확인을 먼저 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isEmailAvailable) {
                Toast.makeText(SignUpActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isNicknameDuplicateCheck) {
                Toast.makeText(SignUpActivity.this, "닉네임 중복 확인을 먼저 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidNickname(nickname)) {
                Toast.makeText(this, "닉네임은 2자 이상 20자 이하 입니다. 특수 문자는 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isValidPassword(password)) {
                Toast.makeText(this, "비밀번호는 8자 이상, 대문자, 소문자, 숫자, 특수 문자를 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(checkpassword)) {
                // 비밀번호와 비밀번호 확인히 일치하지 않는 경우
                Toast.makeText(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                // 비밀번호 입력란으로 포커스 이동
                binding.password.requestFocus();
                return;
            }

            // Firebase Auth로 회원 가입 처리
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 회원가입 성공
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userEmail = user.getEmail();
                                    String userNickname = nickname.toLowerCase();
                                    // Firestore에 사용자 정보 저장
                                    db.collection("users").document(user.getUid())
                                            .set(new HashMap<String, Object>() {{
                                                put("email", userEmail);
                                                put("nickname", userNickname);
                                            }})
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("UserInterface", "회원 가입 성공");
                                                Toast.makeText(SignUpActivity.this, "회원 가입 성공!", Toast.LENGTH_SHORT).show();
                                                // 로그인 화면으로 이동
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("UserInterface", "회원 정보 저장 실패", e);
                                                Toast.makeText(SignUpActivity.this, "회원 정보 저장 실패", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                // 회원가입 실패
                                Log.w("UserInterface", "회원 가입 실패", task.getException());
                                Toast.makeText(SignUpActivity.this, "회원 가입 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private boolean isValidNickname(String nickname) {
        if (nickname.length() >= 2 && nickname.length() <= 20) {
            return true;
        }
        if (!nickname.matches("[a-zA-Z0-9가-힣]+")) {
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            // 8자 이상
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            // 대문자 포함
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            // 소문자 포함
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            // 숫자 포함
            return false;
        }
        if (!password.matches(".*[!@#$%^&*+=?-].*")) {
            // 특수 문자 포함
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, LoginActivity.class);
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
