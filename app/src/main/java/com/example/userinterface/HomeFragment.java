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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // View Binding 객체

    // 내 서재 리사이클러뷰
    private RecyclerView recyclerView;
    private ReviewItem bookItemAdapter;
    private List<BoardItem> itemList;

    private FirebaseFirestore db;
    private String userId;

    // 글귀 모음 리사이클러뷰
    private RecyclerView phraseRecyclerView;//어댑터
    private PhraseAdapter phraseAdapter;

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

        phraseRecyclerView = binding.PhraseRecyclerView;
        phraseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        phraseAdapter = new PhraseAdapter();
        phraseRecyclerView.setAdapter(phraseAdapter);

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

        binding.RecordAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyRecordSearchActivity.class);
            launcher.launch(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        });

        ActivityResultLauncher<Intent> phraseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
//                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        String title = result.getData().getStringExtra("title");
//                        String author = result.getData().getStringExtra("author");
//                        String cover = result.getData().getStringExtra("cover");
//                        String updatedPhrase = result.getData().getStringExtra("phrase");
//                        String updatedFeel = result.getData().getStringExtra("feel");
//
//                        Log.d("UInterface", "Received Updated Phrase: " + updatedPhrase);
//                        Log.d("UInterface", "Received Updated Feel: " + updatedFeel);
//
//                        if (title != null) {
//                            BoardItem updatedItem = new BoardItem(title, author, updatedPhrase, cover, updatedFeel, null);
//
//                            // Adapter에 업데이트
//                            phraseAdapter.addPhrase(updatedItem);
//
//                            // Firestore에 저장
//                            savePhraseToFirestore(updatedItem);
//                        } else {
//                            Log.e("UInterface", "Title is null, cannot save data.");
//                        }
//                    }
//                    else {
//                        Log.e("UInterface", "Result not OK or data is null.");
//                    }
                }
        );




//        binding.PhraseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                phraseLauncher.launch(new Intent(getActivity(), MyRecordSearchActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
//            }
//        });

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

        //글귀 모음 클릭할 경우 이동
        phraseAdapter.setOnItemClickListener(new PhraseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BoardItem bookData) {
                startActivity(new Intent(getActivity(), MyPhraseModifyActivity.class)
                        .putExtra("title", bookData.getTitle())
                        .putExtra("author", bookData.getAuthor())
                        .putExtra("cover", bookData.getBookImage())
                        .putExtra("phrase", bookData.getDescription())
                        .putExtra("feel", bookData.getPubDate()));
            }
        });
    }

    private void loadBooksFromFirestore() {
        db.collection("users")
                .document(userId)
                .collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            BoardItem item = BoardItem.fromMap(document.getData());
                            if (item != null) {
                                itemList.add(item);
                            }
                        }
                        bookItemAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("UInterface", "No books found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Books 데이터 로드 실패: " + e.getMessage()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // 프래그먼트로 돌아왔을 때 데이터 다시 로드
        loadBooksFromFirestore();
        loadPhrasesFromFirestore(); // 글귀 모음 데이터 새로 불러오기
    }



    private void loadPhrasesFromFirestore() {
        db.collection("users")
                .document(userId)
                .collection("phrases")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("UInterface", "글귀 데이터 가져오기 성공");
                    phraseAdapter.clearPhrases(); // 기존 데이터 초기화
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            BoardItem item = BoardItem.fromMap(document.getData());
                            if (item != null) { // null 체크
                                Log.d("UInterface", "가져온 데이터: " + item.getTitle());
                                phraseAdapter.addPhrase(item);
                            } else {
                                Log.d("UInterface", "데이터 변환 실패: " + document.getId());
                            }
                        }
                        phraseAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("UInterface", "글귀 데이터가 없습니다.");
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "글귀 데이터 로드 실패: " + e.getMessage()));
    }

    private void saveBookToFirestore(BoardItem bookItem) {
        db.collection("users")
                .document(userId)
                .collection("books")
                .document(bookItem.getTitle()) // 제목을 문서 ID로 사용
                .set(bookItem.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "책 데이터 저장 성공");

                    // 글귀 모음에 기본 데이터 자동 추가
                    BoardItem phraseItem = new BoardItem(
                            bookItem.getTitle(),
                            bookItem.getAuthor(),
                            "",                 // phrase (인상 깊은 글귀는 빈 값)
                            bookItem.getBookImage(),
                            "",                 // feel (느낀 점은 빈 값)
                            null
                    );

                    savePhraseToFirestore(phraseItem); // Firestore에 저장
                    phraseAdapter.addPhrase(phraseItem); // 바로 Adapter에 추가
                    phraseAdapter.notifyDataSetChanged(); // Adapter 갱신
                })
                .addOnFailureListener(e -> Log.e("UInterface", "책 데이터 저장 실패: " + e.getMessage()));
    }



    private void savePhraseToFirestore(BoardItem phraseItem) {
        db.collection("users")
                .document(userId)
                .collection("phrases")
                .document(phraseItem.getTitle()) // 제목을 문서 ID로 사용
                .set(phraseItem.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "글귀 데이터 저장 성공: " + phraseItem.getTitle());
                    Toast.makeText(getActivity(), "글귀 데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "글귀 데이터 저장 실패: " + e.getMessage());
                    Toast.makeText(getActivity(), "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void updatePhrasesFromFirestore() {
        db.collection("users")
                .document(userId)
                .collection("phrases")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    phraseAdapter.clearPhrases(); // 기존 데이터 초기화
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BoardItem item = BoardItem.fromMap(document.getData());
                        if (item != null) {
                            phraseAdapter.addPhrase(item);
                        }
                    }
                });
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