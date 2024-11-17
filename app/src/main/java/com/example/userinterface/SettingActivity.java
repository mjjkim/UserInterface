package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface.databinding.ActivitySettingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingActivity extends AppCompatActivity {
    // 비밀번호 변경
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

        // 프로필 변경 클릭
        binding.profileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 닉네임 변경 클릭
        binding.nicknameChange.setOnClickListener(view -> {
            // 다이얼로그에 사용할 EditText 생성
            final EditText inputNickname = new EditText(this);
            // 힌트 설정
            inputNickname.setHint("새 닉네임을 입력하세요");
            // 패딩 설정
            inputNickname.setPadding(20,20,20,20);
            // 배경 제거
            inputNickname.setBackground(null);

            // AlertDialog 빌더를 사용하여 다이얼로그 생성
            AlertDialog nicknameChangeDialog = new AlertDialog.Builder(this)
                    .setTitle("닉네임 변경")
                    // 다이얼로그에 EditText 설정
                    .setView(inputNickname)
                    .setPositiveButton("확인", (dialog, which) -> {
                        String newNickname = inputNickname.getText().toString().trim();

                        // 닉네임 유효성 검사
                        if (newNickname.isEmpty()) {
                            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if (newNickname.length() < 2 || newNickname.length() > 20) {
                            Toast.makeText(this, "닉네임은 2자 이상 20자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // 닉네임 업데이트
                            updateNickname(newNickname);
                        }
                    })
                    // 취소 버튼
                    .setNegativeButton("취소", null)
                    .create();

            nicknameChangeDialog.show();
        });

        // 알람 설정 버튼 클릭
        binding.alramSetting.setOnClickListener(new View.OnClickListener() {
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

    }

    private void updateNickname(String newNickname) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .update("nickname", newNickname)
                .addOnSuccessListener(avoid -> {
                    Toast.makeText(this, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show();
                    // 변경 완료 후 결과 전달
                    // 결과 코드를 설정
                    setResult(RESULT_OK);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "닉네임 변경에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

}