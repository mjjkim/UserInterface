package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageBoardMyActivity extends AppCompatActivity {

    private PostAdapter adapter;
    private List<PostItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_my);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 초기화
        items = new ArrayList<>();
        adapter = new PostAdapter(items);
        recyclerView.setAdapter(adapter);

        // 뒤로 가기 버튼 설정
        ImageView back_button = findViewById(R.id.backButton);
        back_button.setOnClickListener(view -> {
            finish();
        });

        // Firebase 설정
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 내가 쓴 글 가져오기
        db.collection("message_boards").document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("UInterface", "Firestore 데이터 가져오기 실패 : " + error.getMessage());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        items.clear();
                        List<Map<String, Object>> posts = (List<Map<String, Object>>) documentSnapshot.get("posts");
                        Log.d("UInterface", "Fetched posts : " + posts);
                        if (posts != null) {
                            for (Map<String, Object> post : posts) {
                                String title = (String) post.get("title");
                                String content = (String) post.get("review");
                                String postUserId = (String) post.get("userId");
                                // 책 이미지 URL
                                String cover = (String) post.get("cover");
                                // 지은이
                                String author = (String) post.get("author");

                                Log.d("UInterface", "Post : " + post);
                                Log.d("UInterface", "UserID : " + userId + ", PostUserID : " + postUserId);

                                // 현재 사용자 ID와 작성자 ID가 일치하는 경우만 추가
                                if (userId.equals(postUserId)) {
                                    items.add(new PostItem(title, content, postUserId, cover, author));
                                }
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("UserInterface", "Adapter item count : " + items.size());
                        }
                    }
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
            holder.authorText.setText(item.getAuthor());

            // Glide로 이미지 로드
            Glide.with(holder.bookImage.getContext())
                    .load(item.getCover())
                    // 기본 이미지
                    .error(R.drawable.error)
                    // 오류 이미지
                    .into(holder.bookImage);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        // ViewHolder 클래스
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleText, contentText, authorText;
            ImageView bookImage, likeButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.titleText);
                contentText = itemView.findViewById(R.id.contentText);
                bookImage = itemView.findViewById(R.id.book_image);
                likeButton = itemView.findViewById(R.id.like_button);
                authorText = itemView.findViewById(R.id.authorText);
            }
        }
    }

    private class PostItem {
        private final String title;
        private final String content;
        private final String userId;
        private final String cover;
        private final String author;

        public PostItem(String title, String content, String userId, String cover, String author) {
            this.title = title;
            this.content = content;
            this.userId = userId;
            this.cover = cover;
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getUserId() {
            return userId;
        }

        public String getCover() {
            return cover;
        }

        public String getAuthor() {
            return author;
        }
    }

}