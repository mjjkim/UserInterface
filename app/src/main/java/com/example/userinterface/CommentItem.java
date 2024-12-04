package com.example.userinterface;

public class CommentItem {
    String username;
    String comment;
    long time;
    String coverImage;
    public CommentItem(String username, String comment, long time, String coverImage){
        this.username = username;
        this.comment = comment;
        this.time = time;
        this.coverImage=coverImage;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public long getTime(){
        return time;
    }
    public void setTime(long time){
        this.time = time;
    }
    public String getCoverImage(){
        return coverImage;
    }
    public void setCoverImage(String coverImage){
        this.coverImage = coverImage;
    }
}
