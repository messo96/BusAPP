package com.example.busapp;

public class Gramification {
    private String title;
    private String description;
    private Integer exp;

    public Gramification(String title, String description, Integer exp) {
        this.title = title;
        this.description = description;
        this.exp = exp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getExp() {
        return exp;
    }
}
