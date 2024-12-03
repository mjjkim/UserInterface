package com.example.userinterface;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // View Binding 객체

    // 내 서재 리사이클러뷰
    private RecyclerView recyclerView;
    private ReviewItem bookItemAdapter;

    // 글귀모음 리사이클러뷰
    private RecyclerView phraseRecyclerView;//어댑터
    private SearchBookAdapter reviewAdapter;
    private List<BookItem> itemList; // bookitem 리스트

    //수신받는 정보
    String title;
    String author;
    String description;
    String publisher;
    String pubDate;
    String cover;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 글귀 모음 예시
        phraseRecyclerView = binding.PhraseRecyclerView;
        reviewAdapter = new SearchBookAdapter(getActivity());

        reviewAdapter.addItem(new AladinSearchBookData(
                "title",
                "author",
                "description",
                "publisher",
                "pubDate",
                String.valueOf(R.drawable.pin),
                "isbn"
        ));
        reviewAdapter.addItem(new AladinSearchBookData(
                "title",
                "author",
                "description",
                "publisher",
                "pubDate",
                String.valueOf(R.drawable.imagewait),
                "isbn"
        ));
        phraseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        phraseRecyclerView.setAdapter(reviewAdapter);

        recyclerView = binding.recyclerview;
        itemList = new ArrayList<>();

        // 데이터 수신
        title = getActivity().getIntent().getStringExtra("title");
        author = getActivity().getIntent().getStringExtra("author");
        description = getActivity().getIntent().getStringExtra("description");
        publisher = getActivity().getIntent().getStringExtra("publisher");
        pubDate = getActivity().getIntent().getStringExtra("pubDate");
        cover = getActivity().getIntent().getStringExtra("cover");


        // 어댑터 연결
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        bookItemAdapter = new ReviewItem(itemList);
        recyclerView.setAdapter(bookItemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);


        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firestore 인스턴스 생성
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // 현재 로그인한 사용자 UID
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Firestore에서 사용자 닉네임 가져오기
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // 닉네임 가져오기
                            String nickname = documentSnapshot.getString("nickname");

                            if (nickname != null && !nickname.isEmpty()) {
                                // 닉네임을 텍스트뷰에 설정
                                TextView titleTextView = binding.main.findViewById(R.id.homeNickname);
                                titleTextView.setText("  "+ nickname + "의 서재");
                            }
                        } else {
                            // 닉네임 문서가 존재하지 않을 때 기본값
                            binding.homeNickname.setText("닉네임의 서재");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Firestore 읽기 실패 처리
                        Toast.makeText(requireContext(), "닉네임을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    });
        }

        // 토글 버튼 클릭시 독서 기록, 글귀 모음 전환
        MaterialButtonToggleGroup toggleGroup = binding.toggleButtonGroup;
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                FrameLayout RecodeFrameLayout = binding.bookFrameLayout;
                FrameLayout PhraseFrameLayout = binding.PhraseFrameLayout;
                if (checkedId == R.id.record) {
                    //  독서 기록 탭
                    RecodeFrameLayout.setVisibility(View.VISIBLE);
                    PhraseFrameLayout.setVisibility(View.GONE);

                }
                else if (checkedId == R.id.phrase) {
                    // 글귀 모음 탭
                    RecodeFrameLayout.setVisibility(View.GONE);
                    PhraseFrameLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        bookItemAdapter.addItem(new BookItem(
                                title,
                                author,
                                description,
                                cover,
                                pubDate,
                                publisher
                        ));
                    }
                });
        binding.RecordAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new Intent(getActivity(), MyRecordSearchActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

    }
    public void onDestroyView() {
        super.onDestroyView();
        // 뷰 참조를 해제하여 메모리 누수 방지
        recyclerView = null;
        binding = null;
    }
}