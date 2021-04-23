package com.example.busapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.busapp.database.User;
import com.example.busapp.database.UserRepository;

import java.util.List;

public class ListViewModel extends AndroidViewModel{

        private LiveData<List<User>> userList;
        private UserRepository repository;

        public ListViewModel(@NonNull Application application) {
            super(application);
            repository = new UserRepository(application);
            userList = repository.getUserList();
        }

        public LiveData<List<User>> getAll() {
            return userList;
        }

        public LiveData<User> getUser(final String username){
            return repository.getUserFromUsername(username);
        }


}
