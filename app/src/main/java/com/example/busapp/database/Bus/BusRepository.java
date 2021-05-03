package com.example.busapp.database.Bus;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.busapp.Utils.Day;
import com.example.busapp.database.BusStop.BusStopDatabase;

import java.util.List;

public class BusRepository {
    private BusDAO busDAO;

    public BusRepository(Application application){
        BusStopDatabase busStopDatabase = BusStopDatabase.getDatabase(application);
        busDAO = busStopDatabase.busDAO();
    }

    public LiveData<List<BusSimple>> getBus(final int id_busStop){
        return busDAO.getBus(id_busStop);
    }

    public LiveData<List<String>> getHours(final int id_busStop, final String number_bus, String day){ return busDAO.getHourOfBusInDay(id_busStop, number_bus, day); };


    public void addBus(final Bus bus){
        BusStopDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                busDAO.addBus(bus);
            }
        });
    }

}
