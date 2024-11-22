package com.example.userinterface;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.ActivityMessageBoradBinding;

import java.util.ArrayList;
import java.util.List;

public class MessageBorad extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private MessageBoardAdapter adapter;
    private List<MessageBoardItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_borad);
        ActivityMessageBoradBinding binding = ActivityMessageBoradBinding.inflate(getLayoutInflater());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        items = new ArrayList<>();
        items.add(new MessageBoardItem("제목: 지웅이가 싫어요", "내용: 지웅이 딱밤 한대", R.drawable.book, true));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실1" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실2" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실3" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실4" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실5" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실6" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실7" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실8" , "override는 ~~~ ", R.drawable.book, true ));
        items.add(new MessageBoardItem("제목: 지웅이의 자바 교실9" , "override는 ~~~ ", R.drawable.book, true ));


        adapter = new MessageBoardAdapter(this, items);
        recyclerView.setAdapter(adapter);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == binding.backButton){
                    finish();
                }
            }
        });

    }




}