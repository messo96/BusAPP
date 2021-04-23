package com.example.busapp.database.BusStop;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.busapp.database.User;
import com.example.busapp.database.UserDAO;
import com.example.busapp.database.UserDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {BusStop.class, User.class}, version = 1)
public abstract class BusStopDatabase extends RoomDatabase {

        public abstract BusStopDAO busStopDAO();
        private static volatile BusStopDatabase INSTANCE;
        private static final int NUMBER_OF_THREADS = 4;

        static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        static BusStopDatabase getDatabase(final Context context){
            if(INSTANCE == null){
                synchronized (BusStopDatabase.class){
                    if(INSTANCE == null){
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BusStopDatabase.class, "busStop_database").fallbackToDestructiveMigration().build();
                    }
                }
            }
            return INSTANCE;
        }

}
