package com.example.busapp.database.Bus;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.busapp.database.BusStop.BusStopDatabase;

import java.util.List;

public class BusRepository {
    private BusDAO busDAO;

    public BusRepository(Application application){
        BusStopDatabase busStopDatabase = BusStopDatabase.getDatabase(application);
        busDAO = busStopDatabase.busDAO();
    }

    public LiveData<List<Bus>> getBus(final int id_busStop){
        return busDAO.getBus(id_busStop);
    }

    public void addBus(final Bus bus){
        BusStopDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                busDAO.addBus(bus);
            }
        });
    }

}
