package com.example.userinterface;

public class BoardItem {
    private String bookimage;
    private String title;
    private String author;
    private String description;
    private String pubDate;
    private String publisher;
    // 좋아요 상태 추가
    private boolean isLiked;

    public BoardItem(String title, String author, String description, String bookimage, String pubDate, String publisher){
        this.title = title;
        this.author = author;
        this.description = description;
        this.bookimage = bookimage;
        this.pubDate = pubDate;
        this.publisher = publisher;
        // 기본값
        this.isLiked = false;
    }

    // Getter 및 Setter
    public String getBookImage(){
        return bookimage;
    }
    public void setBookImage(String bookimage){
        this.bookimage = bookimage;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getPubDate(){
        return pubDate;
    }
    public void setPubDate(String pubDate){
        this.pubDate = pubDate;
    }
    public String getPublisher(){
        return publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }
    public boolean isLiked() {
        return isLiked;
    }
    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
