package com.example.userinterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.ActivityMessageBoardReviewBinding;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageBoardReview extends AppCompatActivity {
    ImageView reviewImage;
    TextView reviewTitle;
    TextView reviewAuthor;
    TextView reviewDescription;

    EditText editText;
    Button okButton;

    RecyclerView comment;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBoardReviewBinding binding = ActivityMessageBoardReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reviewImage = binding.reviewCover;
        reviewTitle = binding.reviewTitle;
        reviewAuthor = binding.reviewAuthor;
        reviewDescription = binding.reviewDescription;

        //데이터 수신
        comment = binding.reviewRecyclerView;
        comment.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter();
        comment.setAdapter(adapter);

        editText = binding.reviewEditText;
        okButton = binding.okButton;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addComment(new CommentItem(
                        "홍길동",
                        editText.getText().toString(),
                        System.currentTimeMillis()
                        , null
                ));
                editText.setText("");
            }
        });

    }
}

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{
    List<CommentItem> comments;
    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_datgeul, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.setComment(comments.get(position));

    }
    public CommentAdapter(){
        comments = new ArrayList<>();
    }
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void addComment(CommentItem comment){
        comments.add(comment);
        notifyDataSetChanged();
    }

    static class CommentHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView comment;
        TextView time;
        ImageView coverImage;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameTextView);
            comment = itemView.findViewById(R.id.contentTextView);
            time = itemView.findViewById(R.id.timestampTextView);
            coverImage = itemView.findViewById(R.id.userCoverImage);
        }
        public void setComment(CommentItem item){
            username.setText(item.getUsername());
            comment.setText(item.getComment());
            time.setText(TimeUtils.getTimeAgo(item.getTime()));

            Glide.with(itemView)
                    .load(item.getCoverImage())
                    .error(R.drawable.imagewait)
                    .into(coverImage);

        }
    }
}