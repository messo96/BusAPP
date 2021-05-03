package com.example.busapp.database.Bus;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.busapp.Utils.Week;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.User;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {@ForeignKey(entity = BusStop.class,
        parentColumns   = "bus_stop_id",
        childColumns    = "id_busStop", onDelete = CASCADE) })
public class Bus {
    @PrimaryKey(autoGenerate = true)
    private int id_bus;

    private String number;
    @Embedded
    private Week calendar;

    private int id_busStop;


    public Bus(String number, Week calendar, int id_busStop) {
        this.number = number;
        this.calendar = calendar;
        this.id_busStop = id_busStop;
    }


    public void setId_bus(int id_bus) {
        this.id_bus = id_bus;
    }

    public int getId_bus() {
        return id_bus;
    }

    public String getNumber() {
        return number;
    }

    public Week getCalendar() {
        return calendar;
    }

    public int getId_busStop() {
        return id_busStop;
    }
}


