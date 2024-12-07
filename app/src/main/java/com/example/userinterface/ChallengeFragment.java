package com.example.userinterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

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

    // 성공 실패 체크 데이터
    private boolean challengeCheck = false;
    private int challengeSuccessCount, challengeFloatingCount, challengeFailCount;

    // Firebase
    private FirebaseFirestore db;
    private String userId;

    private Calendar calendar = Calendar.getInstance(); // 전역 캘린더
    // 파이차트
    private ArrayList<PieEntry> pieEntries;
    private PieChart pieChart;

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
        FirebaseUser currentUser = auth.getCurrentUser();

        // RecyclerView 설정
        adapter = new CalendarAdapter(dayList, null);
        binding.calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7)); // 7열 (요일 기준)
        binding.calendarRecyclerView.setAdapter(adapter);

        // 캘린더 데이터 초기화
        initializeCalendar();

        // 성공 버튼 클릭 이벤트
        binding.successButton.setOnClickListener(v -> updateDateStatus("success"));

        // 실패 버튼 클릭 이벤트
        binding.failureButton.setOnClickListener(v -> updateDateStatus("failure"));

        // 월 변경 버튼 이벤트 추가
        binding.prevMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1); // 이전 월로 이동
            updateMonthText(); // 월 텍스트 업데이트
            initializeCalendar(); // 새로운 월 데이터로 초기화
        });

        binding.nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1); // 다음 월로 이동
            updateMonthText(); // 월 텍스트 업데이트
            initializeCalendar(); // 새로운 월 데이터로 초기화
        });

        // 초기 월 텍스트 설정
        updateMonthText();

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("UInterface", "User ID initialized: " + userId);
            loadChartDataFromFirestore(); // Firestore 데이터 불러오기
        } else {
            Log.e("UInterface", "User is not logged in.");
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
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

        binding.challengeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(challengeCheck) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage("챌린지를 포기하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    challengeSet.setClickable(true);
                                    challengeSetText.setText("챌린지를 선택하세요");
                                    challengeDetails.setText("9999-12-31 ~ 9999-12-31");
                                    challengeCheck = true;
                                    updateChart(2);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .create();
                    dialog.show();
                    challengeCheck=false;
                }
            }
        });

        binding.challengeSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                if(challengeCheck) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage("챌린지에 성공하셨습니까?")
                            .setPositiveButton("성공", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    challengeSet.setClickable(true);
                                    challengeSetText.setText("챌린지를 선택하세요");
                                    challengeDetails.setText("9999-12-31 ~ 9999-12-31");
                                    challengeCheck = true;
                                    updateChart(0);

                                }
                            })
                            .setNegativeButton("실패", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    challengeSet.setClickable(true);
                                    challengeSetText.setText("챌린지를 선택하세요");
                                    challengeDetails.setText("9999-12-31 ~ 9999-12-31");
                                    challengeCheck = true;
                                    updateChart(1);
                                }
                            })
                            .create();
                    dialog.show();
                    challengeCheck=false;
                }
            }
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

    // 성공, 실패, 포기 횟수가 늘어날 때마다 카운트해서 업데이트
    private void updateChart(int x) {
        if (x == 0) {
            challengeSuccessCount++;
        } else if (x == 1) {
            challengeFailCount++;
        } else if (x == 2) {
            challengeFloatingCount++;
        }

        // 파이 차트 데이터 업데이트
        pieEntries.clear(); // 기존 데이터 지우기
        pieEntries.add(new PieEntry(challengeSuccessCount, "성공"));
        pieEntries.add(new PieEntry(challengeFailCount, "실패"));
        pieEntries.add(new PieEntry(challengeFloatingCount, "포기"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "결과");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); // 설명 제거
        pieChart.invalidate(); // 차트 갱신 (화면 다시 그리기)
        pieChart.animateY(1000); // 애니메이션 추가

        // Firestore에 저장 (새 데이터만 저장)
        if (x != -1) {
            saveChartDataToFirestore();
        }
    }



    private void saveChartDataToFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot save chart data.");
            return;
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("successCount", challengeSuccessCount);
        chartData.put("failCount", challengeFailCount);
        chartData.put("floatingCount", challengeFloatingCount);

        Log.d("UInterface", "Saving chartData: " + chartData);

        db.collection("users").document(userId).collection("challenges").document("chartData")
                .set(chartData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UInterface", "Chart data saved successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Error saving chart data: " + e.getMessage());
                });
    }


    private void loadChartDataFromFirestore() {
        if (userId == null) {
            Log.e("UInterface", "User ID is null, cannot load chart data.");
            return;
        }

        db.collection("users").document(userId).collection("challenges").document("chartData")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Firestore 값 불러오기
                        int loadedSuccessCount = documentSnapshot.getLong("successCount").intValue();
                        int loadedFailCount = documentSnapshot.getLong("failCount").intValue();
                        int loadedFloatingCount = documentSnapshot.getLong("floatingCount").intValue();

                        // 불러온 값 누적 (현재 값에 더하기)
                        challengeSuccessCount += loadedSuccessCount;
                        challengeFailCount += loadedFailCount;
                        challengeFloatingCount += loadedFloatingCount;

                        Log.d("UInterface", "Loaded chartData: success=" + challengeSuccessCount +
                                ", fail=" + challengeFailCount + ", floating=" + challengeFloatingCount);

                        updateChart(-1); // UI 갱신
                    } else {
                        Log.d("UInterface", "No existing chart data found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("UInterface", "Error loading chart data: " + e.getMessage()));
    }




    private void openChallengeDialog() {
        ChallengeSettingDialog dialog = new ChallengeSettingDialog();
        dialog.setOnChallengeDataListener((challenge, date) -> {
            // 데이터 수신 후 화면에 반영
            challengeSetText.setText(challenge);
            challengeDetails.setText("기간: " + date);
            challengeSet.setClickable(false);
        });
        dialog.show(getParentFragmentManager(), "ChallengeSettingDialog");
    }

    /**
     * 캘린더 데이터를 초기화합니다.
     */
    private void initializeCalendar() {
        dayList.clear(); // 기존 데이터 초기화

        // 현재 월과 연도 가져오기
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.DAY_OF_MONTH, 1); // 첫 번째 날로 설정
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 요일 (일요일: 0)
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 빈 칸 추가
        for (int i = 0; i < firstDayOfWeek; i++) {
            dayList.add(new DayModel("", new ArrayList<>(), false));
        }

        // 실제 날짜 추가
        for (int day = 1; day <= daysInMonth; day++) {
            boolean isToday = (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    && currentMonth == Calendar.getInstance().get(Calendar.MONTH)
                    && currentYear == Calendar.getInstance().get(Calendar.YEAR));
            dayList.add(new DayModel(String.format("%d-%02d-%02d", currentYear, currentMonth + 1, day), new ArrayList<>(), isToday));
        }

        adapter.notifyDataSetChanged(); // 어댑터 갱신
    }



    // 연도와 월 표시
    private void updateMonthText() {
        String monthYear = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
                .format(calendar.getTime()); // "December 2024" 형식
        binding.currentMonthText.setText(monthYear);
    }

    /**
     * 성공 또는 실패 상태를 업데이트합니다.
     */
    private void updateDateStatus(String status) {
        boolean updated = false;

        for (DayModel day : dayList) {
            if (day.isToday()) { // 오늘 날짜인지 확인
                day.setStatus(status); // 상태 업데이트
                updated = true;
                break;
            }
        }

        if (updated) {
            adapter.notifyDataSetChanged(); // RecyclerView 갱신
            Toast.makeText(requireContext(), "오늘 날짜에 " + (status.equals("success") ? "성공" : "실패") + "로 표시되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "오늘 날짜를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
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

            // 오늘 날짜에만 상태를 표시
            if (day.isToday()) {
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
                holder.statusImage.setVisibility(View.GONE); // 오늘이 아닌 날짜는 상태 숨김
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