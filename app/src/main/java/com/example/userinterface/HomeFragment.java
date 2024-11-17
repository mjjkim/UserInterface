package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userinterface.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding; // View Binding 객체

    private RecyclerView recyclerView; //리사이클러 뷰
    private ItemAdapter itemAdapter; //어댑터
    private List<BookItem> itemList; // bookitem 리스트

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 아이템 리스트 예시
        recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        itemList.add(new BookItem("1aaaa", "2", "33", "4"));
        itemList.add(new BookItem("1sf", "2", "33", "4"));
        itemList.add(new BookItem("1fas", "2", "33", "4"));
        itemList.add(new BookItem("1asfas", "2", "33", "4"));
        itemList.add(new BookItem("1da", "2", "33", "4"));
        itemList.add(new BookItem("1aaaa", "2", "33", "4"));
        itemList.add(new BookItem("1sf", "2", "33", "4"));
        itemList.add(new BookItem("1fas", "2", "33", "4"));
        itemList.add(new BookItem("1asfas", "2", "33", "4"));
        itemList.add(new BookItem("1da", "2", "33", "4"));

        // 어댑터 연결
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
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
                if (checkedId == R.id.mbt_record) {
                    //  독서 기록 탭
                }
                else if (checkedId == R.id.mbt_text) {
                    // 글귀 모음 탭
                }
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