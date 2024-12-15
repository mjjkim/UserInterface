package com.example.userinterface;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    private void loadRecordFromFirestore() {
        if (userId == null || documentId == null) {
            Toast.makeText(this, "사용자 정보나 문서 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("books")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.modifyTitle.setText(documentSnapshot.getString("title"));
                        binding.modifyAuthor.setText(documentSnapshot.getString("author"));
                        binding.modifyPublisher.setText(documentSnapshot.getString("publisher"));
                        binding.modifyPubDate.setText(documentSnapshot.getString("pubDate"));
                        binding.modifyDescription.setText(documentSnapshot.getString("description"));
                        binding.goodPhrase.setText(documentSnapshot.getString("phrase")); // 인상깊은 구절
                        binding.feel.setText(documentSnapshot.getString("feel"));         // 느낀점
                        binding.reason.setText(documentSnapshot.getString("reason"));     // 읽은 이유
                        binding.gital.setText(documentSnapshot.getString("gital"));       // 기타
                        binding.dateEditText.setText(documentSnapshot.getString("startDate")); // 시작 날짜
                        binding.dateEditText2.setText(documentSnapshot.getString("endDate"));  // 끝 날짜
                        Double rating = documentSnapshot.getDouble("rating");
                        if (rating != null) {
                            binding.ratingBar.setRating(rating.floatValue()); // 별점
                        }
                    } else {
                        Toast.makeText(this, "저장된 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveRecordToFirestore() {
        if (userId == null || documentId == null) {
            Toast.makeText(this, "사용자 정보나 문서 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // cover 값 가져오기 (메서드 외부에서 선언)
        final String cover = getIntent().getStringExtra("cover") != null ? getIntent().getStringExtra("cover") : "";

        // EditText 값 가져오기
        String goodPhrase = binding.goodPhrase.getText().toString();
        String feel = binding.feel.getText().toString();
        String reason = binding.reason.getText().toString();
        String gital = binding.gital.getText().toString();
        float rating = binding.ratingBar.getRating();
        String startDate = binding.dateEditText.getText().toString();
        String endDate = binding.dateEditText2.getText().toString();

        // 저장할 데이터 맵 생성
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("title", documentId);
        recordData.put("author", binding.modifyAuthor.getText().toString());
        recordData.put("phrase", goodPhrase);
        recordData.put("feel", feel);
        recordData.put("reason", reason);
        recordData.put("gital", gital);
        recordData.put("rating", rating);
        recordData.put("startDate", startDate);
        recordData.put("endDate", endDate);
        recordData.put("cover", cover); // 커버 이미지 유지

        // Firestore에 데이터 저장
        db.collection("users")
                .document(userId)
                .collection("books")
                .document(documentId)
                .set(recordData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                    saveToPhrases(goodPhrase, feel, cover); // 글귀 모음에 동시 저장
                    setResult(RESULT_OK); // 결과 코드 설정
                    finish(); // 저장 후 액티비티 종료
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveToPhrases(String goodPhrase, String feel, String cover) {
        if (goodPhrase.isEmpty() || feel.isEmpty()) return;

        Map<String, Object> phraseData = new HashMap<>();
        phraseData.put("title", documentId);
        phraseData.put("author", binding.modifyAuthor.getText().toString());
        phraseData.put("phrase", goodPhrase);
        phraseData.put("feel", feel);
        phraseData.put("cover", cover);

        db.collection("users")
                .document(userId)
                .collection("phrases")
                .document(documentId)
                .set(phraseData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "글귀 모음에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "글귀 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reviewappbarmenu, menu);
        return true;
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
        } else if (item.getItemId() == R.id.remove_action) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("정말로 삭제하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("취소", null)
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
