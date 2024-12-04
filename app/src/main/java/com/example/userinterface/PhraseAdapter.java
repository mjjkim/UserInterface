package com.example.userinterface;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.PhraseHolder> {
    List<BoardItem> list;
    List<String> phrases;
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addPhrase(String s){
        phrases.add(s);
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
