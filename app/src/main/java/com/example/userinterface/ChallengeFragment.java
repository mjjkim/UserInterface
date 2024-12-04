package com.example.userinterface;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userinterface.databinding.FragmentChallengeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

public class ChallengeFragment extends Fragment {
    private FragmentChallengeBinding binding;
    private TextView challengeSet;
    private TextView challengeDetails;
    private TextView challengeSetText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChallengeBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        challengeSet = binding.challengeSet;
        challengeDetails = binding.challengeDetails;
        challengeSetText = binding.challengeSetText;

        // 챌린지 설정 버튼 클릭 이벤트
        challengeSet.setOnClickListener(v -> openChallengeDialog());
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
                            // Firestore 문서 확인
                            Log.d("Firestore", "문서 데이터: " + documentSnapshot.getData());
                            String nickname = documentSnapshot.getString("nickname");

                            if (nickname != null && !nickname.isEmpty()) {
                                binding.challengeNickname.setText(nickname + "의 챌린지");
                                Log.d("Firestore", "닉네임: " + nickname);
                            } else {
                                binding.challengeNickname.setText("닉네임의 챌린지");
                                Log.d("Firestore", "닉네임 필드가 비어있음");
                            }
                        } else {
                            Log.d("Firestore", "문서가 존재하지 않음");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Firestore 읽기 실패: " + e.getMessage());
                        Toast.makeText(requireContext(), "닉네임을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    });

        }

        Button button1 = view.findViewById(R.id.tab_in_progress);
        Button button2 = view.findViewById(R.id.tab_past_challenges);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setTextColor(Color.rgb(0, 123, 255));
                button2.setTextColor(Color.rgb(85, 85, 85));
                button1.setBackgroundColor(Color.rgb(211, 234, 253));
                button2.setBackgroundColor(Color.rgb(224, 224, 224));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setTextColor(Color.rgb(85, 85, 85));
                button2.setTextColor(Color.rgb(0, 123, 255));
                button2.setBackgroundColor(Color.rgb(211, 234, 253));
                button1.setBackgroundColor(Color.rgb(224, 224, 224));
            }
        });
    }
    private void openChallengeDialog() {
        ChallengeSettingDialog dialog = new ChallengeSettingDialog();
        dialog.setOnChallengeDataListener((challenge, date) -> {
            // 데이터 수신 후 화면에 반영
            challengeSetText.setText(challenge);
            challengeDetails.setText("기간: " + date);
        });
        dialog.show(getParentFragmentManager(), "ChallengeSettingDialog");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // View Binding 해제
        binding = null;
    }
}