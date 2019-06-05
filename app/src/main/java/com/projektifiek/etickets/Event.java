package com.projektifiek.etickets;

public class Event {

    private int Id;
    private String Title, Author, DateCreated, Content, PhotoURL;

    public Event(int id, String title, String author, String dateCreated, String content, String photoURL) {
        Id = id;
        Title = title;
        Author = author;
        DateCreated = dateCreated;
        Content = content;
        PhotoURL = photoURL;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        PhotoURL = photoURL;
    }
}
