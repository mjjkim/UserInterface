package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
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

        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(bookCover);


        // 날짜 선택 edit 텍스트
        EditText dateEditText = binding.dateEditText;
        // EditText 클릭 이벤트
        dateEditText.setOnClickListener(view -> {
            // DatePickerFragment 생성
            DatePickerFragment datePickerFragment = new DatePickerFragment();

            // 리스너 설정
            datePickerFragment.setDatePickerListener(selectedDate -> {
                // 선택한 날짜를 EditText에 설정
                dateEditText.setText(selectedDate);
            });

            // FragmentManager를 통해 팝업 띄우기
            datePickerFragment.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // 날짜 선택 edit 텍스트
        EditText dateEditText2 = binding.dateEditText2;
        // EditText 클릭 이벤트
        dateEditText2.setOnClickListener(view -> {
            // DatePickerFragment 생성
            DatePickerFragment datePickerFragment = new DatePickerFragment();

            // 리스너 설정
            datePickerFragment.setDatePickerListener(selectedDate -> {
                // 선택한 날짜를 EditText에 설정
                dateEditText2.setText(selectedDate);
            });

            // FragmentManager를 통해 팝업 띄우기
            datePickerFragment.show(getSupportFragmentManager(), "DATE_PICKER");
        });
    }
    private String formatDate(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        return sdf.format(timestamp);
    }
}