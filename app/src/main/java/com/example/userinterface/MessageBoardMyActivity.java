package com.example.userinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MessageBoardMyActivity extends AppCompatActivity {

    private PostAdapter adapter;
    private List<PostItem> items;

    @SuppressLint("MissingInflatedId")
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
        db.collection("message_boards")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("UInterface", "Firestore 데이터 가져오기 실패 : " + error.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        items.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String postId = document.getId(); // 문서 ID를 postId로 사용
                            String title = document.getString("title");
                            String content = document.getString("review");
                            String cover = document.getString("cover");
                            String author = document.getString("author");
                            com.google.firebase.Timestamp timestamp = document.getTimestamp("timestamp");

                            // 수정된 부분: PostItem 생성자에 postId를 추가
                            items.add(new PostItem(postId, title, content, userId, cover, author, timestamp));
                        }

                        // 최신순으로 정렬
                        items.sort((item1, item2) -> item2.getTimestamp().compareTo(item1.getTimestamp()));

                        adapter.notifyDataSetChanged();
                        Log.d("UInterface", "내 게시글 로드 완료 : " + items.size());
                    }
                });


        EditText search = findViewById(R.id.messageMySearch);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String query = search.getText().toString().trim();
                    filterBooks(query);
                    // 확인 버튼이 눌렸을 때 실행할 코드
                    String inputText = search.getText().toString();
                    // 입력된 텍스트를 사용하여 원하는 작업 수행
                    Toast.makeText(getApplicationContext(), "입력된 텍스트: " + inputText, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }
    private void filterBooks(String query) {
        List<PostItem> filteredList = new ArrayList<>();
        for (PostItem book : items) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        adapter.updateList(filteredList);
    }

    // 내부 클래스: RecyclerView 어댑터
    class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private List<PostItem> itemList;

        public PostAdapter(List<PostItem> itemList) {
            this.itemList = itemList;
        }

        public void updateList(List<PostItem> filteredList) {
            itemList = filteredList;
            notifyDataSetChanged();
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

            // 별표(스크랩 아이콘) 숨기기
            holder.likeButton.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessageBoardMyActivity.this, MessageBoardReviewActivity.class);
                    intent.putExtra("postId", item.getPostId()); // 게시물의 postId 전달
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("author", item.getAuthor());
                    intent.putExtra("description", item.getContent());
                    intent.putExtra("cover", item.getCover());
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
        private final com.google.firebase.Timestamp timestamp; // timestamp 필드 추가
        private final String postId;

        public PostItem(String postId, String title, String content, String userId, String cover, String author, com.google.firebase.Timestamp timestamp) {
            this.postId = postId;
            this.title = title;
            this.content = content;
            this.userId = userId;
            this.cover = cover;
            this.author = author;
            this.timestamp = timestamp; // timestamp 초기화
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

        public String getUserId() {
            return userId;
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