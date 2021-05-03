package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

public class DetailBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_detail);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new DetailBusFragment(), "Bus_detail", R.id.fragment_bus);
        }
    }
}
