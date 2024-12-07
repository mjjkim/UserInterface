package com.example.userinterface;

import java.util.HashMap;
import java.util.Map;

public class BoardItem {
    private String bookimage;
    private String title;
    private String author;
    private String description;
    private String pubDate;
    private String publisher;
    private boolean isLiked; // 좋아요 상태 추가

    // 생성자
    public BoardItem(String title, String author, String description, String bookimage, String pubDate, String publisher) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.bookimage = bookimage;
        this.pubDate = pubDate;
        this.publisher = publisher;
        this.isLiked = false; // 기본값
    }

    // Firestore에서 데이터를 불러올 때 사용할 정적 메서드
    public static BoardItem fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        // 공통 필드: 제목과 저자
        String title = map.get("title") != null ? map.get("title").toString() : "";
        String author = map.get("author") != null ? map.get("author").toString() : "";
        String description = "";
        String bookimage = "";
        String pubDate = "";
        String publisher = "";

        // 기록 데이터인지 글귀 데이터인지 분기 처리
        if (map.containsKey("description") && map.containsKey("bookimage")) {
            description = map.get("description") != null ? map.get("description").toString() : "";
            bookimage = map.get("bookimage") != null ? map.get("bookimage").toString() : "";
            pubDate = map.get("pubDate") != null ? map.get("pubDate").toString() : "";
            publisher = map.get("publisher") != null ? map.get("publisher").toString() : "";
        } else {
            description = map.get("phrase") != null ? map.get("phrase").toString() : "";
            bookimage = map.get("cover") != null ? map.get("cover").toString() : "";
            pubDate = map.get("feel") != null ? map.get("feel").toString() : "";
        }

        boolean isLiked = map.containsKey("isLiked") && Boolean.TRUE.equals(map.get("isLiked"));

        BoardItem item = new BoardItem(title, author, description, bookimage, pubDate, publisher);
        item.setLiked(isLiked);
        return item;
    }


    // Firestore에 데이터를 저장할 때 사용할 메서드
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("author", author);
        // 글귀 내용 저장
        map.put("description", description); // description으로 통일
        // 이미지 URL 저장
        map.put("bookimage", bookimage);     // bookimage로 통일
        // 메모 느낌 저장
        map.put("pubDate", pubDate);         // pubDate로 통일
        map.put("publisher", publisher);
        // 좋아요 상태 포함
        map.put("isLiked", isLiked);
        return map;
    }


    // Getter 및 Setter
    public String getBookImage() {
        return bookimage;
    }

    public void setBookImage(String bookimage) {
        this.bookimage = bookimage;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
