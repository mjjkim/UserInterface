package com.example.userinterface;

public class BookItem {
    private String bookimage;
    private String title;
    private String author;
    private String description;
    public BookItem(String title, String author, String description){
        this(title, author, description, null);
    }
    public BookItem(String title, String author, String description, String bookimage){
        this.bookimage = bookimage;
        this.title = title;
        this.author = author;
        this.description = description;
    }
    public String getBbbimage(){
        return bookimage;
    }
    public void setBookimage(String bookimage){
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
}
