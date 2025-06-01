package com.example.withpet;

public class Post {
    public String title;
    public String content;
    public String author;
    public long timestamp;
    public String nickname;
    public String imageUrl;
    public String key;

    public Post() {}

    public Post(String title, String content, String author, long timestamp, String imageUrl) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }
}





