package com.example.busapp;

public class Gamification {
    private final String title;
    private final String description;
    private final Integer exp;

    public Gamification(String title, String description, Integer exp) {
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
