package com.example.busapp.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserRepository {
    private UserDAO userDAO;

    private LiveData<List<User>> userList;

    public UserRepository(Application application){
        UserDatabase userDatabase = UserDatabase.getDatabase(application);

        userDAO = userDatabase.userDAO();
        userList = userDAO.getAll();
    }

    public LiveData<List<User>> getUserList(){
        return userDAO.getAll();
    }


    public void addUser(final User user){
        UserDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.addUser(user);
            }
        });
    }


    public LiveData<User> getUserFromUsername(final String username){
        return userDAO.getUserFromUsername(username);
    }


    public void deleteAll(){
        UserDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.deleteAll();
            }
        });
    }
}
