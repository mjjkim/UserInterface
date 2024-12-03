package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyLike extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 준비
        List<BoardItem> items = new ArrayList<>();
        items.add(new BoardItem("책: 지웅이가 싫어요", "내용: 지웅이 싫다"));
        items.add(new BoardItem("책: 김철홍도 싫어요 ", "내용: 다른 책 내용을 여기에 표시"));

        // 어댑터 설정
        BoardAdapter adapter = new BoardAdapter(items);
        recyclerView.setAdapter(adapter);

        // 뒤로가기 버튼 동작
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
    }

    // 내부 클래스: RecyclerView 어댑터
    class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
        private final List<BoardItem> itemList;

        public BoardAdapter(List<BoardItem> itemList) {
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // item_board 레이아웃 파일 inflate
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // 데이터 바인딩
            BoardItem item = itemList.get(position);
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
    public class BoardItem {
        private final String title;
        private final String content;

        public BoardItem(String title, String content) {
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