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
        String title = (String) map.get("title");
        String author = (String) map.get("author");
        String description = (String) map.get("description");
        String bookimage = (String) map.get("bookimage");
        String pubDate = (String) map.get("pubDate");
        String publisher = (String) map.get("publisher");
        boolean isLiked = map.containsKey("isLiked") && (boolean) map.get("isLiked");

        BoardItem item = new BoardItem(title, author, description, bookimage, pubDate, publisher);
        item.setLiked(isLiked); // 좋아요 상태 설정
        return item;
    }

    // Firestore에 데이터를 저장할 때 사용할 메서드
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("author", author);
        map.put("description", description);
        map.put("bookimage", bookimage);
        map.put("pubDate", pubDate);
        map.put("publisher", publisher);
        map.put("isLiked", isLiked); // 좋아요 상태 포함
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
