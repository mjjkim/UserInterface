package com.example.userinterface;

public class MessageBoardItem {
    private String title;
    private String content;
    private int imageResId;
    private boolean isLiked;

    public  MessageBoardItem(String title, String content, int imageResId, boolean isLiked){
        this.title = title;
        this.content = content;
        this.imageResId = imageResId;
        this.isLiked = isLiked;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}
