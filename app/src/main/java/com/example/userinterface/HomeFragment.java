package com.example.userinterface;

import static android.app.Activity.RESULT_OK;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // View Binding 객체

    // 내 서재 리사이클러뷰
    private RecyclerView recyclerView;
    private ReviewItem bookItemAdapter;
    private List<BoardItem> itemList;

    private FirebaseFirestore db;
    private String userId;

//    // 글귀모음 리사이클러뷰
//    private RecyclerView phraseRecyclerView;//어댑터
//    private SearchBookAdapter reviewAdapter;
//
//    //수신받는 정보
//    String title;
//    String author;
//    String description;
//    String publisher;
//    String pubDate;
//    String cover;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerview;
        itemList = new ArrayList<>();
        bookItemAdapter = new ReviewItem(itemList);

        // GridLayoutManager 객체 생성 및 RecyclerView 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(bookItemAdapter);

        // Firebase 설정
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 데이터 불러오기
        loadBooksFromFirestore();

        return binding.getRoot();

        // 글귀 모음 예시
//        phraseRecyclerView = binding.PhraseRecyclerView;
//        reviewAdapter = new SearchBookAdapter(getActivity());

//        reviewAdapter.addItem(new AladinSearchBookData(
//                "title",
//                "author",
//                "description",
//                "publisher",
//                "pubDate",
//                String.valueOf(R.drawable.pin),
//                "isbn"
//        ));
//        reviewAdapter.addItem(new AladinSearchBookData(
//                "title",
//                "author",
//                "description",
//                "publisher",
//                "pubDate",
//                String.valueOf(R.drawable.imagewait),
//                "isbn"
//        ));
//        phraseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        phraseRecyclerView.setAdapter(reviewAdapter);

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

        binding.RecordAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyRecordSearchActivity.class);
            launcher.launch(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        });

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
        // 내 서재 클릭할 경우 MyRecordModifyActivity로 이동 이미 저장해 둔 책의 정보를 수정할 수 있는 activity
        bookItemAdapter.setOnItemClickListener(new ReviewItem.OnItemClickListener() {
            @Override
            public void onItemClick(BoardItem bookData) {
                startActivity(new Intent(getActivity(), MyRecordModifyActivity.class)
                        .putExtra("title", bookData.getTitle())
                        .putExtra("author", bookData.getAuthor())
                        .putExtra("publisher", bookData.getPublisher())
                        .putExtra("cover", bookData.getBookImage())
                        .putExtra("description", bookData.getDescription())
                        .putExtra("pubDate", bookData.getPubDate())
                );
            }
        });

//        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult o) {
//                        if(o.getResultCode() == RESULT_OK){
////                             데이터 수신
//                            title = o.getData().getStringExtra("title");
//                            author = o.getData().getStringExtra("author");
//                            description = o.getData().getStringExtra("description");
//                            publisher = o.getData().getStringExtra("publisher");
//                            pubDate = o.getData().getStringExtra("pubDate");
//                            cover = o.getData().getStringExtra("cover");
//
//                            Log.d("omj",  "이름" + title);
//                            bookItemAdapter.addItem(new BoardItem(
//                                    title,
//                                    author,
//                                    description,
//                                    cover,
//                                    pubDate,
//                                    publisher
//                            ));
//                        }
//                    }
//                });

//        binding.toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
//            if (isChecked) {
//                FrameLayout recordFrameLayout = binding.bookFrameLayout;
//                FrameLayout phraseFrameLayout = binding.PhraseFrameLayout;
//                if (checkedId == R.id.record) {
//                    recordFrameLayout.setVisibility(View.VISIBLE);
//                    phraseFrameLayout.setVisibility(View.GONE);
//                } else if (checkedId == R.id.phrase) {
//                    recordFrameLayout.setVisibility(View.GONE);
//                    phraseFrameLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    private void loadBooksFromFirestore() {
        db.collection("user_books").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> books = (List<Map<String, Object>>) documentSnapshot.get("books");
                        if (books != null) {
                            itemList.clear();
                            for (Map<String, Object> book : books) {
                                itemList.add(BoardItem.fromMap(book));
                            }
                            bookItemAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "데이터 불러오기 실패: " + e.getMessage()));
    }

    // Firestore에 책 데이터 저장
    private void saveBookToFirestore(BoardItem bookItem) {
        db.collection("user_books").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // books를 익명 클래스 밖에서 final로 선언
                    final List<Map<String, Object>> books;
                    if (documentSnapshot.exists()) {
                        books = new ArrayList<>((List<Map<String, Object>>) documentSnapshot.get("books"));
                    } else {
                        books = new ArrayList<>();
                    }

                    books.add(bookItem.toMap()); // 새 책 추가

                    // Firestore에 저장
                    Map<String, Object> data = new HashMap<>();
                    data.put("books", books); // Map으로 저장

                    db.collection("user_books").document(userId)
                            .set(data)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "책 데이터 저장 성공"))
                            .addOnFailureListener(e -> Log.e("Firestore", "책 데이터 저장 실패: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Firestore 읽기 실패: " + e.getMessage()));
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String title = result.getData().getStringExtra("title");
                    String author = result.getData().getStringExtra("author");
                    String description = result.getData().getStringExtra("description");
                    String publisher = result.getData().getStringExtra("publisher");
                    String pubDate = result.getData().getStringExtra("pubDate");
                    String cover = result.getData().getStringExtra("cover");

                    BoardItem bookItem = new BoardItem(title, author, description, cover, pubDate, publisher);
                    itemList.add(bookItem);
                    bookItemAdapter.notifyDataSetChanged();

                    saveBookToFirestore(bookItem);
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        binding = null;
    }
}