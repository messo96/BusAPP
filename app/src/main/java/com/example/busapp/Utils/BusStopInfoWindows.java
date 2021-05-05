package com.example.busapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.BusStopActivity;
import com.example.busapp.R;
import com.example.busapp.database.Bus.BusRepository;
import com.example.busapp.database.Bus.BusSimple;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;
import java.util.stream.Collectors;

public class BusStopInfoWindows extends InfoWindow {
    SharedPreferences sharedPreferences;
    BusStop busStop;
    Activity activity;
    MapView map;
    boolean isOpen;
    FirebaseFirestore db;

    public BusStopInfoWindows(Activity activity, int idRes, MapView map, BusStop busStop){
        super(idRes, map);
        this.busStop = busStop;
        this.activity = activity;
        this.map = map;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        db = FirebaseFirestore.getInstance();

    }


    @Override
    public void onOpen(Object item) {
        InfoWindow.closeAllInfoWindowsOn(map);
        if(!isOpen) {
            ImageView imageView = getView().findViewById(R.id.image_busStop);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(busStop.getImage(), 0, busStop.getImage().length));
            TextView textView = getView().findViewById(R.id.name_busStop);
            TextView textView_list_bus = getView().findViewById(R.id.list_bus);
            TextView textView_creator = getView().findViewById(R.id.text_created_marker);
            textView.setText(busStop.getName());

            db.collection("Bus").whereEqualTo("busStop_id", busStop.getBus_stop_id()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    textView_list_bus.setText("");
                    for(QueryDocumentSnapshot q : queryDocumentSnapshots) {
                        String s = String.valueOf(textView_list_bus.getText());
                        textView_list_bus.setText(s + q.get("name_bus") + " | ");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    textView_list_bus.setText("Something gone wrong, retry");
                }
            });


            db.collection("User").whereEqualTo("id", busStop.getUser_created_id()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                        textView_creator.setText(textView_creator.getText() + String.valueOf(q.get("username")));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView_creator.setText("");
                    }
                });



            getView().findViewById(R.id.btn_see_bus).setOnClickListener(e -> {
                int id_creator = sharedPreferences.getInt("id", -1);
                Intent intent = new Intent(activity.getApplicationContext(), BusStopActivity.class);
                intent.putExtra("busStop_id", busStop.getBus_stop_id());
                intent.putExtra("id", id_creator);

                activity.startActivity(intent);
            });
/*

/*
            busRepository.getBus(busStop.getBus_stop_id()).observe((LifecycleOwner) activity, new Observer<List<BusSimple>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onChanged(List<BusSimple> buses) {
                    textView_list_bus.setText("");
                    List<String> list = buses.stream().map(b -> b.getNumber()).distinct().collect(Collectors.toList());
                    for (String busNumber : list) {
                        String s = String.valueOf(textView_list_bus.getText());
                        textView_list_bus.setText(s + busNumber + " | ");
                    }
                }
            });


            userRepository.getUserFromId(busStop.getUser_created_id()).observe((LifecycleOwner) activity, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            textView_creator.setText(textView_creator.getText() + s);
                        }
                    });


            Toast.makeText(activity.getApplicationContext(), "OPEN", Toast.LENGTH_SHORT).show();
            isOpen = true;
        }
        else{
            isOpen = false;
            InfoWindow.closeAllInfoWindowsOn(getMapView());
        }

*/
        }
    }


    @Override
    public void onClose() {
        Toast.makeText(activity.getApplicationContext(), "CLOSE", Toast.LENGTH_SHORT).show();

    }
}
