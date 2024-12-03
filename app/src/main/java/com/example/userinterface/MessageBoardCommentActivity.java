package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_comment);


        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 준비
        List<CommentItem> items = new ArrayList<>();
        items.add(new CommentItem("책: 지웅이가 싫어요", "내용: 지웅이 싫다"));
        items.add(new CommentItem("책: 철홍씨도 미워요", "내용: 다른 책의 내용을 표시합니다"));

        // 어댑터 설정
        CommentAdapter adapter = new CommentAdapter(items);
        recyclerView.setAdapter(adapter);

        ImageView backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // 내부 클래스: RecyclerView 어댑터
    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        private final List<CommentItem> itemList;

        public CommentAdapter(List<CommentItem> itemList) {
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
            CommentItem item = itemList.get(position);
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

    public class CommentItem {
        private final String title;
        private final String content;

        public CommentItem(String title, String content) {
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

