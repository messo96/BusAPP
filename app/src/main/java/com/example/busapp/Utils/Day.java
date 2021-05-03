package com.example.busapp.Utils;

public enum Day{
    Lunedì("Lunedì", 0),
    Martedì("Martedì", 1),
    Mercoledì("Mercoledì", 2),
    Giovedì("Giovedì", 3),
    Venerdì("Venerdì", 4),
    Sabato("Sabato", 5),
    Domenica("Domenica", 6),
    Undefined("Undefined", 7);

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
        return Day.Undefined;
    }

}
