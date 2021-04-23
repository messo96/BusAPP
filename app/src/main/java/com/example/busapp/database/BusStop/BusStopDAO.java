package com.example.busapp.database.BusStop;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BusStopDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBusStop(BusStop busStop);

    @Query("SELECT * FROM BusStop")
    LiveData<List<BusStop>> getAll();

    @Query("SELECT S.* FROM BusStop S, User U where S.user_created_id = :user_id")
    LiveData<List<BusStop>> getBusStop(final int user_id);


    @Query("DELETE FROM BusStop")
    void deleteAll();

}
