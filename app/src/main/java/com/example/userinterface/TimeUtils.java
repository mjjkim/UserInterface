package com.example.userinterface;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "방금 전";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            return (diff / TimeUnit.MINUTES.toMillis(1)) + "분 전";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            return (diff / TimeUnit.HOURS.toMillis(1)) + "시간 전";
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            return (diff / TimeUnit.DAYS.toMillis(1)) + "일 전";
        } else {
            return "오래 전";
        }
    }
}
