package com.example.userinterface;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.SearchBookViewHolder>{

    private Activity mContext;

    private List<AladinSearchBookData> bookList = new ArrayList<>();

    private OnItemClickListener listener; // 클릭 이벤트 인터페이스

    public SearchBookAdapter(Activity context){this.mContext=context;}

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(AladinSearchBookData bookData);
    }

    // 클릭 이벤트 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class SearchBookViewHolder extends RecyclerView.ViewHolder{
        public ImageView cover;
        public TextView title, description, author;

        public SearchBookViewHolder(@NonNull View view){
            super(view);
            cover = view.findViewById(R.id.image_book);
            title = view.findViewById(R.id.tv_title);
            author = view.findViewById(R.id.tv_author);
            description = view.findViewById(R.id.tv_description);
        }
        public void setItem(AladinSearchBookData item){
            title.setText(item.getTitle());
            author.setText(item.getAuthor());
            description.setText(item.getDescription());


            Glide.with(itemView.getContext())
                    .load(item.getCover())
                    .error(R.drawable.imagewait)
                    .into(cover);
        }
    }

    @NonNull
    @Override
    public SearchBookAdapter.SearchBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_review, parent, false);
        SearchBookAdapter.SearchBookViewHolder holder = new SearchBookAdapter.SearchBookViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookAdapter.SearchBookViewHolder holder, int position) {
        // bh1. view에 아이템 setting 하기
        AladinSearchBookData item = bookList.get(position);
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
        return bookList.size();
    }

    // 아이템 추가
    public void addItem(AladinSearchBookData bookitem) {
        Log.i("search book adapter - additem", "책 제목: " + bookitem.getTitle() + "책 작가: " + bookitem.getAuthor());
        bookList.add(bookitem);
        notifyDataSetChanged(); // 리사이클러뷰 갱신
    }

    // 아이템 삭제
    public void clearItem() {bookList.clear();}

    public List<AladinSearchBookData> getBookList(){
        return bookList;
    }
}
