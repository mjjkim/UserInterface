package com.example.userinterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.userinterface.databinding.ActivitySettingBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class SettingActivity extends AppCompatActivity {
    private Uri selectedImageUri;
    private ActivitySettingBinding binding;
    // userId 클래스 멤버 변수로 선언
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;


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
                openGallery();
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
                startActivity(new Intent(SettingActivity.this, AlarmActivity.class));
            }
        });

        // 비밀 번호 변경 클릭
        binding.passwordChange.setOnClickListener(view -> {
            // 현재 비밀번호 EditText
            final EditText currentPassword = new EditText(this);
            currentPassword.setHint("현재 비밀번호를 입력하세요");
            currentPassword.setPadding(20,20,20,20);
            currentPassword.setBackground(null);

            // 비밀번호 EditText
            final EditText inputPassword = new EditText(this);
            inputPassword.setHint("새 비밀번호를 입력하세요(8자 이상, 대문자, 소문자, 숫자, 특수 문자를 포함 필요)");
            inputPassword.setPadding(20,20,20,20);
            inputPassword.setBackground(null);

            // 비밀번호 확인 EditText
            final EditText checkPassword = new EditText(this);
            checkPassword.setHint("비밀번호 확인");
            checkPassword.setPadding(20,20,20,20);
            checkPassword.setBackground(null);

            // LinearLayout으로 EditText 두 개 다이얼로그에 배치
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20,20,20,20);
            layout.addView(currentPassword);
            layout.addView(inputPassword);
            layout.addView(checkPassword);

            AlertDialog passwordChangeDialog = new AlertDialog.Builder(this)
                    .setTitle("비밀번호 변경")
                    .setView(layout)
                    .setNegativeButton("취소", null)
                    .create();

            // 다이얼로그 생성 후 확인 버튼 리스너 수동 설정
            passwordChangeDialog.setButton(AlertDialog.BUTTON_POSITIVE, "확인", (dialog, which) -> {
            });

            passwordChangeDialog.show();

            // 버튼 리스너 설정
            passwordChangeDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String forCheckCurrentPassword = currentPassword.getText().toString().trim();
                String newPassword = inputPassword.getText().toString().trim();
                String newCheckPassword = checkPassword.getText().toString().trim();

                if (forCheckCurrentPassword.isEmpty()) {
                    Toast.makeText(this, "현재 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword.isEmpty()) {
                    Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if (newCheckPassword.isEmpty()) {
                    Toast.makeText(this, "비밀번호 확인을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword.equals(forCheckCurrentPassword)) {
                    Toast.makeText(this, "현재 비밀번호와 새 비밀번호가 동일합니다", Toast.LENGTH_SHORT).show();
                }
                else if (!newPassword.equals(newCheckPassword)) {
                    Toast.makeText(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (newPassword.length() < 8) {
                    Toast.makeText(this, "비밀번호는 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.matches(".*[A-Z].*")) {
                    Toast.makeText(this, "비밀번호는 대문자를 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.matches(".*[a-z].*")) {
                    Toast.makeText(this, "비밀번호는 소문자를 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.matches(".*\\d.*")) {
                    Toast.makeText(this, "비밀번호는 숫자를 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.matches(".*[!@#$%^&*+=?-].*")) {
                    Toast.makeText(this, "비밀번호는 특수 문자를 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 비밀번호 변경
                    reauthenticateAndChangePassword(forCheckCurrentPassword, newPassword, passwordChangeDialog);
                }
            });
        });

    }
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                // 이미 권한이 허용된 경우
                Toast.makeText(this, "알림 권한이 이미 허용되어 있습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Android 13 미만 버전에서는 별도의 권한 요청 없이 알림 사용 가능
            Toast.makeText(this, "이 기기에서는 별도의 알림 권한이 필요하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    // 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // URI 읽기 권한 추가
        galleryLauncher.launch(galleryIntent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri);
                    }
                    else {
                        Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // Firebase Storage에 이미지 업로드
    private void uploadImageToFirebase(Uri imageUri) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null. User is not authenticated.");
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Log.e("UInterface", "Image URI is null.");
            Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("profile_images/" + userId + ".jpg");

        try {
            InputStream stream = getContentResolver().openInputStream(imageUri);
            if (stream == null) {
                Log.e("UInterface", "Stream is null.");
                Toast.makeText(this, "이미지를 처리할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            storageReference.putStream(stream)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("UInterface", "Image upload succeeded.");
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("UInterface", "Download URL: " + uri);
                            updateFirestore("profileImage", uri.toString());
                        }).addOnFailureListener(e -> {
                            Log.e("UInterface", "Failed to retrieve download URL: " + e.getMessage());
                            Toast.makeText(this, "URL 가져오기 실패", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UInterface", "Upload failed: " + e.getMessage());
                        if (e instanceof StorageException) {
                            StorageException se = (StorageException) e;
                            Log.e("UInterface", "Error code: " + se.getErrorCode());
                        }
                        Toast.makeText(this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        try {
                            stream.close(); // 스트림 닫기
                        } catch (IOException e) {
                            Log.e("UInterface", "Failed to close stream: " + e.getMessage());
                        }
                    });
        } catch (FileNotFoundException e) {
            Log.e("UInterface", "File not found: " + e.getMessage());
            Toast.makeText(this, "파일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFirestore(String field, String value) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null. Cannot update Firestore.");
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "Firestore updated successfully for field: " + field);
                    Toast.makeText(this, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Failed to update Firestore: " + e.getMessage());
                    Toast.makeText(this, "Firestore 업데이트 실패.", Toast.LENGTH_SHORT).show();
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

    private void reauthenticateAndChangePassword(String currentPassword, String newPassword, AlertDialog dialog) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 사용자의 이메일과 현재 비밀번호로 인증
        String email = mAuth.getCurrentUser().getEmail();
        if (email == null) {
            Toast.makeText(this, "사용자 이메일 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnSuccessListener(avoid1 -> {
                    // 인증 성공 후 비밀번호 변경
                    mAuth.getCurrentUser().updatePassword(newPassword)
                                    .addOnSuccessListener(avoid2 -> {
                                        Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                        // 성공시 dialog 닫기
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "비밀번호 변경에 실패했습니다 : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "현재 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                });
    }

}