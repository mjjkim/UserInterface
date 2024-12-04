package com.example.userinterface;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MessageBoardAdapter extends RecyclerView.Adapter<MessageBoardAdapter.ViewHolder>{

    private Context context;
    private List<MessageBoardItem> items;

    private MessageBoardAdapter.OnItemClickListener listener; // 클릭 이벤트 인터페이스

    // 클릭 이벤트 리스너 설정 메서드
    public void setOnItemClickListener(MessageBoardAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MessageBoardItem item);
    }

    public MessageBoardAdapter(Context context, List<MessageBoardItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_board, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageBoardItem item = items.get(position);

        // 제목, 내용 저자 표시
        holder.titleText.setText(item.getTitle());
        holder.contentText.setText(item.getReview());
        Log.d("UInterface", item.getReview());
        holder.author.setText(item.getAuthor());

        // Glide로 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(item.getCover())
                .error(R.drawable.imagewait)
                .into(holder.bookImage);

        // 좋아요 버튼 상태에 따른 UI 업데이트
        holder.likeButton.setImageResource(item.isLiked() ? R.drawable.vector_star_filled : R.drawable.vector_star);

        // 좋아요 버튼 클릭 이벤트
        holder.likeButton.setOnClickListener(v -> {
            // 좋아요 상태를 변경
            boolean newLikedState = !item.isLiked();
            item.setLiked(newLikedState);

            // 상태 반영
            holder.likeButton.setImageResource(newLikedState ? R.drawable.vector_star_filled : R.drawable.vector_star);

            // Firestore에 상태 업데이트
            updateLikeStatusInFirestore(item, newLikedState);

            // Toast 메시지 표시
            String message = newLikedState ? "스크랩되었습니다!" : "스크랩이 해제되었습니다!";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });

        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item); // 클릭된 데이터 전달
            }
        });
    }

    private void updateLikeStatusInFirestore(MessageBoardItem item, boolean isLiked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getUid();

        String postId = item.getPostId();
        if (postId == null || postId.isEmpty()) {
            Log.e("UInterface", "postId가 null이거나 비어 있습니다. 업데이트를 중단합니다.");
            return;
        }

        db.collection("user_likes").document(currentUserId)
                .update(postId, isLiked)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "스크랩 상태 업데이트 성공: " + postId);
                })
                .addOnFailureListener(e -> {
                    // 문서가 없으면 새로 생성
                    Map<String, Object> initialData = new HashMap<>();
                    initialData.put(postId, isLiked);
                    db.collection("user_likes").document(currentUserId)
                            .set(initialData)
                            .addOnSuccessListener(aVoid -> Log.d("UInterface", "스크랩 상태 초기화 후 저장 성공: " + postId))
                            .addOnFailureListener(err -> Log.e("UInterface", "스크랩 상태 저장 실패: " + err.getMessage()));
                });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    // viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImage, likeButton;
        TextView titleText, contentText, author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image);
            titleText = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
            likeButton = itemView.findViewById(R.id.like_button);
            author = itemView.findViewById(R.id.authorText);
        }
    }



}
