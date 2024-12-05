package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyPhraseStoreBinding;

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("title", title)
                        .putExtra("cover", cover)
                        .putExtra("phrase", phrase.getText().toString())
                        .putExtra("feel", feel.getText().toString())
                        .putExtra("author", author);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}