package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userinterface.databinding.FragmentMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuFragment extends Fragment {
    FragmentMenuBinding binding;

    // 로그아웃 만들기

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);

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
                                TextView titleTextView = binding.main.findViewById(R.id.menuNickname);
                                titleTextView.setText("닉네임 : " + nickname);
                            }
                            else {
                                // 닉네임 문서가 존재하지 않을 때 기본값
                                binding.menuNickname.setText("닉네임 : 닉네임");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Firestore 읽기 실패 처리
                        Toast.makeText(requireContext(), "닉네임을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    });
        }

        // 설정 버튼 클릭 시 설정 창 이동
        binding.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), SettingActivity.class));
            }
        });

    }
}