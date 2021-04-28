package com.example.busapp.Utils;

enum Day{Lunedì, Martedì, Mercoledì, Giovedì, Venerdì, Sabato, Domenica}

public class Week {
    private String day;
    private String hour;

    public Week(String day, String hour) {
        this.day = day.toString();
        this.hour = hour;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }
}
