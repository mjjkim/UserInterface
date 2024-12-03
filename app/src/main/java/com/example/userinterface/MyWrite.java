package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyWrite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_write);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 준비
        List<PostItem> items = new ArrayList<>();
        items.add(new PostItem("책: 지웅이가 싫어요", "지웅이 싫다"));
        items.add(new PostItem("책: 다른 이야기", "다른 책 내용을 여기에 표시"));

        // 어댑터 설정
        PostAdapter adapter = new PostAdapter(items);
        recyclerView.setAdapter(adapter);

        ImageView back_button = findViewById(R.id.backButton);
        back_button.setOnClickListener(view -> {
            finish();
        });


    }

    // 내부 클래스: RecyclerView 어댑터
    class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private final List<PostItem> itemList;

        public PostAdapter(List<PostItem> itemList) {
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 기존 item_board 레이아웃 파일을 사용
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 데이터 바인딩
            PostItem item = itemList.get(position);
            holder.titleText.setText(item.getTitle());
            holder.contentText.setText(item.getContent());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        // ViewHolder 클래스
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleText, contentText;
            ImageView bookImage, likeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.titleText);
                contentText = itemView.findViewById(R.id.contentText);
                bookImage = itemView.findViewById(R.id.book_image);
                likeButton = itemView.findViewById(R.id.like_button);
            }
        }
    }

    private class PostItem {
        private final String title;
        private final String content;

        public PostItem(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

}