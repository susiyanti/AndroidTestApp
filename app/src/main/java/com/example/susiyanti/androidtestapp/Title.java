package com.example.susiyanti.androidtestapp;

import java.io.Serializable;

/**
 * Created by susiyanti on 6/24/16.
 */
public class Title implements Serializable{
    private String title;
    private String body;
    private int userId;
    private int id;

    public Title() {
    }

    public Title(String title, String body, int userId, int id) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Title{" +
                "title='" + title + '\'' +
                ", id=" + id +
                '}';
    }
}
