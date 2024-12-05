package com.example.userinterface;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    }
}