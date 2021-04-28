package com.example.busapp.database.Bus;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.busapp.ListBus;
import com.example.busapp.database.BusStop.BusStop;

import java.util.List;

@Dao
public interface BusDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBus(Bus bus);

    @Query("SELECT B.* FROM BUS B, BUSSTOP S WHERE B.id_busStop = :id_busStop")
    LiveData<List<Bus>> getBus(final int id_busStop);


}
