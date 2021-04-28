package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AddBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        if(savedInstanceState == null){

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_view, new AddBusFragment(), "AddBus");
            transaction.addToBackStack("AddBus");
            transaction.commit();

        }
    }

}
