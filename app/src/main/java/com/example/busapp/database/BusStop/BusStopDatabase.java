package com.example.busapp.database.BusStop;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusDAO;
import com.example.busapp.database.User;
import com.example.busapp.database.UserDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {BusStop.class, User.class, Bus.class}, version = 3)
public abstract class BusStopDatabase extends RoomDatabase {

        public abstract UserDAO userDAO();
        public abstract BusStopDAO busStopDAO();
        public abstract BusDAO busDAO();
        private static volatile BusStopDatabase INSTANCE;
        private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BusStopDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (BusStopDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BusStopDatabase.class, "database_busstop").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
