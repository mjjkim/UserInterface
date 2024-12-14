package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyPhraseModifyBinding;

public class MyPhraseModifyActivity extends AppCompatActivity {

    TextView bookTitle;
    TextView bookAuthor;
    ImageView bookCover;

    EditText bookPhrase;
    EditText bookFeel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyPhraseModifyBinding binding = ActivityMyPhraseModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //백버튼
        binding.phraseModifyBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //데이터 수신
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String cover = getIntent().getStringExtra("cover");
        String phrase = getIntent().getStringExtra("phrase");
        String feel = getIntent().getStringExtra("feel");

        //초기화
        bookTitle = binding.phraseModifyTitle;
        bookAuthor = binding.phraseModifyAuthor;
        bookCover = binding.phraseModifyImage;
        bookPhrase = binding.quoteModifyInput;
        bookFeel = binding.memoModifyInput;

        bookTitle.setText(title);
        bookAuthor.setText(author);
        Glide.with(this)
                .load(cover)
                .into(bookCover);

        bookPhrase.setText(phrase);
        bookFeel.setText(feel);

        // 수정된 데이터를 저장
        Button saveButton = binding.saveModifyButton;
        saveButton.setOnClickListener(view -> {
            String updatedPhrase = bookPhrase.getText().toString();
            String updatedFeel = bookFeel.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("author", author);
            resultIntent.putExtra("cover", cover);
            resultIntent.putExtra("phrase", updatedPhrase);
            resultIntent.putExtra("feel", updatedFeel);

            setResult(RESULT_OK, resultIntent);
            finish();
        });


    }
}