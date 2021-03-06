package com.example.busapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addUser(User user);


    @Query("SELECT * FROM User")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM User WHERE username= :username")
    LiveData<User> getUserFromUsername(final String username);

    @Query("SELECT username FROM User WHERE user_id = :idUser")
    LiveData<String>  getUserFromIdUser(final int idUser);

    @Query("SELECT count(*) FROM User WHERE email = :email")
    LiveData<Integer> checkIfUserExist(final String email);

    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    LiveData<User> login(String email, String password);


    @Query("DELETE FROM User")
    void deleteAll();

}
