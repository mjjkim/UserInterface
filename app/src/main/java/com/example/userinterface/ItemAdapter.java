package com.example.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<BookItem> bookItemList;

    public ItemAdapter(List<BookItem> itemList) {
        this.bookItemList = itemList;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BookItem currentItem = bookItemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.bookimageTextView.setImageResource(R.drawable.vege);
        holder.authorTextView.setText(currentItem.getAuthor());
//        holder.descriptionTextView.setText(currentItem.getDescription());
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
    }
}
