package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMessageBoardWriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MessageBoardWriteActivity extends AppCompatActivity {

    //리뷰 edittext
    EditText reviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoardWriteBinding binding = ActivityMessageBoardWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView bookCover = binding.recordCover;
        TextView bookTitle = binding.recordTitle;
        TextView bookAuthor = binding.recordAuthor;
        TextView bookDescription = binding.recordDescription;

        // 데이터 수신
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
//        String publisher = getIntent().getStringExtra("publisher");
//        String pubDate = getIntent().getStringExtra("pubDate");
        String cover = getIntent().getStringExtra("cover");
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);

        // 데이터 송신 게시판으로 데이터 보내기
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("author", author);
        resultIntent.putExtra("description", "description");
        resultIntent.putExtra("cover", cover);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.recordAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getIntent().getStringExtra("title");
                String author = getIntent().getStringExtra("author");
                String description = getIntent().getStringExtra("description");
                String cover = getIntent().getStringExtra("cover");
                String review = binding.etReview.getText().toString();

                db.collection("message_boards").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (!documentSnapshot.exists() || !documentSnapshot.contains("posts")) {
                                db.collection("message_boards").document(userId)
                                        .set(new HashMap<String, Object>() {{
                                            put("posts", new ArrayList<>());
                                        }});
                            }
                        })
                        .addOnCompleteListener(task -> {
                            // Firestore에 데이터 추가
                            MessageBoardItem newItem = new MessageBoardItem(title, author, cover, true, review);
                            db.collection("message_boards").document(userId)
                                    .update("posts", FieldValue.arrayUnion(newItem))
                                    .addOnSuccessListener(avoid -> {
                                        Log.d("UInterface", "Upload Success");
                                        setResult(RESULT_OK);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UInterface", "Upload Failed : " + e.getMessage());
                                        Toast.makeText(MessageBoardWriteActivity.this, "게시글 추가 실패", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UInterface", "Upload Failed : " + e.getMessage());
                            Toast.makeText(MessageBoardWriteActivity.this, "게시글 추가 실패", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(bookCover);

        reviewText = binding.etReview;

    }
    private String formatDate(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return sdf.format(timestamp);
    }
}