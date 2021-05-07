package com.example.busapp;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.room.util.DBUtil;

import com.example.busapp.Utils.BusStopInfoWindows;
import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusRepository;
import com.example.busapp.database.Bus.BusSimple;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;
import com.example.busapp.database.DbConnector;
import com.example.busapp.database.User;
import com.example.busapp.database.UserRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class MapsHome extends Fragment {
    private MapView map;
    private IMapController mapController;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Geocoder geocoder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.maps_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        else {
            initializeLocation(getActivity());
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                Toast.makeText(getContext(), "Request Position", Toast.LENGTH_SHORT).show();
                                startLocationUpdates(getActivity());
                                Log.d("GPS_LOG", "PERMISSION GRANTED GPS");
                            } else {
                                Log.d("GPS_LOG", "PERMISSION DENIED GPS");
                                showDialog(getActivity());
                            }
                        }
                    });


            String position = sharedPreferences.getString("last_coordinates",new Coordinates((float)48.8583, (float)2.2944 ).toString());
            Coordinates coordinates = new Coordinates(Double.parseDouble(position.split(";")[0]), Double.parseDouble(position.split(";")[1]));
            GeoPoint lastPosition = new GeoPoint(coordinates.getLatitudine(), coordinates.getLongitudine());

            mapSetup(lastPosition);
            for(BusStop busStop : getBusStops()){
                addMarker(busStop);
            }
          /* busStopRepository.getAll().observe((LifecycleOwner) getActivity(), new Observer<List<BusStop>>() {
               @Override
               public void onChanged(List<BusStop> busStops) {
                   for (BusStop busStop : busStops){
                       addMarker(busStop);
                   }
               }
           });
*/


            view.findViewById(R.id.btn_current_gps).setOnClickListener(l ->{
                initializeLocation(getActivity());
                Toast.makeText(getContext(), "Finding your position...", Toast.LENGTH_SHORT).show();
                startLocationUpdates(getActivity());

            });
        }
}

    private void mapSetup(GeoPoint startPosition) {
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        map = (MapView) getView().findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(17.0);
       //Toast.makeText(getContext(), position, Toast.LENGTH_SHORT).show();
        mapController.setCenter(startPosition);

    }

    private void addMarker(BusStop busStop){
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(busStop.getPosition().getLatitudine(), busStop.getPosition().getLongitudine()));
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_bus_alert_24));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(new BusStopInfoWindows(getActivity(), R.layout.marker_bus_stop, map, busStop));
        map.getOverlays().add(marker);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<BusStop> getBusStops(){
        List<BusStop> list = new ArrayList<>();
        Task<QuerySnapshot> tResult =  db.collection("BusStop").get();
        while(!tResult.isComplete()){}

        for (QueryDocumentSnapshot q : tResult.getResult()) {
            String coord = String.valueOf(q.get("position"));
            list.add(new BusStop(
                    Integer.parseInt(String.valueOf(q.get("bus_stop_id"))),
                    Integer.parseInt(String.valueOf(q.get("user_created_id"))),
                    String.valueOf(q.get("name")),
                    Coordinates.getCoordinatesFromString(coord),
                    Base64.getDecoder().decode(String.valueOf(q.get("image"))) )
            );
        }

        return list;
    }



    private void initializeLocation(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mapController.animateTo(new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
                Toast.makeText(getContext(), "Done.", Toast.LENGTH_SHORT).show();
                sharedPreferences
                        .edit()
                        .putString("last_coordinates", locationResult.getLastLocation().getLatitude()+";"+locationResult.getLastLocation().getLongitude())
                        .apply();

            }
        };
    }

    private void startLocationUpdates(Activity activity) {
        String PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(activity, PERMISSION_REQUESTED) == PackageManager.PERMISSION_GRANTED){
            LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                showDialog(activity);
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_REQUESTED))
            showDialog(activity);
        else
            activityResultLauncher.launch(PERMISSION_REQUESTED);


    }



            private void showDialog(Activity activity) {
                new AlertDialog.Builder(activity)
                        .setMessage("GPS is disabled, please enable for track the bus stop!")
                        .setCancelable(false)
                        .setPositiveButton("Enable now : )", (dialog, id) -> activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton("Cancel ", (dialog, id) -> dialog.cancel())
                        .create()
                        .show();
            }




}
