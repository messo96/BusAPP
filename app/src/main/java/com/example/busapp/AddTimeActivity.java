package com.example.busapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

import java.util.Objects;

public class AddTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_time);

        if(savedInstanceState == null){
            Utilities.insertFragment(this, new AddTimeFragment(), "AddBusTime", R.id.fragment_container_view);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("name_busStop")
                                                                        + " - " + getIntent().getStringExtra("bus_name")
                                                                        + " - " + getIntent().getStringExtra("day"));
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
