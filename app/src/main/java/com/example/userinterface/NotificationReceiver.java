package com.example.userinterface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_notification_channel";
    private static final String CHANNEL_ID2 = "challenge_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");

        Log.d("UInterface", "onReceive 실행됨. Type: " + type);

        if ("daily_phrase".equals(type)) {
            sendDailyPhraseNotification(context);
        } else if ("challenge".equals(type)) {
            sendChallengeNotification(context);
        } else {
            Log.e("UInterface", "알림 타입이 설정되지 않았습니다.");
        }
    }

    // 랜덤 문구 알림
    private void sendDailyPhraseNotification(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId;

        // FirebaseAuth에서 현재 로그인된 사용자 가져오기
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Log.e("UInterface", "사용자가 로그인되지 않았습니다.");
            sendNotification(context, "로그인이 필요합니다. 앱을 실행해 주세요.", CHANNEL_ID, 1001, "Daily Notification");
            return; // 메서드 종료
        }

        db.collection("users")
                .document(userId)
                .collection("phrases")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> phrases = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        if (document.contains("phrase")) {
                            String phrase = document.getString("phrase");
                            if (phrase != null && !phrase.isEmpty()) {
                                phrases.add(phrase);
                            }
                        }
                    }

                    String randomPhrase = "오늘 읽은 한 문장이 당신의 생각을 바꿀지 모릅니다.";
                    if (!phrases.isEmpty()) {
                        Random random = new Random();
                        randomPhrase = phrases.get(random.nextInt(phrases.size()));
                    }

                    sendNotification(context, randomPhrase, CHANNEL_ID, 1001, "Daily Notification");
                })
                .addOnFailureListener(e -> {
                    Log.e("UInterface", "Firestore 데이터 가져오기 실패: " + e.getMessage());
                    sendNotification(context, "오늘 읽은 한 문장이 당신의 생각을 바꿀지 모릅니다.", CHANNEL_ID, 1001, "Daily Notification");
                });
    }


    // 챌린지 알림
    private void sendChallengeNotification(Context context) {
        sendNotification(context, "오늘 한 장이라도 더 읽으면, 내일의 내가 달라집니다. 독서 목표를 달성해보세요!", CHANNEL_ID2, 1002, "Challenge Notification");
    }

    private void sendNotification(Context context, String contentText, String channelId, int notificationId, String channelName) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent activityIntent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Book Connect")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
        Log.d("UInterface", "Notification sent: " + contentText);
    }
}


