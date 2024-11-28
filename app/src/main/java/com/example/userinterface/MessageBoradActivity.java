package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.ActivityMessageBoradBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageBoradActivity extends AppCompatActivity {

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        items = new ArrayList<>();
        adapter = new MessageBoardAdapter(this, items);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Firestore에서 데이터 가져오기
        db.collection("message_boards").document(userId)
                        .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists() && documentSnapshot.contains("posts")) {
                                        List<Map<String, Object>> posts = (List<Map<String, Object>>) documentSnapshot.get("posts");
                                        for (Map<String, Object> post : posts){
                                            String title = (String) post.get("title");
                                            String author = (String) post.get("author");
                                            String cover = (String) post.get("cover");
                                            String review = (String) post.get("review");
                                            items.add(new MessageBoardItem(title, author, cover, true, review));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                        .addOnFailureListener(e -> {
                                            Log.e("UInterface", "Data Fail! : " + e.getMessage());
                                        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new Intent(MessageBoradActivity.this, MyRecordActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                Log.d("omj", "MessageBoard to MyRecord");
            }
        });
    }




}