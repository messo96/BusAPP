package com.example.busapp.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.busapp.database.BusStop.BusStopDAO;
import com.example.busapp.database.BusStop.BusStopDatabase;

import java.util.List;

public class UserRepository {
    private UserDAO userDAO;

    private LiveData<List<User>> userList;

    public UserRepository(Application application){
        BusStopDatabase busStopDatabase = BusStopDatabase.getDatabase(application);

        userDAO = busStopDatabase.userDAO();
        userList = userDAO.getAll();
    }

    public LiveData<List<User>> getUserList(){
        return userDAO.getAll();
    }


    public void addUser(final User user){
        BusStopDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.addUser(user);
            }
        });
    }


    public LiveData<User> getUserFromUsername(final String username){
        return userDAO.getUserFromUsername(username);
    }

    public LiveData<String> getUserFromId(final int idUser) {return userDAO.getUserFromIdUser(idUser); }

    public LiveData<Integer> checkUser(final String email) { return userDAO.checkIfUserExist(email);}

    public LiveData<User> login(final String email, final String password) { return userDAO.login(email, password);}


    public void deleteAll(){
        BusStopDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.deleteAll();
            }
        });
    }
}
