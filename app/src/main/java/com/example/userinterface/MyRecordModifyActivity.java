package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    Button bookInfoButton;
    Button bookRecordButton;

    FrameLayout bookInfo;
    FrameLayout bookRecord;

    EditText dateEditText;
    EditText dateEditText2;
    EditText feel;
    EditText phrase;
    EditText reason;
    EditText gital;

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
        dateEditText = binding.dateEditText;
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
        dateEditText2 = binding.dateEditText2;
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


        bookInfoButton = binding.bookInfoButton;
        bookRecordButton = binding.bookRecordButton;

        bookInfo = binding.bookInfo;
        bookRecord = binding.bookRecord;

        bookInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookInfo.setVisibility(View.VISIBLE);
                bookRecord.setVisibility(View.GONE);
            }
        });
        bookRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookInfo.setVisibility(View.GONE);
                bookRecord.setVisibility(View.VISIBLE);
            }
        });

        // Toolbar 설정
        Toolbar toolbar = binding.reviewToolBar;

        setSupportActionBar(toolbar);

        // 제목 설정 (옵션)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.a);
        }

        phrase = binding.goodPhrase;
        feel = binding.feel;
        reason = binding.reason;
        gital = binding.gital;

    }
    // 뒤로가기 버튼 동작 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 현재 Activity 종료
            finish();
            return true;
        } else if (item.getItemId() == R.id.review_action) {
            // 저장 버튼 클릭 시 동작
            Toast.makeText(this, "저장 버튼이 눌렸습니다!", Toast.LENGTH_SHORT).show();

            finish();

            return true;
        } else if(item.getItemId() == R.id.cancel_action){
            // 현재 Activity 종료
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reviewappbarmenu, menu); // 메뉴 리소스 연결
        return true;
    }
}