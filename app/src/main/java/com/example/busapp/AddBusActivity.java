package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

import java.util.Objects;

public class AddBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_add_bus);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new AddBusFragment(), "AddBus", R.id.fragment_container_view);
        }
    }

}
