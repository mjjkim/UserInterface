package com.example.userinterface;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.userinterface.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 전역 예외 처리기 설정
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log.e("UInterface", "App crashed: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) {
            Toast.makeText(this, "Firebase 인증 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        // 회원 가입 클릭시 회원가입 창으로 이동
        binding.signUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        // 로그인 클릭 시 메인 창으로 이동
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = binding.idLogin.getText().toString();
                String password = binding.passwordLogin.getText().toString();

                Log.d("UInterface",
                        "ID: " + ID + ", Password: " + password);

                if (ID.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase 인증을 통한 로그인 처리
                mAuth.signInWithEmailAndPassword(ID, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 로그인 성공 시 메인 액티비티로 이동
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // 로그인 실패 시 에러 메시지 출력
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "알 수 없는 오류";
                                    Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    Log.e("UInterface", "Login failed: " + errorMessage);  // 로그에 실패 이유 출력
                                }
                            }
                        });
            }
        });

        checkNotificationPermission();
        setDailyAlarm();

    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                Toast.makeText(this, "알림이 허용되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                // 권한이 거부됨
                Toast.makeText(this, "알림이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setDailyAlarm() {
        // 랜덤 문구 알림 설정 (오후 12시)
        Calendar calendarPhrase = Calendar.getInstance();
        calendarPhrase.set(Calendar.HOUR_OF_DAY, 20); // 오후 12시
        calendarPhrase.set(Calendar.MINUTE, 35);
        calendarPhrase.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendarPhrase)) {
            calendarPhrase.add(Calendar.DATE, 1); // 시간이 지났으면 다음 날 예약
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent phraseIntent = new Intent(this, NotificationReceiver.class);
        phraseIntent.putExtra("type", "daily_phrase"); // type 구분

        PendingIntent phrasePendingIntent = PendingIntent.getBroadcast(
                this, 1001, phraseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendarPhrase.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                phrasePendingIntent
        );

        // 챌린지 알림 설정 (저녁 8시)
        Calendar calendarChallenge = Calendar.getInstance();
        calendarChallenge.set(Calendar.HOUR_OF_DAY, 20); // 저녁 8시
        calendarChallenge.set(Calendar.MINUTE, 35);
        calendarChallenge.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendarChallenge)) {
            calendarChallenge.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent challengeIntent = new Intent(this, NotificationReceiver.class);
        challengeIntent.putExtra("type", "challenge"); // type 구분

        PendingIntent challengePendingIntent = PendingIntent.getBroadcast(
                this, 1002, challengeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager2.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendarChallenge.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                challengePendingIntent
        );

        Log.d("UInterface", "알람 설정 완료: 랜덤 문구(12시), 챌린지(8시)");
    }
}