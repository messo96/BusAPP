package com.example.busapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;
import com.google.android.gms.common.util.MapUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;


public class MapsHome extends Fragment {
    private MapView map;
    private IMapController mapController;
    private SharedPreferences sharedPreferences;
    private BusStopRepository busStopRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        busStopRepository = new BusStopRepository(getActivity().getApplication());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.maps_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        else {
            String position = sharedPreferences.getString("last_coordinates",new Coordinates((float)48.8583, (float)2.2944 ).toString());
            Coordinates coordinates = new Coordinates(Double.parseDouble(position.split(";")[0]), Double.parseDouble(position.split(";")[1]));
            GeoPoint lastPosition = new GeoPoint(coordinates.getLatitudine(), coordinates.getLongitudine());

            mapSetup(lastPosition);
            List<BusStop> list = new ArrayList<>();
            busStopRepository.getAll().observe((LifecycleOwner) getActivity(), busStops -> list.addAll(busStops));
            if(list != null && !list.isEmpty()) {
                for (BusStop busStop : list){
                    addMarker(busStop);
                }
            }

        }
}

    private void mapSetup(GeoPoint startPosition) {
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        map = (MapView) getView().findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(17.0);
       //Toast.makeText(getContext(), position, Toast.LENGTH_SHORT).show();
        mapController.setCenter(startPosition);
    }

    private void addMarker(BusStop busStop){
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(busStop.getPosition().getLatitudine(), busStop.getPosition().getLongitudine()));
        marker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_bus_alert_24));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);

    }

}