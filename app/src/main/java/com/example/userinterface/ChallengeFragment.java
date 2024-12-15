package com.example.userinterface;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userinterface.databinding.FragmentChallengeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeFragment extends Fragment {
    private FragmentChallengeBinding binding;
    private TextView challengeSet, challengeDetails, challengeSetText;
    private CalendarAdapter adapter;
    private List<DayModel> dayList = new ArrayList<>();

    private int challengeSuccessCount, challengeFloatingCount, challengeFailCount;

    // Firebase
    private FirebaseFirestore db;
    private String userId;

    private Calendar calendar = Calendar.getInstance(); // 전역 캘린더
    // 파이차트
    private ArrayList<PieEntry> pieEntries;
    private PieChart pieChart;
    private BarChart barChart;

    // 클래스 멤버 변수로 선언
    private int[] successCounts = new int[12]; // 1월~12월 성공 횟수 저장용

    private HashMap<String, String> dateStatusMap = new HashMap<>();

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

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser(); // 현재 사용자 확인
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("UInterface", "User logged in with ID: " + userId);
        } else {
            Log.e("UInterface", "User is not logged in.");
            Toast.makeText(getContext(), "로그인이 필요합니다. 로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();
            getActivity().finish(); // 로그아웃 상태라면 화면 종료
        }

        // RecyclerView 설정
        adapter = new CalendarAdapter(dayList, null);
        binding.calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7)); // 7열 (요일 기준)
        binding.calendarRecyclerView.setAdapter(adapter);

        // BarChart 초기화
        barChart = binding.barChart; // XML 레이아웃의 BarChart ID

        // PieChart 초기화
        pieChart = binding.pieChart;

        // UI 초기화 및 Firebase 데이터 로드
        initializeCalendar(); // adapter 초기화 후 호출
        loadChallengeFromFirestore();
        // 차트 데이터 로드
        loadPieChartDataFromFirestore(); // 원형 차트 데이터 로드
        loadBarChartDataFromFirestore(); // 막대 차트 데이터 로드
        loadUserNickname();

        setupButtonActions();
        loadUserNickname();

        // 성공 버튼 클릭 이벤트
        binding.successButton.setOnClickListener(v -> {
            updateDateStatus("success"); // 오늘 날짜 상태를 "성공"으로 저장
            int currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH); // 현재 월 인덱스 가져오기
            updateMonthlySuccess(currentMonthIndex); // 바 차트 갱신
        });

        // 실패 버튼 클릭 이벤트
        binding.failureButton.setOnClickListener(v -> {
            updateDateStatus("failure"); // 오늘 날짜 상태를 "실패"로 저장
        });

        // 초기 월 텍스트 설정
        updateMonthText();

        if (userId == null || userId.isEmpty()) {
            Log.e("UInterface", "User ID is null or empty.");
            Toast.makeText(getContext(), "유효한 사용자 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UInterface", "User ID initialized: " + userId);

        Button button1 = view.findViewById(R.id.tab_in_progress);
        Button button2 = view.findViewById(R.id.tab_past_challenges);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setTextColor(Color.rgb(0, 123, 255));
                button2.setTextColor(Color.rgb(85, 85, 85));
                button1.setBackgroundColor(Color.rgb(211, 234, 253));
                button2.setBackgroundColor(Color.rgb(224, 224, 224));
                binding.challenging.setVisibility(View.VISIBLE);
                binding.pastChallenge.setVisibility(View.GONE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setTextColor(Color.rgb(85, 85, 85));
                button2.setTextColor(Color.rgb(0, 123, 255));
                button2.setBackgroundColor(Color.rgb(211, 234, 253));
                button1.setBackgroundColor(Color.rgb(224, 224, 224));
                binding.challenging.setVisibility(View.GONE);
                binding.pastChallenge.setVisibility(View.VISIBLE);
            }
        });

        binding.challengeSuccessButton.setOnClickListener(rootView -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("챌린지에 성공하셨습니까?")
                    .setPositiveButton("성공", (dialogInterface, i) -> {
                        updatePieChart("success");        // 파이 차트 업데이트
                        resetChallengeFirestoreAndUI();   // 챌린지 초기화 (바 차트는 갱신 안함)
                    })
                    .setNegativeButton("실패", (dialogInterface, i) -> {
                        updatePieChart("failure");        // 파이 차트 업데이트
                        resetChallengeFirestoreAndUI();   // 챌린지 초기화 (바 차트는 갱신 안함)
                    })
                    .create()
                    .show();
        });

        binding.challengeFloatingButton.setOnClickListener(rootView -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("챌린지를 포기하시겠습니까?")
                    .setPositiveButton("확인", (dialogInterface, i) -> {
                        updatePieChart("floating");       // 파이 차트 업데이트
                        resetChallengeFirestoreAndUI();   // 챌린지 초기화 (바 차트는 갱신 안함)
                    })
                    .setNegativeButton("취소", null)
                    .create()
                    .show();
        });


        //past challenge
        BarChart barChart = binding.barChart;

        // 샘플 데이터 (Firestore 데이터로 대체 가능)
        int[] successCounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // 월별 성공 횟수
        String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"}; // 월 이름

        // BarEntry 리스트 생성
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < successCounts.length; i++) {
            entries.add(new BarEntry(i, successCounts[i]+i));
        }

        // BarDataSet 생성
        BarDataSet dataSet = new BarDataSet(entries, "월별 성공 횟수");
        dataSet.setColor(getResources().getColor(R.color.theme_pink)); // 바 색상 설정
        dataSet.setValueTextSize(14f);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // BarData 생성
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // 바 너비 설정

        // BarChart에 데이터 설정
        barChart.setData(barData);

        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); // 월 이름 적용
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);


        // Y축 설정
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f); // 축 레이블 간격 설정
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f); // 최소값 설정
        leftAxis.setAxisMaximum(30f); // 최대값 설정
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // 오른쪽 Y축 숨기기
        // 소수점 제거: 정수로 표시

        // 기타 설정
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false); // 설명 텍스트 비활성화
        barChart.animateY(1000); // 애니메이션


        //pieChart 초기 설정
        pieChart = binding.pieChart;

        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(challengeSuccessCount, "성공"));
        pieEntries.add(new PieEntry(challengeFailCount, "실패"));
        pieEntries.add(new PieEntry(challengeFloatingCount, "포기"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "카테고리");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);

    }

    private void updateMonthlySuccess(int monthIndex) {
        if (monthIndex >= 0 && monthIndex < 12) {
            successCounts[monthIndex]++;       // 현재 월의 성공 횟수 +1
            saveChartDataToFirestore();        // Firestore에 저장
            setupBarChart();                   // 바 차트 갱신
        }
    }


    private void updatePieChart(String status) {
        // 상태에 따라 카운트 업데이트
        switch (status) {
            case "success":
                challengeSuccessCount++;
                break;
            case "failure":
                challengeFailCount++;
                break;
            case "floating":
                challengeFloatingCount++;
                break;
        }

        // Firestore에 저장
        savePieChartDataToFirestore();

        // 파이 차트 갱신
        setupPieChart();
    }

    private void savePieChartDataToFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot save pie chart data.");
            return;
        }

        Map<String, Object> pieChartData = new HashMap<>();
        pieChartData.put("successCount", challengeSuccessCount);
        pieChartData.put("failCount", challengeFailCount);
        pieChartData.put("floatingCount", challengeFloatingCount);

        db.collection("users").document(userId).collection("challenges")
                .document("chartData")
                .set(pieChartData)
                .addOnSuccessListener(aVoid -> Log.d("UInterface", "Pie chart data saved successfully."))
                .addOnFailureListener(e -> Log.e("UInterface", "Error saving pie chart data: " + e.getMessage()));
    }


    private void setupChartUI() {
        // BarChart 설정
        BarChart barChart = binding.barChart;
        int[] successCounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < successCounts.length; i++) {
            entries.add(new BarEntry(i, successCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "월별 성공 횟수");
        dataSet.setColor(getResources().getColor(R.color.theme_pink));
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

        // PieChart 설정
        pieChart = binding.pieChart;
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(challengeSuccessCount, "성공"));
        pieEntries.add(new PieEntry(challengeFailCount, "실패"));
        pieEntries.add(new PieEntry(challengeFloatingCount, "포기"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "결과");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.invalidate();
    }


    private void setupButtonActions() {
        Button button1 = binding.getRoot().findViewById(R.id.tab_in_progress);
        Button button2 = binding.getRoot().findViewById(R.id.tab_past_challenges);

        button1.setOnClickListener(view -> {
            button1.setTextColor(Color.rgb(0, 123, 255));
            button2.setTextColor(Color.rgb(85, 85, 85));
            button1.setBackgroundColor(Color.rgb(211, 234, 253));
            button2.setBackgroundColor(Color.rgb(224, 224, 224));
            binding.challenging.setVisibility(View.VISIBLE);
            binding.pastChallenge.setVisibility(View.GONE);
        });

        button2.setOnClickListener(view -> {
            button1.setTextColor(Color.rgb(85, 85, 85));
            button2.setTextColor(Color.rgb(0, 123, 255));
            button2.setBackgroundColor(Color.rgb(211, 234, 253));
            button1.setBackgroundColor(Color.rgb(224, 224, 224));
            binding.challenging.setVisibility(View.GONE);
            binding.pastChallenge.setVisibility(View.VISIBLE);
        });
    }

    private void updateChart(int monthIndex) {
        if (monthIndex >= 0 && monthIndex < 12) {
            successCounts[monthIndex]++; // 현재 월(0~11) 인덱스에 값 증가
            saveChartDataToFirestore();  // Firestore에 저장
            setupBarChart();             // 차트 갱신
        } else {
            Log.e("UInterface", "Invalid month index: " + monthIndex);
        }
    }


    private void saveChartDataToFirestore() {
        Map<String, Object> chartData = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            chartData.put("month" + (i + 1), successCounts[i]);
        }

        db.collection("users").document(userId).collection("challenges")
                .document("monthlySuccess")
                .set(chartData)
                .addOnSuccessListener(aVoid -> Log.d("UInterface", "Chart data updated successfully."))
                .addOnFailureListener(e -> Log.e("UInterface", "Error saving chart data: " + e.getMessage()));
    }


    private void loadUserNickname() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        if (nickname != null && !nickname.isEmpty()) {
                            updateChallengeTitle(nickname);
                        } else {
                            updateChallengeTitle("닉네임");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "닉네임 불러오기 실패: " + e.getMessage());
                    Toast.makeText(getContext(), "닉네임을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateChallengeTitle(String nickname) {
        String title = nickname + "의 챌린지";
        binding.challengeNickname.setText(title);
    }

    private void loadChallengeFromFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot load challenge.");
            return;
        }

        db.collection("users").document(userId).collection("challenges")
                .document("currentChallenge")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String challenge = documentSnapshot.getString("challenge");
                        String date = documentSnapshot.getString("date");

                        if (challenge != null && date != null) {
                            challengeSetText.setText(challenge);
                            challengeDetails.setText("기간: " + date);
                            binding.dailyGoalText.setText("오늘 " + challenge + "를 완료하세요!");
                        }
                    } else {
                        resetChallengeUI();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Error loading challenge: " + e.getMessage());
                });
    }




    // 챌린지 기간이 만료되었는지 확인하는 함수
    private boolean isChallengeExpired(Calendar currentCalendar, String endDate) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.ENGLISH);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(sdf.parse(endDate));

            return currentCalendar.after(endCalendar); // 현재 날짜가 종료 날짜 이후인지 확인
        } catch (Exception e) {
            Log.e("UInterface", "Error parsing endDate: " + e.getMessage());
            return true; // 오류 발생 시 만료된 것으로 처리
        }
    }


    // Firestore에서 데이터 불러오기
    private void loadPieChartDataFromFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot load pie chart data.");
            return;
        }

        db.collection("users").document(userId).collection("challenges")
                .document("chartData") // 원형 차트용 데이터 불러오기
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Firestore에서 값 가져오기
                        challengeSuccessCount = documentSnapshot.contains("successCount") ? documentSnapshot.getLong("successCount").intValue() : 0;
                        challengeFailCount = documentSnapshot.contains("failCount") ? documentSnapshot.getLong("failCount").intValue() : 0;
                        challengeFloatingCount = documentSnapshot.contains("floatingCount") ? documentSnapshot.getLong("floatingCount").intValue() : 0;

                        setupPieChart(); // 원형 차트 갱신
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Error loading pie chart data: " + e.getMessage()));
    }

    private void setupPieChart() {
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(challengeSuccessCount, "성공"));
        pieEntries.add(new PieEntry(challengeFailCount, "실패"));
        pieEntries.add(new PieEntry(challengeFloatingCount, "포기"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "결과");

        // 커스텀 색상 설정
        int[] customColors = {
                Color.rgb(76, 175, 80),  // 초록색 (성공)
                Color.rgb(244, 67, 54),  // 빨간색 (실패)
                Color.rgb(255, 193, 7)   // 노란색 (포기)
        };
        pieDataSet.setColors(customColors);

        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false); // 가운데 구멍 없애기
        pieChart.animateY(1000);
        pieChart.invalidate(); // 차트 갱신
    }


    private void loadBarChartDataFromFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot load bar chart data.");
            return;
        }

        db.collection("users").document(userId).collection("challenges")
                .document("monthlySuccess")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 데이터가 존재하면 해당 데이터를 불러옵니다.
                        for (int i = 0; i < 12; i++) {
                            successCounts[i] = documentSnapshot.contains("month" + (i + 1))
                                    ? documentSnapshot.getLong("month" + (i + 1)).intValue()
                                    : 0;
                        }
                    } else {
                        // 데이터가 존재하지 않으면 모든 값을 0으로 초기화합니다.
                        for (int i = 0; i < 12; i++) {
                            successCounts[i] = 0;
                        }
                        saveChartDataToFirestore(); // 초기화된 데이터를 저장합니다.
                    }
                    setupBarChart(); // 차트를 설정합니다.
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Error loading bar chart data: " + e.getMessage()));
    }


    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < successCounts.length; i++) {
            entries.add(new BarEntry(i, successCounts[i])); // 성공 횟수 반영
        }

        BarDataSet dataSet = new BarDataSet(entries, "월별 성공 스티커 개수");
        dataSet.setColor(Color.parseColor("#FF77A9")); // 테마 핑크 색상
        dataSet.setValueTextSize(14f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // 정수만 표시
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        barChart.setData(barData);

        // X축 설정
        String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Y축 설정
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        // 차트 최종 설정
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate(); // 차트 갱신
    }

    private void openChallengeDialog() {
        ChallengeSettingDialog dialog = new ChallengeSettingDialog();
        dialog.setOnChallengeDataListener((challenge, date) -> {
            // UI 업데이트
            challengeSetText.setText(challenge);
            challengeDetails.setText("기간: " + date);
            challengeSet.setClickable(false);

            // Firestore에 챌린지 저장
            saveChallengeToFirestore(challenge, date);
        });
        dialog.show(getParentFragmentManager(), "ChallengeSettingDialog");
    }

    private void saveChallengeToFirestore(String challenge, String date) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot save challenge.");
            return;
        }

        // Firestore에 저장할 데이터
        Map<String, Object> challengeData = new HashMap<>();
        challengeData.put("challenge", challenge);
        challengeData.put("date", date);

        db.collection("users").document(userId).collection("challenges")
                .document("currentChallenge")
                .set(challengeData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "Challenge saved successfully: " + challenge);
                    Toast.makeText(getContext(), "챌린지가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 저장 후 UI 업데이트
                    challengeSetText.setText(challenge);
                    challengeDetails.setText("기간: " + date);
                    binding.dailyGoalText.setText("오늘 " + challenge + "를 완료하세요!");
                    challengeSet.setClickable(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Error saving challenge: " + e.getMessage());
                    Toast.makeText(getContext(), "챌린지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void resetChallengeFirestoreAndUI(int chartType) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot reset challenge.");
            return;
        }

        db.collection("users").document(userId)
                .collection("challenges").document("currentChallenge")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "Challenge successfully reset.");
                    resetChallengeUI();
                    updateChart(chartType); // 차트 데이터 업데이트 (0: 성공, 1: 실패, 2: 포기)
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Error resetting challenge: " + e.getMessage());
                });
    }

    private void resetChallengeFirestoreAndUI() {
        resetChallengeFirestoreAndUI(-1); // 기본 값으로 -1 전달 (차트 갱신 없음)
    }


    private void resetChallengeUI() {
        binding.dailyGoalText.setText("챌린지를 설정해주세요!");
        challengeSet.setClickable(true);
        challengeSetText.setText("챌린지를 설정해주세요!");
        challengeDetails.setText("9999-12-31 ~ 9999-12-31");
    }



    // 캘린더 데이터를 초기화합니다.
    private void initializeCalendar() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot initialize calendar.");
            return;
        }

        dayList.clear(); // 기존 데이터 초기화

        // 현재 월과 연도 가져오기
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // 첫 번째 날 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 일요일: 0
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 첫 주 빈 칸 추가
        for (int i = 0; i < firstDayOfWeek; i++) {
            dayList.add(new DayModel("", new ArrayList<>(), false));
        }

        // 실제 날짜 추가
        for (int day = 1; day <= daysInMonth; day++) {
            boolean isToday = (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    && currentMonth == Calendar.getInstance().get(Calendar.MONTH)
                    && currentYear == Calendar.getInstance().get(Calendar.YEAR));

            String date = String.format("%d-%02d-%02d", currentYear, currentMonth + 1, day);
            dayList.add(new DayModel(date, new ArrayList<>(), isToday));
        }

        adapter.notifyDataSetChanged(); // RecyclerView 갱신

        // Firestore에서 상태 불러오기
        loadCalendarStatus();
    }

    private void loadCalendarStatus() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot load calendar status.");
            return;
        }

        db.collection("users").document(userId)
                .collection("calendarStatus")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String date = document.getId(); // 날짜 (yyyy-MM-dd)
                        String status = document.getString("status");

                        // 캘린더 리스트에서 해당 날짜를 찾아 상태 업데이트
                        for (DayModel day : dayList) {
                            if (day.getDate().equals(date)) {
                                day.setStatus(status);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // RecyclerView 갱신
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Error loading calendar status: " + e.getMessage()));
    }



    private void initializeMonthlySuccess() {
        Map<String, Object> initialData = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            initialData.put("month" + i, 0); // 모든 월의 값을 0으로 초기화
        }

        db.collection("users").document(userId).collection("challenges")
                .document("monthlySuccess")
                .set(initialData)
                .addOnSuccessListener(aVoid -> Log.d("UInterface", "monthlySuccess document initialized"))
                .addOnFailureListener(e -> Log.e("UInterface", "Error initializing monthlySuccess: " + e.getMessage()));
    }

    private void aggregateMonthlySuccess() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot aggregate monthly success.");
            return;
        }

        db.collection("users").document(userId)
                .collection("calendarStatus")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int[] monthlySuccess = new int[12]; // 1월 ~ 12월 성공 횟수 초기화

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String date = document.getId(); // 문서 이름 = 날짜 (yyyy-MM-dd)
                        String status = document.getString("status");

                        if ("success".equals(status)) { // 성공 상태만 집계
                            int month = Integer.parseInt(date.substring(5, 7)) - 1; // 월 추출 (0부터 시작)
                            monthlySuccess[month]++;
                        }
                    }

                    // Firestore에 월별 성공 횟수 저장
                    Map<String, Object> data = new HashMap<>();
                    for (int i = 0; i < 12; i++) {
                        data.put("month" + (i + 1), monthlySuccess[i]);
                    }

                    db.collection("users").document(userId)
                            .collection("challenges")
                            .document("monthlySuccess")
                            .set(data)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("UInterface", "Monthly success data updated successfully.");
                                loadBarChartDataFromFirestore(); // 차트 리프레시
                            })
                            .addOnFailureListener(e -> Log.e("UInterface", "Error saving monthly success data: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Error aggregating calendarStatus: " + e.getMessage()));
    }



    // 연도와 월 표시
    private void updateMonthText() {
        String monthYear = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
                .format(calendar.getTime()); // "December 2024" 형식
        binding.currentMonthText.setText(monthYear);
    }

    // 성공 또는 실패 상태를 업데이트
    private void updateDateStatus(String status) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot update date status.");
            return;
        }

        boolean updated = false;
        String todayDate = null;

        for (DayModel day : dayList) {
            if (day.isToday()) {
                todayDate = day.getDate();
                day.setStatus(status);
                updated = true;
                break;
            }
        }

        if (todayDate == null || todayDate.isEmpty()) {
            Log.e("UInterface", "Today date is null or empty, cannot update Firestore.");
            Toast.makeText(requireContext(), "오늘 날짜를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updated) {
            saveStatusToFirestore(todayDate, status); // Firestore에 상태 저장
            aggregateMonthlySuccess(); // 월별 데이터 업데이트
            adapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "오늘 날짜에 " + (status.equals("success") ? "성공" : "실패") + "로 표시되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }



    // Firestore에 날짜별 상태 저장
    private void saveStatusToFirestore(String date, String status) {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot save status to Firestore.");
            return;
        }

        if (date == null || date.isEmpty()) {
            Log.e("UInterface", "Date is null or empty, cannot save status to Firestore.");
            return;
        }

        Log.d("UInterface", "Saving status to Firestore: userId=" + userId + ", date=" + date + ", status=" + status);

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("status", status);

        db.collection("users").document(userId)
                .collection("calendarStatus").document(date)
                .set(statusMap)
                .addOnSuccessListener(aVoid -> Log.d("UInterface", "Status saved for date: " + date))
                .addOnFailureListener(e -> Log.e("UInterface", "Error saving status to Firestore: " + e.getMessage()));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // View Binding 해제
        binding = null;
    }

    // 데이터 모델
    public class DayModel {
        private String date; // yyyy-MM-dd 형식의 날짜
        private List<String> events; // 해당 날짜의 이벤트 목록
        private boolean isToday;
        private String status; // 성공 또는 실패 상태 저장 (예: "success", "failure")

        public DayModel(String date, List<String> events, boolean isToday) {
            this.date = date;
            this.events = events;
            this.isToday = isToday;
            this.status = ""; // 초기 상태는 빈 값
        }

        public String getDate() {
            return date;
        }

        public List<String> getEvents() {
            return events;
        }

        public boolean isToday() {
            return isToday;
        }

        public String getStatus() {
            return status;
        }

        public void setEvents(List<String> events) {
            this.events = events;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // 달력 리사이클러뷰로 만들기
    public static class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

        private final List<DayModel> dayList;
        private final OnDateClickListener onDateClickListener;
        private View itemView;

        public interface OnDateClickListener {
            void onDateClick(DayModel day);
        }

        public CalendarAdapter(List<DayModel> dayList, OnDateClickListener listener) {
            this.dayList = dayList;
            this.onDateClickListener = listener;
        }

        @NonNull
        @Override
        public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
            return new DayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
            DayModel day = dayList.get(position);
            holder.bind(day);

            // 상태에 따라 스티커 표시
            if (day.getStatus() != null) {
                if (day.getStatus().equals("success")) {
                    holder.statusImage.setVisibility(View.VISIBLE);
                    holder.statusImage.setImageResource(R.drawable.smile); // 성공 이미지
                } else if (day.getStatus().equals("failure")) {
                    holder.statusImage.setVisibility(View.VISIBLE);
                    holder.statusImage.setImageResource(R.drawable.sad); // 실패 이미지
                } else {
                    holder.statusImage.setVisibility(View.GONE); // 상태가 없으면 숨김
                }
            } else {
                holder.statusImage.setVisibility(View.GONE); // 상태가 null인 경우 숨김
            }

            // 오늘 날짜는 배경 강조
            if (day.isToday()) {
                holder.itemView.setBackgroundResource(R.drawable.calendar_day_today); // 오늘 강조
            } else {
                holder.itemView.setBackgroundResource(R.drawable.calendar_day_background);
            }
        }




        @Override
        public int getItemCount() {
            return dayList.size();
        }

        public static class DayViewHolder extends RecyclerView.ViewHolder {
            private final TextView dayText;
            private final TextView eventText;
            private final ImageView statusImage;

            public DayViewHolder(@NonNull View itemView) {
                super(itemView);
                dayText = itemView.findViewById(R.id.day_text);
                eventText = itemView.findViewById(R.id.event_text);
                statusImage = itemView.findViewById(R.id.status_image); // 이미지 뷰 추가
            }

            public void bind(DayModel day) {
                dayText.setText(day.getDate().substring(8)); // 날짜 부분만 표시
                eventText.setText(day.getEvents().isEmpty() ? "" : day.getEvents().get(0)); // 첫 번째 이벤트 표시

                if (day.isToday()) {
                    itemView.setBackgroundResource(R.drawable.calendar_day_today); // 오늘 강조
                } else {
                    itemView.setBackgroundResource(R.drawable.calendar_day_background);
                }
            }
        }

    }




}