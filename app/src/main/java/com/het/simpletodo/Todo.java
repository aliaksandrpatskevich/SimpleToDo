package com.het.simpletodo;

public class Todo {
    public String id;
    public String text;
    public String date;
    public String comments;
    public String level;

    public Todo() {
    }

    public Todo(String text, String date, String comments,String level) {
        this.text = text;
        this.date = date;
        this.comments = comments;
        this.level = level;
    }
}
