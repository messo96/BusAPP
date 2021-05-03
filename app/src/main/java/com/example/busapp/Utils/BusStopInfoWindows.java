package com.example.busapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.BusStopActivity;
import com.example.busapp.R;
import com.example.busapp.database.Bus.BusRepository;
import com.example.busapp.database.Bus.BusSimple;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.UserRepository;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;
import java.util.stream.Collectors;

public class BusStopInfoWindows extends InfoWindow {
    BusRepository busRepository;
    UserRepository userRepository;
    BusStop busStop;
    Activity activity;
    MapView map;
    boolean isOpen;

    public BusStopInfoWindows(Activity activity, int idRes, MapView map, BusRepository busRepository, BusStop busStop, UserRepository userRepository){
        super(idRes, map);
        this.busRepository = busRepository;
        this.busStop = busStop;
        this.activity = activity;
        this.map = map;
        this.userRepository = userRepository;
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

                    getView().findViewById(R.id.btn_see_bus).setOnClickListener(e -> {
                        Intent intent = new Intent(activity.getApplicationContext(), BusStopActivity.class);
                        intent.putExtra("busStop_id", busStop.getBus_stop_id());
                        activity.startActivity(intent);
                    });
            Toast.makeText(activity.getApplicationContext(), "OPEN", Toast.LENGTH_SHORT).show();
            isOpen = true;
        }
        else{
            isOpen = false;
            InfoWindow.closeAllInfoWindowsOn(getMapView());
        }
    }

    @Override
    public void onClose() {
        Toast.makeText(activity.getApplicationContext(), "CLOSE", Toast.LENGTH_SHORT).show();

    }
}
