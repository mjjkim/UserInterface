package com.example.userinterface;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.userinterface.databinding.ActivityAlarmBinding;

public class AlarmActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private Button toggleNotificationButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAlarmBinding binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toggleNotificationButton = binding.toggleNotificationButton;
        updateButtonText();

        toggleNotificationButton.setOnClickListener(v -> toggleNotificationPermission());
    }

    private void updateButtonText() {
        boolean isEnabled = areNotificationsEnabled();
        toggleNotificationButton.setText(isEnabled ? "알림 비활성화" : "알림 활성화");
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void toggleNotificationPermission() {
        if (areNotificationsEnabled()) {
            // 알림이 활성화된 경우, 설정 화면으로 이동하여 비활성화하도록 안내
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림 비활성화")
                    .setMessage("알림을 비활성화하려면 설정 화면으로 이동해야 합니다. 계속하시겠습니까?")
                    .setPositiveButton("설정으로 이동", (dialog, which) -> openNotificationSettings())
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            // 알림이 비활성화된 경우, 권한 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                openNotificationSettings();
            }
        }
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
            updateButtonText();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonText();
    }
}
