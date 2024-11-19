package com.example.userinterface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMyrecordBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRecordActivity extends AppCompatActivity {
    // import
    AladinApiSevice service; // 레트로핏 서비스

    // 책 목록을 출력하기 위한 리사이클러뷰 및 어댑터
    private LinearLayoutManager linearLayoutManager; // 리사이클러뷰를 위한 리니어 레이아웃
    private RecyclerView recyclerView;
    SearchBookAdapter bookAdapter; // 책검색 결과 리사이클러뷰 어댑터

    // int
    private int currentPage = 1; // 현재 페이지 번호
    private final int maxResults = 100; // 한 페이지당 결과 개수
    int total_page = 0; // 결과 값에 대한 총 페이지 수. // 문제가 생겼을때 결과값이 0이 되도록.

    // string
    String ttbkey = "ttbdlsrks09871450001"; // 알라딘 인증 키


    // view
    EditText book_search; // 책 검색어 입력창

    ActivityMyrecordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyrecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ----------------- 초기화
        // 레트로핏 객체 생성
        service = AladinRetrofitInstance.getClient().create(AladinApiSevice.class);

        book_search = binding.ettest;

        bookAdapter = new SearchBookAdapter(MyRecordActivity.this);

        recyclerView = binding.test;
        linearLayoutManager = new LinearLayoutManager(MyRecordActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookAdapter);


        book_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String searchQuery = book_search.getText().toString().trim(); // 검색어 입력값
                    if (!searchQuery.isEmpty()) { // 입력값 있을 때 엔터 누를 경우.
                        // 에디트 텍스트 "" 빈값 만들지 않기.

                        if (bookAdapter.getItemCount() > 0) { // 검색할 때 마다 기존 검색 결과 clear 하기.
                            bookAdapter.clearItem();
                        }

                        currentPage = 1;
                        total_page = 0; // 재검색시 결과 페이지 다시 0으로.
                        search_send(ttbkey, searchQuery, currentPage);
                        Toast.makeText(MyRecordActivity.this, "입력한 검색어 : " + searchQuery, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MyRecordActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                    return true; // 엔터키 처리 완료
                }
                return false; //  엔터 키 처리 안함.

            }
        });
    }
    private void search_send (String ttbkey, String keyword, int page) {
        Log.e("알라딘검색메소드", "키: " + ttbkey + " 검색어 keyword : "+keyword + "요청 페이지: " + page);
        service.getSearchBook(ttbkey,
                keyword,
                "Keyword",
                maxResults,
                page,
                "Book",
                "JS",
                "20131101").enqueue(new Callback<AladinResponse.AladinResponse2>() {
            @Override
            public void onResponse(Call<AladinResponse.AladinResponse2> call, Response<AladinResponse.AladinResponse2> response) {
                AladinResponse.AladinResponse2 result = response.body();
                Log.e("알라딘api 검색 성공", "onResponse: " + response.body());

                // 책 목록 가져오기.
                List<AladinResponse> books = result.getBooks(); // 검색 결과 책 목록
                int result_total = result.getTotalResults(); // 검색 결과 개수
                int item_perpage_cnt = result.getItemsPerPage(); // 한 페이지에 출력할 책의 개수
                Log.i("검색결과 개수 및 페이지수 ", "총 개수: " + result_total + " , 페이지당 아이템개수: " + item_perpage_cnt );

                // 총 페이지수
                total_page = (int) Math.ceil((double) result_total/(double) maxResults);

                // 책 목록 - 각 책 정보 출력
                for (AladinResponse book : books ) {
                    Log.i("검색결과책 정보 >>>  ", "Title: " + book.getTitle() + ", Author: " + book.getAuthor() +
                            "Description: " + book.getDescription() + ", Publisher: " + book.getPublisher() +
                            ", PubDate: " + book.getPubDate() + ", Cover: " + book.getCover() + ", ISBN: "+ book.getIsbn());
                    String title = book.getTitle();
                    String author =  book.getAuthor();
                    String description = book.getDescription();
                    String publisher =  book.getPublisher();
                    String pubDate = book.getPubDate();
                    String cover = book.getCover();
                    String isbn = book.getIsbn();
                    // add search book adapter
                    bookAdapter.addItem(new AladinSearchBookData(title, author, description, publisher, pubDate, cover, isbn));
                    //   Log.i("검색책목록개수!! ", "getItemCount: " + bookAdapter.getItemCount());
                }
//                isLoading = false; // 로딩 완료 후 플래그 초기화. / 페이징용.



            }

            @Override
            public void onFailure(Call<AladinResponse.AladinResponse2> call, Throwable throwable) {
                throwable.getMessage();
                Log.e("알라딘api 검색", "onFailure: " + throwable.getMessage());
            }
        });

    }
}




class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.SearchBookViewHolder>{

    private Activity mContext;

    private List<AladinSearchBookData> bookList = new ArrayList<>();

    public SearchBookAdapter(Activity context){this.mContext=context;}

    static class SearchBookViewHolder extends RecyclerView.ViewHolder{
        public ImageView cover;
        public TextView title, description, author;

        public SearchBookViewHolder(@NonNull View view){
            super(view);
            cover = view.findViewById(R.id.image_book);
            title = view.findViewById(R.id.tv_title);
            author = view.findViewById(R.id.tv_author);
            description = view.findViewById(R.id.tv_description);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        public void setItem(AladinSearchBookData item){
            title.setText(item.getTitle());
            author.setText(item.getAuthor());
            description.setText(item.getDescription());


            Glide.with(itemView.getContext())
                    .load(item.getCover())
                    .error("@drawable/imagewait")
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
}
