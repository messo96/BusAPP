package com.example.busapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.example.busapp.BusStopActivity;
import com.example.busapp.R;

import com.example.busapp.database.BusStop.BusStop;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;



public class BusStopInfoWindows extends InfoWindow {
    SharedPreferences sharedPreferences;
    BusStop busStop;
    Activity activity;
    MapView map;
    FirebaseFirestore db;
    private int fixOpenClose = 0;

    public BusStopInfoWindows(Activity activity, int idRes, MapView map, BusStop busStop){
        super(idRes, map);
        this.busStop = busStop;
        this.activity = activity;
        this.map = map;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        db = FirebaseFirestore.getInstance();
        setUp();

    }



    private void setUp() {
        ImageView imageView = getView().findViewById(R.id.image_busStop);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(busStop.getImage(), 0, busStop.getImage().length));
        TextView textView = getView().findViewById(R.id.name_busStop);
        TextView textView_list_bus = getView().findViewById(R.id.list_bus);
        TextView textView_creator = getView().findViewById(R.id.text_created_marker);
        textView.setText(busStop.getName());


        db.collection("Bus").whereEqualTo("busStop_id", busStop.getBus_stop_id()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
               addListToInfoWindows(queryDocumentSnapshots, textView_list_bus);

        }).addOnFailureListener(f -> textView_list_bus.setText("Something gone wrong, retry"));


        /*
         * Update list bus
         */

        db.collection("Bus")
                .whereEqualTo("busStop_id", busStop.getBus_stop_id())
                .addSnapshotListener((value,error) -> {
                    addListToInfoWindows(value, textView_list_bus);

                });


        db.collection("User")
                .whereEqualTo("id", busStop.getUser_created_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    textView_creator.setText(textView_creator.getText() + String.valueOf(q.get("username")));
                }
        }).addOnFailureListener(f -> textView_creator.setText("") );



        getView().findViewById(R.id.btn_see_bus).setOnClickListener(e -> {
            int id_creator = sharedPreferences.getInt("id", -1);
            Intent intent = new Intent(activity.getApplicationContext(), BusStopActivity.class);
            intent.putExtra("busStop_id", busStop.getBus_stop_id());
            intent.putExtra("id", id_creator);
            intent.putExtra("name_busStop", busStop.getName());

            activity.startActivity(intent);
        });
    }




    @Override
    public void onOpen(Object item) {
        InfoWindow.closeAllInfoWindowsOn(map);
        if(fixOpenClose == 2){
            this.getView().setVisibility(View.VISIBLE);
            fixOpenClose = 0;
        }
    }


    @Override
    public void onClose() {
        this.getView().setVisibility(View.INVISIBLE);
        fixOpenClose++;
    }


    private void addListToInfoWindows(QuerySnapshot queryDocumentSnapshots, TextView textView_list_bus) {
        textView_list_bus.setText("");
        for(QueryDocumentSnapshot q : queryDocumentSnapshots) {
            String s = String.valueOf(textView_list_bus.getText());
            textView_list_bus.setText(s + q.get("name_bus") + " | ");
        }
    }
}
