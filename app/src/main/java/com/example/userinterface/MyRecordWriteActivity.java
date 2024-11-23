package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyRecordWriteBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker.Builder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyRecordWriteActivity extends AppCompatActivity {

    //리뷰 edittext
    EditText reviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRecordWriteBinding binding = ActivityMyRecordWriteBinding.inflate(getLayoutInflater());
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


        binding.recordAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultIntent.putExtra("review", binding.etReview.getText().toString());
                setResult(RESULT_OK, resultIntent);
                Log.d("omj", "record to board");
                finish();
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