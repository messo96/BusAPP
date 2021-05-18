package com.example.busapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
            getSupportActionBar().setTitle(getIntent().getStringExtra("name_busStop"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         switch (item.getItemId()) {
             case android.R.id.home:
                 this.finish();
         }

         return true;
    }
}
