package com.example.userinterface;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    private String title;
    private String author;
    private String description;
    private String review;
    private String cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoradBinding binding = ActivityMessageBoradBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        items = new ArrayList<>();

        adapter = new MessageBoardAdapter(this, items);
        recyclerView.setAdapter(adapter);

        binding.boardBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        title = data.getStringExtra("title");
                        author = data.getStringExtra("author");
                        cover = data.getStringExtra("cover");
                        description = data.getStringExtra("description");
                        review = data.getStringExtra("review");
                        items.add(new MessageBoardItem(title, author, cover, true, review));
                        adapter.notifyDataSetChanged();
                        Log.d("omj", "Data Get Ok for MessageBoard");
                    }
                }
            }
        });
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new Intent(MessageBorad.this, MyRecordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                Log.d("omj", "MessageBoard to MyRecord");
            }
        });
    }




}