package com.example.busapp.database;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.busapp.Utils.Coordinates;


@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int user_id;

    private String username;
    private String email;
    private String password;
    @Embedded private Coordinates last_location;

    public User(String username, String email, String password, Coordinates last_location) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.last_location = last_location;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id(){
        return user_id;
    }


    public String getUsername() {
        return username;
    }

    public Coordinates getLast_location() {
        return last_location;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
