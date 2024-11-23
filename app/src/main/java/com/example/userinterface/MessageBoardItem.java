package com.example.userinterface;

public class MessageBoardItem {
    private String title;
    private String content;
    private String cover;
    private boolean isLiked;
    private String review;

    public  MessageBoardItem(String title, String content, String cover, boolean isLiked, String review){
        this.title = title;
        this.content = content;
        this.cover = cover;
        this.isLiked = isLiked;
        this.review = review;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCover() { return cover; }
    public void setImageResId(String cover) { this.cover = cover; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public String getReview(){ return this.review; }
    public void setReview(String review){ this.review = review; }
}
