package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

public class AddTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_time);

        if(savedInstanceState == null){

            Utilities.insertFragment(this, new AddTimeFragment(), "AddBusTime", R.id.fragment_container_view);

        }
    }

}
