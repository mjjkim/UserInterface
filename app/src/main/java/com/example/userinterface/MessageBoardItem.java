package com.example.userinterface;

import java.util.HashMap;
import java.util.Map;

public class MessageBoardItem {
    private String postId;
    private String title;
    private String author;
    private String cover;
    private boolean isLiked;
    private String review;
    private String userId;
    private com.google.firebase.Timestamp timestamp;

    public MessageBoardItem(String postId, String title, String author, String cover, boolean isLiked, String review, String userId, com.google.firebase.Timestamp timestamp) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.isLiked = isLiked;
        this.review = review;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isLiked() {
        return isLiked;
    }
    public void setLiked(boolean reviewed) {
        isLiked = reviewed;
    }

    public String getReview(){
        return review;
    }
    public void setReview(String review){
        this.review = review;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter 및 Setter 추가
    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Firestore에 저장하기 위한 Map 변환
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("postId", this.postId);
        map.put("title", this.title);
        map.put("author", this.author);
        map.put("cover", this.cover);
        map.put("liked", this.isLiked);
        map.put("review", this.review);
        map.put("userId", this.userId);
        map.put("timestamp", this.timestamp);
        return map;
    }
}
