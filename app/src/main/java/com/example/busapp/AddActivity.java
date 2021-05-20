package com.example.busapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Utilities;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if(savedInstanceState == null){
            Utilities.insertFragment(this, new AddFragment(getSupportActionBar()), "Add", R.id.fragment_container_view);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
