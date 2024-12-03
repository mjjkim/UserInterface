package com.example.userinterface;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userinterface.databinding.ActivityMessageBoardReviewBinding;

import org.w3c.dom.Text;

public class MessageBoardReview extends AppCompatActivity {
    ImageView reviewImage;
    TextView reviewTitle;
    TextView reviewAuthor;
    TextView reviewDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoardReviewBinding binding = ActivityMessageBoardReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reviewImage = binding.reviewCover;
        reviewTitle = binding.reviewTitle;
        reviewAuthor = binding.reviewAuthor;
        reviewDescription = binding.reviewDescription;

        //데이터 수신

    }
}