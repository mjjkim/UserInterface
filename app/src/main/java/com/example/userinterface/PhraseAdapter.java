package com.example.userinterface;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.PhraseHolder> {
    List<BoardItem> list;

    private PhraseAdapter.OnItemClickListener listener; // 클릭 이벤트 인터페이스

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(BoardItem bookData);
    }

    // 클릭 이벤트 리스너 설정 메서드
    public void setOnItemClickListener(PhraseAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public PhraseAdapter(){list = new ArrayList<>();
    }
    @NonNull
    @Override
    public PhraseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phrase, parent, false);
        return new PhraseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.author.setText(list.get(position).getAuthor());
        holder.phrase.setText(list.get(position).getDescription());
        Glide.with(holder.itemView)
                .load(list.get(position).getBookImage())
                        .into(holder.cover);
        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(list.get(position)); // 클릭된 데이터 전달
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addPhrase(BoardItem item){
        list.add(item);
        notifyDataSetChanged();
    }

    static class PhraseHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView title;
        TextView author;
        TextView phrase;
        public PhraseHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.phraseCover);
            title = itemView.findViewById(R.id.phraseTitle);
            author = itemView.findViewById(R.id.phraseAuthor);
            phrase = itemView.findViewById(R.id.phraseContent);
        }
    }

}
