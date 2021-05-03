package com.example.busapp.database.Bus;

public class BusSimple {
    private int id_busStop;
    private String number;
    private String day;

    public BusSimple(int id_busStop, String number, String day) {
        this.id_busStop = id_busStop;
        this.number = number;
        this.day = day;
    }

    public String getNumber() {
        return number;
    }

    public int getId_busStop() {
        return id_busStop;
    }

    public String getDay() {
        return day;
    }

}
