package com.example.busapp.Utils;

public enum Day{
    All("All", 0),
    Lunedì("Lunedì", 1),
    Martedì("Martedì", 2),
    Mercoledì("Mercoledì", 3),
    Giovedì("Giovedì", 4),
    Venerdì("Venerdì", 5),
    Sabato("Sabato", 6),
    Domenica("Domenica", 7);

    private int index;
    private String name;

    Day(final String name, final int index){
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static Day getDay(final int index){
        for(Day d : Day.values()){
            if(d.getIndex() == index)
                return d;
        }
        return Day.Domenica;
    }

}
