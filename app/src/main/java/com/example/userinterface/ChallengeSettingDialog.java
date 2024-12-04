package com.example.userinterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ChallengeSettingDialog extends DialogFragment {

    private OnChallengeDataListener listener;

    public interface OnChallengeDataListener {
        void onChallengeDataReceived(String challengeName, String dateRange);
    }

    public void setOnChallengeDataListener(OnChallengeDataListener listener) {
        this.listener = listener;
    }

    private TextView dateDisplay;
    private TextView recommendedChallenges;
    private EditText personalChallengeInput;
    private CheckBox checkboxRecommended, checkboxPersonal;
    private Button saveButton, cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_setting_dialog, container, false);

        dateDisplay = view.findViewById(R.id.date_display);
        recommendedChallenges = view.findViewById(R.id.text_recommended_challenge);
        personalChallengeInput = view.findViewById(R.id.edit_personal_challenge);
        checkboxRecommended = view.findViewById(R.id.checkbox_recommended);
        checkboxPersonal = view.findViewById(R.id.checkbox_personal);
        saveButton = view.findViewById(R.id.btn_save);
        cancelButton = view.findViewById(R.id.btn_cancel);

        // 날짜 선택 (기간)
        dateDisplay.setOnClickListener(v -> showDateRangePicker());

        // 추천 챌린지 리스트 띄우기
        recommendedChallenges.setOnClickListener(v -> showRecommendedChallenge());

        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener(v -> saveChallengeData());

        // 취소 버튼 클릭 이벤트
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    // 날짜 범위 선택
    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("기간을 선택하세요");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 선택된 시작 날짜와 종료 날짜를 가져옴
            Pair<Long, Long> dateRange = (Pair<Long, Long>) selection;
            String startDate = formatDate(dateRange.first); // 시작 날짜
            String endDate = formatDate(dateRange.second); // 종료 날짜

            // 날짜 범위를 TextView에 표시
            dateDisplay.setText(String.format("%s ~ %s", startDate, endDate));
        });

        datePicker.show(getParentFragmentManager(), "DateRangePicker");
    }

    // 날짜를 문자열로 포맷
    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // 추천 챌린지 리스트 띄우기
    private void showRecommendedChallenge() {
        String[] challenges = getResources().getStringArray(R.array.recommended_challenges);

        // AlertDialog로 리스트 띄우기
        new AlertDialog.Builder(requireContext())
                .setTitle("추천 챌린지를 선택하세요")
                .setItems(challenges, (dialog, which) -> {
                    // 사용자가 선택한 추천 챌린지를 텍스트뷰에 표시
                    recommendedChallenges.setText(challenges[which]);
                })
                .show();
    }

    private void saveChallengeData() {
        String dateRange = dateDisplay.getText().toString().trim();
        String challengeData = null;

        // 두 체크박스가 모두 선택된 경우
        if (checkboxRecommended.isChecked() && checkboxPersonal.isChecked()) {
            Toast.makeText(getContext(), "추천 챌린지와 개인 챌린지 중 하나만 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 추천 챌린지 선택
        if (checkboxRecommended.isChecked()) {
            challengeData = recommendedChallenges.getText().toString();
        }

        // 개인 챌린지 선택
        if (checkboxPersonal.isChecked()) {
            challengeData = personalChallengeInput.getText().toString().trim();
        }

        // 입력 데이터 검증
        if (challengeData == null || challengeData.isEmpty() || dateRange.isEmpty()) {
            Toast.makeText(getContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터 전달 및 다이얼로그 닫기
        if (listener != null) {
            listener.onChallengeDataReceived(challengeData, dateRange);
        }
        dismiss();
    }
}


