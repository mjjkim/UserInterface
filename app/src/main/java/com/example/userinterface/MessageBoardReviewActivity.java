package com.example.userinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.userinterface.databinding.ActivityMessageBoardReviewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBoardReviewActivity extends AppCompatActivity {
    private ImageView reviewImage;
    private TextView reviewTitle, reviewAuthor, reviewDescription;
    private EditText etReview, editText;
    private Button okButton;
    private RecyclerView comment;
    private CommentAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    // 게시물 고유 ID
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoardReviewBinding binding = ActivityMessageBoardReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 책 정보 바인딩
        reviewImage = binding.reviewCover;
        reviewTitle = binding.reviewTitle;
        reviewAuthor = binding.reviewAuthor;
        reviewDescription = binding.reviewDescription;
        etReview = binding.etReview;

        // 게시물 수신 및 설정
        documentId = getIntent().getStringExtra("postId");
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "게시물 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 책 데이터 수신 및 표시
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String decription = getIntent().getStringExtra("description");
        String cover = getIntent().getStringExtra("cover");

        // 책 데이터 수신 및 표시
        reviewTitle.setText(getIntent().getStringExtra("title"));
        reviewAuthor.setText(getIntent().getStringExtra("author"));
        reviewDescription.setText(getIntent().getStringExtra("description"));
        Glide.with(this)
                .load(getIntent().getStringExtra("cover"))
                .into(binding.reviewCover);

        etReview.setText(getIntent().getStringExtra("review"));
        etReview.setFocusable(false);
        etReview.setFocusableInTouchMode(false);

        // 댓글 및 리사이클러뷰 설정
        comment = binding.reviewRecyclerView;
        comment.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter();
        comment.setAdapter(adapter);

        // 댓글 작성
        editText = binding.reviewEditText;
        okButton = binding.okButton;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        // 뒤로 가기 버튼
        binding.messageBoardReviewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Firestore에서 게시물 데이터 로드
        db.collection("message_boards").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.reviewTitle.setText(documentSnapshot.getString("title"));
                        binding.reviewAuthor.setText(documentSnapshot.getString("author"));
                        binding.reviewDescription.setText(documentSnapshot.getString("description"));
                        Glide.with(this)
                                .load(documentSnapshot.getString("cover"))
                                .into(binding.reviewCover);
                    } else {
                        Toast.makeText(this, "게시물을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "게시물 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Firestore에서 댓글 로드
        loadCommentsFromFirestore();

    }

    private void loadCommentsFromFirestore() {
        db.collection("message_boards")
                .document(documentId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CommentItem> commentList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) { // 타입 명시
                        String userId = document.getString("userId"); // Firestore에 댓글 작성자의 userId를 저장했다고 가정
                        String commentText = document.getString("comment");
                        long timestamp = document.getLong("timestamp");

                        // 사용자 정보 가져오기
                        db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    String username = userSnapshot.getString("nickname");
                                    String profileImage = userSnapshot.getString("profileImage");

                                    // 댓글 리스트에 추가
                                    commentList.add(new CommentItem(username, commentText, timestamp, profileImage));
                                    // 어댑터에 업데이트
                                    adapter.setComments(commentList);
                                })
                                .addOnFailureListener(e -> Log.e("UInterface", "Failed to fetch user info: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "댓글 불러오기 실패 : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void addComment() {
        String commentText = editText.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // 댓글 작성자의 userId
        long timestamp = System.currentTimeMillis();

        // 댓글 데이터 생성
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("userId", userId);
        commentData.put("comment", commentText);
        commentData.put("timestamp", timestamp);

        // Firestore에 댓글 저장
        db.collection("message_boards")
                .document(documentId)
                .collection("comments")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {
                    editText.setText(""); // 입력창 초기화
                    loadCommentsFromFirestore(); // 댓글 다시 로드
                })
                .addOnFailureListener(e -> Toast.makeText(this, "댓글 저장 실패 : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private final List<CommentItem> comments = new ArrayList<>();

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_datgeul, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.setComment(comments.get(position));

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<CommentItem> commentList) {
        comments.clear();
        comments.addAll(commentList);
        // RecyclerView UI 갱신
        notifyDataSetChanged(); // RecyclerView UI 갱신
    }


    static class CommentHolder extends RecyclerView.ViewHolder{
        private final TextView username, comment, time;
        private final ImageView coverImage;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameTextView);
            comment = itemView.findViewById(R.id.contentTextView);
            time = itemView.findViewById(R.id.timestampTextView);
            coverImage = itemView.findViewById(R.id.userCoverImage);
        }

        public void setComment(CommentItem item) {
            username.setText(item.getUsername() != null ? item.getUsername() : "닉네임");
            comment.setText(item.getComment());
            time.setText(TimeUtils.getTimeAgo(item.getTime()));

            Glide.with(itemView)
                    // 기본 이미지 사용
                    .load(item.getCoverImage() != null ? item.getCoverImage() : R.drawable.profile)
                    // 오류 시 기본 이미지
                    .error(R.drawable.profile)
                    .into(coverImage);
        }


    }
}