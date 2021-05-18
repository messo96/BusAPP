package com.example.busapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;


import com.example.busapp.Utils.Utilities;
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


         //FOR DEBUG, EVERY TIME YOU OPEN THE APP YOU HAVE TO LOGIN
      // PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
             //  .edit()
             // .putBoolean("logged", false)
             //.apply();


        //Map as default
        Utilities.insertFragment(activity, new MapsHome(), "MapsHome", R.id.fragment_container_view);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                        startActivity(intent);
                        trans = false;
                        break;
                    case R.id.navigation_maps:
                        getSupportActionBar().setTitle(R.string.app_name);
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

    @Override
    public void onBackPressed() {

        bottomNavigationView.getMenu().findItem(R.id.navigation_maps).setChecked(true);
        getSupportActionBar().setTitle(R.string.app_name);
        super.onBackPressed();

    }
}