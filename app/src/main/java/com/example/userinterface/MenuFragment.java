package com.example.userinterface;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.userinterface.databinding.FragmentMenuBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MenuFragment extends Fragment {
    FragmentMenuBinding binding;

    // import
    AladinApiSevice service; // 레트로핏 서비스

    // 책 목록을 출력하기 위한 리사이클러뷰 및 어댑터
    private LinearLayoutManager linearLayoutManager; // 리사이클러뷰를 위한 리니어 레이아웃
    private RecyclerView recyclerView;
    SearchBookAdapter bookAdapter; // 책검색 결과 리사이클러뷰 어댑터
    List<AladinSearchBookData> itemList;

    // int
    private int currentPage = 1; // 현재 페이지 번호
    private final int maxResults = 100; // 한 페이지당 결과 개수
    int total_page = 0; // 결과 값에 대한 총 페이지 수. // 문제가 생겼을때 결과값이 0이 되도록.

    // string
    String ttbkey = "ttbdlsrks09871450001"; // 알라딘 인증 키


    // view
    EditText book_search; // 책 검색어 입력창

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
                            }
                            else {
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
        binding.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), SettingActivity.class));
            }
        });

        // 댓글 단 글
        binding.myComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MyComment.class));
            }
        });

        // 내가 쓴 글
        binding.myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MyWrite.class));
            }
        });

        // 스크랩 한 글
        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(),MyLike.class));
            }
        });


        // 로그아웃 버튼 클릭 시 로그아웃
        binding.logoutButton.setOnClickListener(view1 -> {
            // Firebase Authentication에서 로그아웃
            mAuth.signOut();

            // 로그아웃 성공 메시지
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

            // 로그인 화면으로 이동
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            // FLAG_ACTIVITY_CLEAR_TOP : 백 스택에 호출하려는 액티비티가 이미 존재하면 해당 액티비티를 맨 앞으로 가져오고, 그 위의 모든 액티비티를 제거
            // FLAG_ACTIVITY_NEW_TASK : 호출하려는 액티비티가 존재하지 않으면 새로 생성하고 새로운 태스크로 실행
            // 결과 : 기존이 스택에서 로그인 액티비티 위의 모든 액티비티가 제거되고, 로그인이 최상단에 위치
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // 현재 프래그먼트 종료
            requireActivity().finish();
        });

        // 개시판
        binding.borad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MessageBoradActivity.class));
            }
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
        // 메모리 누수 방지
        binding = null;
    }
}