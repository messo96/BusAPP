package com.example.busapp.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.busapp.AddFragment;
import com.example.busapp.DetailBusFragment;
import com.example.busapp.ListBusFragment;



public class Utilities {

    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag, int idContainer) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container_view with this fragment,
        transaction.replace(idContainer, fragment, tag);

        //add the transaction to the back stack so the user can navigate back
        if( !(fragment instanceof AddFragment) && !(fragment instanceof ListBusFragment) && !(fragment instanceof DetailBusFragment) )
           transaction.addToBackStack(tag);


        // Commit the transaction
        transaction.commit();
    }

}