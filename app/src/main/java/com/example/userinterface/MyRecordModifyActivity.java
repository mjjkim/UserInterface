package com.example.userinterface;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyRecordModifyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyRecordModifyActivity extends AppCompatActivity {
    // View Binding
    private ActivityMyRecordModifyBinding binding;

    // Firestore
    private FirebaseFirestore db;
    private String userId;

    // 책 데이터
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRecordModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firestore 초기화
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // 데이터 수신
        documentId = getIntent().getStringExtra("title"); // 문서 ID로 책 제목 사용
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
        String publisher = getIntent().getStringExtra("publisher");
        String pubDate = getIntent().getStringExtra("pubDate");
        String cover = getIntent().getStringExtra("cover");

        // 데이터 바인딩
        binding.modifyAuthor.setText(author);
        binding.modifyTitle.setText(documentId);
        binding.modifyDescription.setText(description);
        binding.modifyPublisher.setText(publisher);
        binding.modifyPubDate.setText(pubDate);

        // 책 이미지 로드
        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(binding.modifyCover);

        // Firestore에서 데이터 불러오기
        loadRecordFromFirestore();

        // 별점 변경 리스너 설정
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                Toast.makeText(this, "별점: " + rating, Toast.LENGTH_SHORT).show();
            }
        });

        // Toolbar 설정
        Toolbar toolbar = binding.reviewToolBar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.a);
            getSupportActionBar().setTitle(""); // 툴바 제목 제거
        }

        // 책 정보/독서 기록 전환 버튼
        binding.bookInfoButton.setOnClickListener(view -> {
            binding.bookInfo.setVisibility(FrameLayout.VISIBLE);
            binding.bookRecord.setVisibility(FrameLayout.GONE);
        });

        binding.bookRecordButton.setOnClickListener(view -> {
            binding.bookInfo.setVisibility(FrameLayout.GONE);
            binding.bookRecord.setVisibility(FrameLayout.VISIBLE);
        });

        // 달력 팝업 설정
        setupDatePickers();
    }

    private void loadRecordFromFirestore() {
        if (userId == null || documentId == null) {
            Toast.makeText(this, "사용자 정보나 문서 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("user_records")
                .document(userId)
                .collection("books")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Firestore에서 데이터 읽어와서 UI에 반영
                        binding.modifyTitle.setText(documentSnapshot.getString("title"));
                        binding.modifyAuthor.setText(documentSnapshot.getString("author"));
                        binding.modifyPublisher.setText(documentSnapshot.getString("publisher"));
                        binding.modifyPubDate.setText(documentSnapshot.getString("pubDate"));
                        binding.modifyDescription.setText(documentSnapshot.getString("description"));
                        binding.goodPhrase.setText(documentSnapshot.getString("phrase"));
                        binding.feel.setText(documentSnapshot.getString("feel"));
                        binding.reason.setText(documentSnapshot.getString("reason"));
                        binding.gital.setText(documentSnapshot.getString("gital"));
                        binding.dateEditText.setText(documentSnapshot.getString("startDate"));
                        binding.dateEditText2.setText(documentSnapshot.getString("endDate"));
                        Double rating = documentSnapshot.getDouble("rating");
                        if (rating != null) {
                            binding.ratingBar.setRating(rating.floatValue());
                        }
                    } else {
                        Toast.makeText(this, "저장된 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupDatePickers() {
        binding.dateEditText.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setDatePickerListener(selectedDate -> binding.dateEditText.setText(selectedDate));
            datePickerFragment.show(getSupportFragmentManager(), "DATE_PICKER_START");
        });

        binding.dateEditText2.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setDatePickerListener(selectedDate -> binding.dateEditText2.setText(selectedDate));
            datePickerFragment.show(getSupportFragmentManager(), "DATE_PICKER_END");
        });
    }

    // Firestore에 데이터 저장
    private void saveRecordToFirestore() {
        if (userId == null || documentId == null) {
            Toast.makeText(this, "사용자 정보나 문서 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("title", binding.modifyTitle.getText().toString());
        recordData.put("author", binding.modifyAuthor.getText().toString());
        recordData.put("publisher", binding.modifyPublisher.getText().toString());
        recordData.put("pubDate", binding.modifyPubDate.getText().toString());
        recordData.put("description", binding.modifyDescription.getText().toString());
        recordData.put("phrase", binding.goodPhrase.getText().toString());
        recordData.put("feel", binding.feel.getText().toString());
        recordData.put("reason", binding.reason.getText().toString());
        recordData.put("gital", binding.gital.getText().toString());
        recordData.put("rating", binding.ratingBar.getRating());
        // 시작 날짜와 끝 날짜 추가
        recordData.put("startDate", binding.dateEditText.getText().toString());
        recordData.put("endDate", binding.dateEditText2.getText().toString());

        db.collection("user_records")
                .document(userId)
                .collection("books")
                .document(documentId)
                .set(recordData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Toolbar 메뉴 동작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.review_action) {
            saveRecordToFirestore();
            return true;
        } else if (item.getItemId() == R.id.cancel_action) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reviewappbarmenu, menu);
        return true;
    }
}
