package com.example.busapp.database.BusStop;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.busapp.database.User;
import com.example.busapp.database.UserDAO;
import com.example.busapp.database.UserDatabase;

import java.util.List;

public class BusStopRepository {

    private BusStopDAO busStopDAO;

    private LiveData<List<BusStop>> busStopList;

    public BusStopRepository(Application application){
        BusStopDatabase busStopDatabase = BusStopDatabase.getDatabase(application);
        busStopDAO = busStopDatabase.busStopDAO();
        busStopList = busStopDAO.getAll();
    }

    public LiveData<List<BusStop>> getAll(){
        return busStopList;
    }


    public void addBusStop(final BusStop busStop){
        BusStopDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                busStopDAO.addBusStop(busStop);
            }
        });
    }


}
