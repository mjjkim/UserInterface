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

import java.util.List;

public class MessageBoardAdapter extends RecyclerView.Adapter<MessageBoardAdapter.ViewHolder>{

    private Context context;
    private List<MessageBoardItem> items;

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
        holder.titleText.setText(item.getTitle());
        holder.contentText.setText(item.getReview());
        Log.d("omj", item.getReview());
        holder.likeButton.setImageResource(item.isLiked() ? R.drawable.star : R.drawable.star);
        holder.author.setText(item.getAuthor());

        Glide.with(holder.itemView.getContext())
                .load(item.getCover())
                .error(R.drawable.imagewait)
                .into(holder.bookImage);

        holder.likeButton.setOnClickListener(v -> {
            item.setLiked(!item.isLiked());
            notifyItemChanged(position);
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
            author = itemView.findViewById(R.id.writer);
        }
    }



}
