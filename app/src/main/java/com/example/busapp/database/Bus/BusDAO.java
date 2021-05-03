package com.example.busapp.database.Bus;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface BusDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBus(Bus bus);

    @Query("SELECT DISTINCT B.id_busStop, B.number, B.day FROM BUS B, BUSSTOP S WHERE B.id_busStop = :id_busStop")
    LiveData<List<BusSimple>> getBus(final int id_busStop);

    @Query("SELECT B.hour FROM BUS B WHERE B.id_busStop = :id_busStop AND B.number = :number_bus AND day= :day ORDER BY B.hour ASC")
    LiveData<List<String>> getHourOfBusInDay(final int id_busStop, final String number_bus, final String day);


}
