package com.example.userinterface;

public class BookItem {
    private String bookimage;
    private String title;
    private String author;
    private String description;
    private String pubDate;
    private String publisher;
    private String isbn;
    public BookItem(String title, String author, String description, String bookimage, String pubDate, String publisher, String isbn){
        this.title = title;
        this.author = author;
        this.description = description;
        this.bookimage = bookimage;
        this.pubDate = pubDate;
        this.publisher = publisher;
        this.isbn = isbn;
    }
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
    public String getIsbn(){
        return isbn;
    }
    public void setIsbn(String isbn){
        this.isbn = isbn;
    }
}
