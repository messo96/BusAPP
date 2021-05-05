package com.example.busapp.Utils;

public class Coordinates{
    public double longitudine;
    public double latitudine;

    public Coordinates(double latitudine, double longitudine) {
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }


    @Override
    public String toString() {
        return latitudine +";"+longitudine;
    }

    public static Coordinates getCoordinatesFromString(String str){
        return new Coordinates(Double.parseDouble(str.split(";",2)[0]), Double.parseDouble(str.split(";",2)[1]));
    }
}
