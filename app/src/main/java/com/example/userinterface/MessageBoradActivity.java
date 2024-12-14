package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.userinterface.databinding.ActivityMessageBoradBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBoradActivity extends AppCompatActivity {

    private List<MessageBoardItem> items;
    private MessageBoardAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoradBinding binding = ActivityMessageBoradBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 초기화
        items = new ArrayList<>();
        adapter = new MessageBoardAdapter(this, items);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        binding.boardBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 게시글 불러오기
        loadMessageBoards();

        // 검색 버튼 동작 설정
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            addNewPost(data);
                        }
                    }
                });

        binding.searchButton.setOnClickListener(view -> {
            Intent intent = new Intent(MessageBoradActivity.this, MyRecordActivity.class);
            launcher.launch(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            Log.d("UInterface", "MessageBoard to MyRecord");
        });

        adapter.setOnItemClickListener(new MessageBoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessageBoardItem item) {
                Intent intent = new Intent(MessageBoradActivity.this, MessageBoardReviewActivity.class);
                intent.putExtra("postId", item.getPostId()); // postId 전달
                intent.putExtra("title", item.getTitle());
                intent.putExtra("author", item.getAuthor());
                intent.putExtra("cover", item.getCover());
                intent.putExtra("review", item.getReview());
                intent.putExtra("description", item.getDescription());
                startActivity(intent);
            }
        });


        //책 이름 검색 ㅇㅇ;
        binding.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    String query = binding.searchBar.getText().toString().trim();
                    filterBooks(query);
                    return true;
                }
                return false;
            }
        });

    }
    private void filterBooks(String query) {
        List<MessageBoardItem> filteredList = new ArrayList<>();
        for (MessageBoardItem book : items) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        adapter.updateList(filteredList);
    }

    private void loadMessageBoards() {
        db.collection("message_boards")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MessageBoardItem> tempItems = new ArrayList<>(); // tempItems를 메서드 내에서 정의
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String postId = document.getId();
                        String title = document.getString("title");
                        String author = document.getString("author");
                        String cover = document.getString("cover");
                        String review = document.getString("review");
                        String description = document.getString("description");
                        String userId = document.getString("userId");
                        com.google.firebase.Timestamp timestamp = document.getTimestamp("timestamp");

                        tempItems.add(new MessageBoardItem(postId, title, author, cover, false, review, userId, timestamp, description));
                    }

                    // 최신순 정렬
                    tempItems.sort((item1, item2) -> item2.getTimestamp().compareTo(item1.getTimestamp()));

                    // 데이터를 items에 추가하고 동기화
                    syncScrapStatus(tempItems);
                })
                .addOnFailureListener(e -> Log.e("UInterface", "게시글 로드 실패: " + e.getMessage()));
    }

    private void syncScrapStatus(List<MessageBoardItem> tempItems) {
        db.collection("user_likes").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> likes = documentSnapshot.getData();
                        if (likes != null) {
                            for (MessageBoardItem item : tempItems) {
                                String postId = item.getPostId();
                                if (likes.containsKey(postId) && (Boolean) likes.get(postId)) {
                                    item.setLiked(true); // 스크랩 상태 업데이트
                                }
                            }
                        }
                    }
                    items.clear();
                    items.addAll(tempItems);
                    adapter.notifyDataSetChanged();
                    Log.d("UInterface", "스크랩 상태 동기화 완료");
                })
                .addOnFailureListener(e -> Log.e("UInterface", "스크랩 상태 동기화 실패: " + e.getMessage()));
    }



    // Firestore에 스크랩 상태 업데이트
    public void updateScrapStatus(String postId, boolean isLiked) {
        db.collection("user_likes").document(currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Map<String, Object> updates = snapshot.exists() ? snapshot.getData() : new HashMap<>();
                    updates.put(postId, isLiked); // postId를 키로 사용

                    db.collection("user_likes").document(currentUserId)
                            .set(updates)
                            .addOnSuccessListener(aVoid -> Log.d("UInterface", "스크랩 상태 저장 성공"))
                            .addOnFailureListener(e -> Log.e("UInterface", "스크랩 상태 저장 실패: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Firestore 읽기 실패: " + e.getMessage()));
    }



    // 새로운 게시글 추가
    private void addNewPost(Intent data) {
        String title = data.getStringExtra("title");
        String author = data.getStringExtra("author");
        String cover = data.getStringExtra("cover");
        String review = data.getStringExtra("review");
        String description = data.getStringExtra("description");

        String postId = db.collection("message_boards").document().getId(); // 고유 ID 생성

        Map<String, Object> newPost = new HashMap<>();
        newPost.put("postId", postId);
        newPost.put("title", title);
        newPost.put("author", author);
        newPost.put("cover", cover);
        newPost.put("review", review);
        newPost.put("userId", currentUserId);
        newPost.put("liked", false);
        newPost.put("timestamp", com.google.firebase.Timestamp.now());

        db.collection("message_boards").document(currentUserId)
                .update("posts", com.google.firebase.firestore.FieldValue.arrayUnion(newPost))
                .addOnSuccessListener(aVoid -> {
                    items.add(new MessageBoardItem(postId, title, author, cover, false, review, currentUserId, com.google.firebase.Timestamp.now(), description));
                    adapter.notifyDataSetChanged();
                    Log.d("UInterface", "게시글 추가 성공");
                })
                .addOnFailureListener(e -> Log.e("UInterface", "게시글 추가 실패: " + e.getMessage()));




    }

}