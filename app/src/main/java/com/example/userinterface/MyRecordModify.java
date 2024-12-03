package com.example.userinterface;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityMyRecordModifyBinding;

import org.checkerframework.checker.units.qual.A;

public class MyRecordModify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRecordModifyBinding binding = ActivityMyRecordModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RatingBar ratingBar = binding.ratingBar;
        // 별점 변경 리스너 설정
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    Toast.makeText(getApplicationContext(), "별점: " + rating, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}