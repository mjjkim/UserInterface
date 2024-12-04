package com.example.userinterface;

import android.os.Bundle;
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

        // Firestore에서 댓글 로드
        loadCommentsFromFirestore();

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

        // 댓글 데이터 생성
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("username", currentUser.getDisplayName());
        commentData.put("comment", commentText);
        commentData.put("timestamp", System.currentTimeMillis());
        commentData.put("profileImage", currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null);

        // Firestore에 댓글 저장
        db.collection("comments")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {
                    // 입력창 초기화
                    editText.setText("");
                    // 최신 댓글 로드
                    loadCommentsFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "댓글 저장 실패 : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCommentsFromFirestore() {
        db.collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CommentItem> commentList = new ArrayList<>();
                    queryDocumentSnapshots.forEach(document -> {
                        String username = document.getString("username");
                        String comment = document.getString("comment");
                        long timestamp = document.getLong("timestamp");
                        String profileImage = document.getString("profileImage");

                        commentList.add(new CommentItem(username, comment, timestamp, profileImage));
                    });
                    // 어댑터에 댓글 업데이트
                    adapter.setComments(commentList);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "댓글 불러오기 실패 : " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    public void setComments (List<CommentItem> commentList){
        comments.clear();
        comments.addAll(commentList);
        notifyDataSetChanged();
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

        public void setComment(CommentItem item){
            username.setText(item.getUsername());
            comment.setText(item.getComment());
            time.setText(TimeUtils.getTimeAgo(item.getTime()));

            Glide.with(itemView)
                    .load(item.getCoverImage())
                    .error(R.drawable.imagewait)
                    .into(coverImage);

        }
    }
}