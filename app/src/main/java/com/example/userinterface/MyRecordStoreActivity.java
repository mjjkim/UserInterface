package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; // 반드시 androidx 버전을 임포트해야 합니다.


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyRecordStoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRecordStoreActivity extends AppCompatActivity {
    String title, author, description, publisher, pubDate, cover, isbn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRecordStoreBinding binding = ActivityMyRecordStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView bookCover = binding.recordStoreCover;
        TextView bookTitle = binding.recordStoreTitle;
        TextView bookAuthor = binding.recordStoreAuthor;
        TextView bookDescription = binding.recordStoreDescription;
        TextView bookPublisher = binding.recordStorePublisher;
        TextView bookpubDate = binding.recordStorePubDate;

        // 데이터 수신
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        description = getIntent().getStringExtra("description");
        publisher = getIntent().getStringExtra("publisher");
        pubDate = getIntent().getStringExtra("pubDate");
        cover = getIntent().getStringExtra("cover");
        isbn = getIntent().getStringExtra("isbn");

        // 정보 띄우기
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);
        bookPublisher.setText(publisher);
        bookpubDate.setText(pubDate);

        // 데이터 송신 게시판으로 데이터 보내기
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("author", author);
        resultIntent.putExtra("description", "description");
        resultIntent.putExtra("cover", cover);
        resultIntent.putExtra("publisher", publisher);
        resultIntent.putExtra("pubDate", pubDate);
        resultIntent.putExtra("isbn", isbn);


        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(bookCover);



        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // 제목 설정 (옵션)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.a);
        }
    }

    // 뒤로가기 버튼 동작 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 현재 Activity 종료
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            // 저장 버튼 클릭 시 동작
            Toast.makeText(this, "저장 버튼이 눌렸습니다!", Toast.LENGTH_SHORT).show();


            // 커스텀 동작 추가
            Intent intent = new Intent()
                    .putExtra("title", title)
                    .putExtra("author", author)
                    .putExtra("description", description)
                    .putExtra("publisher", publisher)
                    .putExtra("cover", cover)
                    .putExtra("pubDate", pubDate);
            setResult(RESULT_OK, intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbarmenu, menu); // 메뉴 리소스 연결
        return true;
    }
}