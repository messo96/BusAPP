package com.example.busapp.database.BusStop;

import android.graphics.Bitmap;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.User;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns   = "user_id",
        childColumns    = "user_created_id", onDelete = CASCADE) })
public class BusStop {
    @PrimaryKey(autoGenerate = true)
    int bus_stop_id;


    private int user_created_id;
    private String name;
    @Embedded private Coordinates position;
    private byte[] image;

    public BusStop(int bus_stop_id, int user_created_id, String name, Coordinates position, byte[] image) {
        this.bus_stop_id = bus_stop_id;
        this.user_created_id = user_created_id;
        this.name = name;
        this.position = position;
        this.image = image;
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


    public byte[] getImage() {
        return image;
    }
}
