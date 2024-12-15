package com.example.userinterface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_notification_channel";
    private static final String CHANNEL_ID2 = "challenge_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel 설정 (API 26 이상 필요)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID2,
                    "Challenge Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel2);
        }

        // 알림 클릭 시 실행될 Activity 설정
        Intent activityIntent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Notification 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Book Connect")
                .setContentText("오늘의 독서 목표를 아직 달성하지 않으셨네요. 지금 바로 앱을 열고 목표를 향해 한 걸음 더 나아가세요!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());

        Intent challengeIntent = new Intent(context, LoginActivity.class);
        PendingIntent challengePPendingIntent = PendingIntent.getActivity(
                context, 1, challengeIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(context, CHANNEL_ID2)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Book Connect")
                .setContentText("챌린지를 도전해보세요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(challengePPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1002, builder2.build());

        Log.d("NotificationReceiver", "Alarm triggered!");
    }
}
