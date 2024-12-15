package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMessageBoardWriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MessageBoardWriteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText reviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoardWriteBinding binding = ActivityMessageBoardWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView bookCover = binding.recordCover;
        TextView bookTitle = binding.recordTitle;
        TextView bookAuthor = binding.recordAuthor;
        TextView bookDescription = binding.recordDescription;

        // Firebase 설정
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String uid = currentUser != null ? currentUser.getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 책 데이터 수신
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
        String publisher = getIntent().getStringExtra("publisher");
        String pubDate = getIntent().getStringExtra("pubDate");
        String cover = getIntent().getStringExtra("cover");

        // 화면 데이터 설정
        binding.recordTitle.setText(title);
        binding.recordAuthor.setText(author);
        binding.recordDescription.setText(description);

        // Glide로 이미지 로드
        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(bookCover);


        db.collection("users").document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("UInterface", "Firestore 데이터 가져오기 실패 : " + error.getMessage());
                        Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        binding.messageBoardWriteNickname.setText(nickname != null ? nickname + "의 게시글" : "닉네임의 게시글");
                    }
                });

        // 데이터 화면에 표시
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);

        // 게시글 추가 버튼 클릭 리스너
        String finalUid = uid;

        binding.recordAddButton.setOnClickListener(view -> {
            String review = binding.etReview.getText().toString();

            if (review.isEmpty()) {
                Toast.makeText(this, "리뷰를 작성해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 고유 postId 생성
            String postId = db.collection("message_boards").document().getId();

            // Firestore에 데이터 추가
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("postId", postId);
            newItem.put("title", title);
            newItem.put("author", author);
            newItem.put("description", description);
            newItem.put("cover", cover);
            newItem.put("review", review);
            newItem.put("userId", uid);
            newItem.put("liked", false);
            newItem.put("timestamp", com.google.firebase.Timestamp.now());

            db.collection("message_boards").document(postId)
                    .set(newItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "게시글이 추가되었습니다.", Toast.LENGTH_SHORT).show();

                        // 메시지보드로 이동
                        Intent intent = new Intent(this, MessageBoradActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UInterface", "게시글 추가 실패: " + e.getMessage());
                        Toast.makeText(this, "게시글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        });


        // 뒤로 가기 동작 설정
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MessageBoardWriteActivity.this, MyRecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        reviewText = binding.etReview;
    }

    // Firestore posts 업데이트 함수
    private void updatePosts(String uid, Map<String, Object> newItem) {
        db.collection("message_boards").document(uid)
                .update("posts", FieldValue.arrayUnion(newItem))
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "게시글 추가 성공");
                    Toast.makeText(this, "게시글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "게시글 업데이트 실패: " + e.getMessage());
                    Toast.makeText(this, "게시글 추가 실패", Toast.LENGTH_SHORT).show();
                });
    }
}