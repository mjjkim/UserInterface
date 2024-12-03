package com.example.userinterface;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyRecordModifyBinding;

public class MyRecordModifyActivity extends AppCompatActivity {
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookPublihser;
    ImageView bookImage;
    TextView bookPubDate;
    TextView bookDescrition;
    TextView bookIsbn;

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

        // 책 데이터 수신
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
        String publisher = getIntent().getStringExtra("publisher");
        String pubDate = getIntent().getStringExtra("pubDate");
        String cover = getIntent().getStringExtra("cover");
        String isbn = getIntent().getStringExtra("isbn");

        bookAuthor = binding.modifyAuthor;
        bookTitle = binding.modifyTitle;
        bookDescrition = binding.modifyDescription;
        bookIsbn = binding.modifyISBN;
        bookImage = binding.modifyCover;
        bookPubDate = binding.modifyPubDate;
        bookPublihser = binding.modifyPublisher;

        bookAuthor.setText(author);
        bookTitle.setText(title);
        bookDescrition.setText(description);
        bookIsbn.setText(isbn);
        bookPublihser.setText(publisher);
        bookPubDate.setText(pubDate);

        Glide.with(this)
                .load(cover)
                .error(R.drawable.imagewait)
                .into(bookImage);
    }
}