package com.example.busapp.database;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.BusStop.BusStop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class DbConnector {
/*
                                                                         /* User */
/*
    public Task<DocumentReference> addUser(final User user) {
        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("password", user.getPassword());

        return this.db.collection("User").add(map);
    }

    public boolean login(final String email, final String password) {
        boolean check = false;
        Task<QuerySnapshot> tResult = db.collection("User").get();
        while(!tResult.isComplete()){}  //Bruttissimo, cambia appena puoi !!

        for(QueryDocumentSnapshot query : tResult.getResult()){
            if (email.matches(query.getData().get("email").toString())
                    && password.matches(query.getData().get("password").toString())) {
                check = true;
                break;
            }

        }
        return check;


        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot query : task.getResult()) {
                        if (email.matches(query.getData().get("email").toString())
                                && password.matches(query.getData().get("password").toString())) {
                            check[0] = true;
                            break;
                        }
                    }
                }
            }
        });
        return check[0];

    }


                                                                                             /* Bus Stop */

/*

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<BusStop> getBusStops(){
        List<BusStop> list = new ArrayList<>();
        Task<QuerySnapshot> tResult =  db.collection("BusStop").get();
        while(!tResult.isComplete()){}

        for (QueryDocumentSnapshot q : tResult.getResult()) {
             String coord = String.valueOf(q.get("position"));
            list.add(new BusStop(
                    Integer.parseInt(String.valueOf(q.get("user_created_id"))),
                    String.valueOf(q.get("name")),
                    Coordinates.getCoordinatesFromString(coord),
                    Base64.getDecoder().decode(String.valueOf(q.get("image"))) )
            );
        }

        return list;
    }

*/

}
