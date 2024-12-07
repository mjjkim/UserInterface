package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyPhraseStoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyPhraseStoreActivity extends AppCompatActivity {
    TextView bookTitle;
    ImageView bookCover;
    TextView bookAuthor;

    EditText phrase;
    EditText feel;

    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyPhraseStoreBinding binding = ActivityMyPhraseStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("title");
        String cover = getIntent().getStringExtra("cover");
        String author = getIntent().getStringExtra("author");

        bookTitle = binding.phraseTitle;
        bookCover = binding.phraseImage;
        phrase = binding.quoteInput;
        feel = binding.memoInput;
        saveButton = binding.saveQuoteButton;
        bookAuthor = binding.phraseAuthor;

        bookAuthor.setText(author);
        bookTitle.setText(title);
        Glide.with(this)
                .load(cover)
                .into(bookCover);

        saveButton.setOnClickListener(view -> savePhraseToFirestore(title, cover, author));
    }

    private void savePhraseToFirestore(String title, String cover, String author) {
        // Firestore 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 저장할 데이터 생성
        Map<String, Object> phraseData = new HashMap<>();
        phraseData.put("title", title);
        phraseData.put("cover", cover);
        phraseData.put("author", author);
        phraseData.put("phrase", phrase.getText().toString());
        phraseData.put("feel", feel.getText().toString());
        phraseData.put("timestamp", com.google.firebase.Timestamp.now());

        // Firestore의 사용자별 컬렉션에 데이터 저장
        db.collection("users")
                .document(userId)
                .collection("phrases")
                .add(phraseData) // 자동 문서 ID 생성
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MyPhraseStoreActivity.this, "글귀가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyPhraseStoreActivity.this, "저장에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}