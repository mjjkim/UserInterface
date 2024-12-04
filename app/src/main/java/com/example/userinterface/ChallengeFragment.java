package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ChallengeFragment extends Fragment {


    private TextView challengeSet;
    private TextView challengeDetails;
    private TextView challengeSetText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        challengeSet = view.findViewById(R.id.challengeSet);
        challengeDetails = view.findViewById(R.id.challengeDetails);
        challengeSetText = view.findViewById(R.id.challenge_set_text);

        // 챌린지 설정 버튼 클릭 이벤트
        challengeSet.setOnClickListener(v -> openChallengeDialog());

        return view;
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
}