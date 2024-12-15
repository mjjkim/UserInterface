package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageBoardLikeActivity extends AppCompatActivity {

    private List<BoardItem> items; // 데이터를 저장할 리스트
    private BoardAdapter adapter; // RecyclerView 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board_like);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 데이터 초기화 및 어댑터 설정
        items = new ArrayList<>();
        adapter = new BoardAdapter(items);
        recyclerView.setAdapter(adapter);

        // 뒤로가기 버튼 동작
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        // 스크랩된 게시물 로드
        loadLikedPosts();

        EditText search = findViewById(R.id.search_bar);
        //책 이름 검색 ㅇㅇ;
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    String query = search.getText().toString().trim();
                    filterBooks(query);
                    return true;
                }
                return false;
            }
        });

    }
    private void filterBooks(String query) {
        List<BoardItem> filteredList = new ArrayList<>();
        for (BoardItem book : items) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        adapter.updateList(filteredList);
    }

    // Firestore에서 스크랩된 게시물 로드
    private void loadLikedPosts() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 현재 사용자의 스크랩 상태 확인
        db.collection("user_likes").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> likes = documentSnapshot.getData();
                        if (likes != null) {
                            List<String> likedPostIds = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : likes.entrySet()) {
                                if ((Boolean) entry.getValue()) {
                                    likedPostIds.add(entry.getKey());
                                }
                            }

                            // 스크랩된 게시물 데이터 가져오기
                            fetchPostsByIds(likedPostIds);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "스크랩 상태 로드 실패: " + e.getMessage()));
    }

    // 게시물 ID 리스트를 기반으로 Firestore에서 게시물 데이터 로드
    private void fetchPostsByIds(List<String> postIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("message_boards")
                .whereIn("postId", postIds) // postId가 스크랩된 ID 리스트에 포함된 게시물만 가져오기
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BoardItem> tempItems = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String postId = document.getString("postId");
                        String title = document.getString("title");
                        String content = document.getString("review");
                        String cover = document.getString("cover");
                        String author = document.getString("author");
                        com.google.firebase.Timestamp timestamp = document.getTimestamp("timestamp");

                        tempItems.add(new BoardItem(postId, title, content, cover, author, timestamp));
                    }

                    // 데이터 정렬 (최신순 정렬)
                    tempItems.sort((item1, item2) -> item2.getTimestamp().compareTo(item1.getTimestamp()));

                    // RecyclerView 업데이트
                    items.clear();
                    items.addAll(tempItems);
                    adapter.notifyDataSetChanged();
                    Log.d("UInterface", "스크랩된 게시물 로드 완료 : " + items.size());
                })
                .addOnFailureListener(e -> Log.e("UInterface", "게시물 로드 실패: " + e.getMessage()));
    }


    // 내부 클래스: RecyclerView 어댑터
    class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
        private List<BoardItem> itemList;

        public BoardAdapter(List<BoardItem> itemList) {
            this.itemList = itemList;
        }

        public void updateList(List<BoardItem> filteredList) {
            itemList = filteredList;
            notifyDataSetChanged();
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
            BoardItem item = itemList.get(position);

            // 데이터 바인딩
            holder.titleText.setText(item.getTitle());
            holder.contentText.setText(item.getContent());
            Log.d("UInterface", "Binding Author: " + item.getAuthor());
            if (holder.authorText != null) {
                holder.authorText.setText(item.getAuthor());
            } else {
                Log.e("UInterface", "authorText is null");
            }

            // Glide를 사용해 이미지 로드
            Glide.with(holder.bookImage.getContext())
                    .load(item.getCover())
                    .error(R.drawable.error)
                    .into(holder.bookImage);

            // 별표(스크랩 아이콘) 숨기기
            holder.likeButton.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessageBoardLikeActivity.this, MessageBoardReviewActivity.class);
                    intent.putExtra("postId", item.getPostId());
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("author", item.getAuthor());
                    intent.putExtra("description", item.getContent());
                    intent.putExtra("cover", item.getCover());
                    intent.putExtra("review", item.getContent()); // 리뷰 데이터를 추가
                    startActivity(intent);
                }
            });
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
                authorText = itemView.findViewById(R.id.authorText);
                bookImage = itemView.findViewById(R.id.book_image);
                likeButton = itemView.findViewById(R.id.like_button);
            }
        }
    }

    // 게시물 데이터를 저장할 클래스
    public class BoardItem {
        private final String postId;
        private final String title;
        private final String content;
        private final String cover; // 책 이미지 URL
        private final String author;
        private final com.google.firebase.Timestamp timestamp; // 추가

        public BoardItem(String postId, String title, String content, String cover, String author, com.google.firebase.Timestamp timestamp) {
            this.postId = postId;
            this.title = title;
            this.content = content;
            this.cover = cover;
            this.author = author;
            this.timestamp = timestamp;
        }

        public String getPostId() {
            return postId;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getCover() {
            return cover;
        }

        public String getAuthor() {
            return author;
        }

        public com.google.firebase.Timestamp getTimestamp() {
            return timestamp;
        }
    }
}