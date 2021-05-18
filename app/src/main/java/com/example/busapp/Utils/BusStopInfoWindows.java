package com.example.busapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.example.busapp.BusStopActivity;
import com.example.busapp.R;

import com.example.busapp.database.BusStop.BusStop;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;



public class BusStopInfoWindows extends InfoWindow {
    SharedPreferences sharedPreferences;
    BusStop busStop;
    Activity activity;
    MapView map;
    FirebaseFirestore db;

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
                textView_list_bus.setText("");
                for(QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    String s = String.valueOf(textView_list_bus.getText());
                    textView_list_bus.setText(s + q.get("name_bus") + " | ");
                }

        }).addOnFailureListener(f -> textView_list_bus.setText("Something gone wrong, retry"));


        /*
         * Update list bus
         */

        db.collection("Bus")
                .whereEqualTo("busStop_id", busStop.getBus_stop_id())
                .addSnapshotListener((value,error) -> {
                    assert value != null;
                    for(QueryDocumentSnapshot q : value) {
                    String s = String.valueOf(textView_list_bus.getText());
                    textView_list_bus.setText(s + q.get("name_bus") + " | ");
                }
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
        Toast.makeText(activity.getApplicationContext(), "OPEN", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClose() {
        Toast.makeText(activity.getApplicationContext(), "CLOSE", Toast.LENGTH_SHORT).show();
    }


}
