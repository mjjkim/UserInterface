package com.example.userinterface;

public class MessageBoardItem {
    private String title;
    private String author;
    private String cover;
    private boolean isLiked;
    private String review;

    public MessageBoardItem() {

    }

    public  MessageBoardItem(String title, String author, String cover, boolean isLiked, String review){
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.isLiked = isLiked;
        this.review = review;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean reviewed) { isLiked = reviewed; }

    public String getReview(){ return review; }
    public void setReview(String review){ this.review = review; }
}
