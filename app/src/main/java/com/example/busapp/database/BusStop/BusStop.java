package com.example.busapp.database.BusStop;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.User;

@Entity(foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns   = "user_id",
        childColumns    = "user_created_id") })
public class BusStop {
    @PrimaryKey(autoGenerate = true)
    int bus_stop_id;

    private int user_created_id;
    private String name;
    @Embedded private Coordinates position;


    public BusStop(int user_created_id, String name, Coordinates position) {
        this.user_created_id = user_created_id;
        this.name = name;
        this.position = position;
    }


    public void setBus_stop_id(int bus_stop_id) {
        this.bus_stop_id = bus_stop_id;
    }

    public int getBus_stop_id() {
        return bus_stop_id;
    }

    public int getUser_created_id() {
        return user_created_id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getPosition() {
        return position;
    }
}
