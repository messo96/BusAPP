package com.example.busapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.busapp.Utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

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
        getSupportActionBar().setTitle(R.string.app_name);


      // PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
             //  .edit()
             // .putBoolean("logged", false)
             //.apply();

        //Map as default
        Utilities.insertFragment(activity, new MapsHome(), "MapsHome", R.id.fragment_container_view);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {



                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                        startActivity(intent);
                        trans = false;
                        break;
                    case R.id.navigation_maps:
                        selectedFragment = new MapsHome();
                        nameFragment = "MapsHome";
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = new ProfileFragment(true, getSupportActionBar());
                        nameFragment = "Profile";
                        break;
                }

                if(trans){
                        Utilities.insertFragment(activity, selectedFragment, nameFragment, R.id.fragment_container_view);
                }


                return true;

            }
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //nothing
            }
        });

    }

    @Override
    public void onBackPressed() {

        bottomNavigationView.getMenu().findItem(R.id.navigation_maps).setChecked(true);
        super.onBackPressed();

    }
}