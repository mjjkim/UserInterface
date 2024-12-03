package com.example.userinterface;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
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

        // 스크랩 버튼 상태에 따라 이미지 설정
        holder.likeButton.setImageResource(item.isLiked() ? R.drawable.star_filled : R.drawable.star);

        holder.likeButton.setOnClickListener(v -> {
            boolean newLikedState = !item.isLiked();
            // 상태 업데이트
            item.setLiked(!item.isLiked());
            // UI 업데이트
            notifyItemChanged(position);

            // Firestor에 스크랩 상태 업데이트
            updateLikeStatusInFirestore(item, newLikedState);
        });
    }

    // Firestore에 스크랩 상태 업데이트 메서드
    private void updateLikeStatusInFirestore(MessageBoardItem item, boolean isLiked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 기존 항목 삭제 및 업데이트
        db.collection("message_boards").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> posts = (List<Map<String, Object>>) documentSnapshot.get("posts");
                if (posts != null) {
                    for (Map<String, Object> post : posts)
                    {
                        if (post.get("title").equals(item.getTitle())) {
                            // 스크랩 상태만 업데이트
                            post.put("liked", isLiked);
                            break;
                        }
                    }
                }
                db.collection("message_boards").document(userId)
                        .update("posts", posts)
                        .addOnSuccessListener(avoid -> Log.d("UInterface", "Firestore 업데이트 성공"))
                        .addOnFailureListener(e -> Log.e("UInterface", "Firestore 업데이트 실패 : " + e.getMessage()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    // viewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImage, likeButton;
        TextView titleText, contentText;
        TextView author;

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
