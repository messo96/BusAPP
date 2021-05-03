package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

public class BusStopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new ListBusFragment(), "Bus_activity", R.id.fragment_list_bus);
        }
    }

}