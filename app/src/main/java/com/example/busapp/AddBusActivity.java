package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.busapp.Utils.Utilities;

public class AddBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new AddBusFragment(), "AddBus", R.id.fragment_container_view);
        }
    }

}
