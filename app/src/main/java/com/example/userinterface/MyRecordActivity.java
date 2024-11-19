package com.example.userinterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.userinterface.databinding.ActivityMyrecordBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecordActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    ActivityMyrecordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyrecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://www.aladin.co.kr/ttb/api/")
//                .addConverterFactory(SimpleXmlConverterFactory.create())
//                .build();
        recyclerView = binding.test;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchBooks("파울로 코엘료");
//        AladinApiSevice apiService = retrofit.create(AladinApiSevice.class);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER:
                String s = binding.ettest.getText().toString();
                fetchBooks(s);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void fetchBooks(String query) {
        AladinApiSevice apiService = AladinRetrofitInstance.getApiService();
        Call<AladinResponse> call = apiService.searchBooks(
                "ttbdlsrks09871450001",
                query,
                "Title",
                10,
                "xml",
                "20131101"
        );

        call.enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookItem> books = response.body().getItems();
                    recyclerView.setAdapter(new BookAdapter(books));
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<BookItem> books;

    public BookAdapter(List<BookItem> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookItem book = books.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_title);
            authorTextView = itemView.findViewById(R.id.tv_author);
        }
    }
}
