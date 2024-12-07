package com.example.userinterface;

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
    private List<BoardItem> list;

    // 클릭 이벤트 인터페이스
    private PhraseAdapter.OnItemClickListener listener;

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(BoardItem bookData);
    }

    // 클릭 이벤트 리스너 설정 메서드
    public void setOnItemClickListener(PhraseAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public PhraseAdapter(){
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public PhraseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phrase, parent, false);
        return new PhraseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseHolder holder, int position) {
        BoardItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.author.setText(item.getAuthor());
        holder.phrase.setText(item.getDescription().isEmpty() ? "인상 깊은 구절을 입력하세요" : item.getDescription());
        Glide.with(holder.itemView)
                .load(item.getBookImage())
                .into(holder.cover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    // 리스트 반환 메서드 추가
    public List<BoardItem> getList() {
        return list;
    }

    // 리스트 초기화
    public void clearPhrases() {
        list.clear();
        notifyDataSetChanged();
    }

    // 특정 항목 업데이트 메서드
    public void updatePhrase(String title, String updatedPhrase, String updatedFeel, String cover) {
        for (BoardItem item : list) {
            if (item.getTitle().equals(title)) {
                item.setDescription(updatedPhrase);
                item.setPubDate(updatedFeel);
                item.setBookImage(cover);
                notifyDataSetChanged();
                return;
            }
        }
    }

    // 항목을 추가하거나 업데이트하는 메서드
    public void addPhrase(BoardItem newItem) {
        boolean itemUpdated = false;
        for (int i = 0; i < list.size(); i++) {
            BoardItem item = list.get(i);
            if (item.getTitle().equals(newItem.getTitle())) {
                // 기존 항목 업데이트
                item.setDescription(newItem.getDescription());
                item.setPubDate(newItem.getPubDate());
                item.setBookImage(newItem.getBookImage());
                notifyItemChanged(i);
                itemUpdated = true;
                break;
            }
        }
        if (!itemUpdated) {
            // 새로운 항목 추가
            list.add(newItem);
            notifyItemInserted(list.size() - 1);
        }
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
