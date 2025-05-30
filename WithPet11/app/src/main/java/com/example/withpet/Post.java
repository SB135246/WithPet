package com.example.withpet;

public class Post {
    private String title;
    private String content;
    private String author;
    private long timestamp;
    private String nickname;
    private String imageUrl;
    private String key;

    public Post() {}

    // 주요 필드 초기화용 생성자
    public Post(String title, String content, String author, long timestamp, String imageUrl) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}






