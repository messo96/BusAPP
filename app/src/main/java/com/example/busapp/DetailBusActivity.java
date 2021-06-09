package com.example.busapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

import java.util.Objects;

public class DetailBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_detail);

        if(savedInstanceState == null) {
            Utilities.insertFragment(this, new DetailBusFragment(), "Bus_detail", R.id.fragment_bus);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("name_busStop") + " - " + getIntent().getStringExtra("bus_name"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return true;
    }
}
