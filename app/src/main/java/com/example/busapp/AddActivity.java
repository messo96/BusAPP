package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.busapp.Utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new AddFragment(getSupportActionBar()), "Add", R.id.fragment_container_view);
        }
    }

}
