package com.example.busapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.example.busapp.Utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;
    private String nameFragment = "";
    private boolean trans = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        AppCompatActivity activity = this;
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        getSupportActionBar().hide();


         //DEBUG ONLY, EVERY TIME YOU OPEN THE APP YOU HAVE TO LOGIN
      // PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
             //  .edit()
             // .putBoolean("logged", false)
             //.apply();


        //Map as default
        Utilities.insertFragment(activity, new MapsHome(), "MapsHome", R.id.fragment_container_view);

        FloatingActionButton fab_add = findViewById(R.id.fab_add_busStop);
        fab_add.setOnClickListener(l -> addBusStop() );

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        addBusStop();

                        trans = false;
                        break;
                    case R.id.navigation_maps:
                        getSupportActionBar().setTitle(R.string.app_name);
                        selectedFragment = new MapsHome();
                        getSupportActionBar().hide();
                        nameFragment = "MapsHome";
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = new ProfileFragment(true, getSupportActionBar());
                        nameFragment = "Profile";
                        break;
                    default:
                        Log.d("INFO", "Unexpected value: " + item.getItemId());
                }

                if(trans){
                        Utilities.insertFragment(activity, selectedFragment, nameFragment, R.id.fragment_container_view);
                        return true;
                }
                else{
                    trans = true;
                    return false;
                }




        });

        bottomNavigationView.setOnNavigationItemReselectedListener(r ->  {
                //nothing
        });

    }

    private void addBusStop() {
        Intent intent = new Intent(getApplicationContext(), AddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        bottomNavigationView.getMenu().findItem(R.id.navigation_maps).setChecked(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        super.onBackPressed();
    }
}