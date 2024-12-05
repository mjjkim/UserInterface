package com.example.userinterface;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.userinterface.databinding.FragmentChallengeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChallengeFragment extends Fragment {
    private FragmentChallengeBinding binding;
    private TextView challengeSet;
    private TextView challengeDetails;
    private TextView challengeSetText;
    private CalendarAdapter adapter;
    private List<DayModel> dayList = new ArrayList<>();
    private DayModel selectedDay;

    private Calendar calendar = Calendar.getInstance(); // 전역 캘린더

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

        // RecyclerView 설정
        adapter = new CalendarAdapter(dayList, this::onDateClicked);
        binding.calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7)); // 7열 (요일 기준)
        binding.calendarRecyclerView.setAdapter(adapter);

        // 캘린더 데이터 초기화
        initializeCalendar();

        // 성공 버튼 클릭 이벤트
        binding.successButton.setOnClickListener(v -> updateDateStatus("✅"));

        // 실패 버튼 클릭 이벤트
        binding.failureButton.setOnClickListener(v -> updateDateStatus("❌"));

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

        // 어댑터 갱신
        adapter.notifyDataSetChanged();
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
        if (selectedDay != null) {
            selectedDay.setStatus(status); // 선택된 날짜 상태 업데이트
            adapter.notifyDataSetChanged(); // RecyclerView 갱신
            Toast.makeText(requireContext(), selectedDay.getDate() + "에 " + status + "로 표시되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 날짜 클릭 이벤트
     */
    private void onDateClicked(DayModel day) {
        selectedDay = day; // 선택된 날짜 업데이트
        Toast.makeText(requireContext(), "선택된 날짜: " + day.getDate(), Toast.LENGTH_SHORT).show();
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

        public DayModel(String date, List<String> events, boolean isToday) {
            this.date = date;
            this.events = events;
            this.isToday = isToday;
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

        public void setEvents(List<String> events) {
            this.events = events;
        }

        public void setStatus(String status) {
            if (events == null) {
                events = new ArrayList<>();
            }
            events.clear(); // 기존 상태 제거
            events.add(status); // 새로운 상태 추가
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

            holder.itemView.setOnClickListener(v -> onDateClickListener.onDateClick(day));
        }

        @Override
        public int getItemCount() {
            return dayList.size();
        }

        public static class DayViewHolder extends RecyclerView.ViewHolder {
            private final TextView dayText;
            private final TextView eventText;

            public DayViewHolder(@NonNull View itemView) {
                super(itemView);
                dayText = itemView.findViewById(R.id.day_text);
                eventText = itemView.findViewById(R.id.event_text);
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