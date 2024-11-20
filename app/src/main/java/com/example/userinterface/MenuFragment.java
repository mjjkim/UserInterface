package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.FragmentMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuFragment extends Fragment {
    private FragmentMenuBinding binding;

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

            // Firestore에서 사용자 닉네임 및 프로필 이미지 URL 가져오기
            db.collection("users").document(uid)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Log.e("UInterface", "Firestore 데이터 가져오기 실패: " + error.getMessage());
                            Toast.makeText(requireContext(), "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // 닉네임 업데이트
                            String nickname = documentSnapshot.getString("nickname");
                            if (nickname != null && !nickname.isEmpty()) {
                                binding.menuNickname.setText("닉네임 : " + nickname);
                            } else {
                                binding.menuNickname.setText("닉네임 : nickname");
                            }

                            // 프로필 이미지 업데이트
                            String imageUrl = documentSnapshot.getString("profileImage");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                setImageToImageView(imageUrl);
                            } else {
                                // 기본 이미지 설정
                                binding.menuProfileImage.setImageResource(R.drawable.profile);
                            }
                        }
                    });
        }

        // 설정 버튼 클릭 시 설정 창 이동
        binding.settingButton.setOnClickListener(view1 -> {
            startActivity(new Intent(requireContext(), SettingActivity.class));
        });

        // 로그아웃 버튼 클릭 시 로그아웃
        binding.logoutButton.setOnClickListener(view1 -> {
            // Firebase Authentication에서 로그아웃
            mAuth.signOut();

            // 로그아웃 성공 메시지
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // 현재 프래그먼트 종료
            requireActivity().finish();
        });
    }

    private void setImageToImageView(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(binding.menuProfileImage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
