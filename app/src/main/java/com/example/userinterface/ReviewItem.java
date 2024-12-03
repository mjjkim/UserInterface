package com.example.userinterface;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewItem extends RecyclerView.Adapter<ReviewItem.ItemViewHolder> {
    private List<BookItem> bookItemList;

    public ReviewItem(List<BookItem> itemList) {
        this.bookItemList = itemList;
    }

    private ReviewItem.OnItemClickListener listener; // 클릭 이벤트 인터페이스


    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(BookItem bookData);
    }

    public void addItem(BookItem item){
        bookItemList.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BookItem item = bookItemList.get(position);
        holder.setItem(item);
        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item); // 클릭된 데이터 전달
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookItemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView;
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView bookimageTextView;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
//            descriptionTextView = itemView.findViewById(R.id.tv_description);
            authorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            bookimageTextView = itemView.findViewById(R.id.bookCoverImageView);
        }
        public void setItem(BookItem item){
            titleTextView.setText(item.getTitle());
            authorTextView.setText(item.getAuthor());
            Glide.with(itemView.getContext())
                    .load(item.getBookImage())
                    .error(R.drawable.imagewait)
                    .into(bookimageTextView);
        }
    }
}
