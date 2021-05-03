package com.example.busapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class AddFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private ActivityResultLauncher<String> activityResultLauncher;
    private Geocoder geocoder;
    private SharedPreferences sharedPreferences;
    private TextView view_position;
    private Coordinates coordinates;
    private BusStopRepository busStopRepository;
    private byte[] image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        busStopRepository = new BusStopRepository(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();

        if (activity != null) {
            view.findViewById(R.id.button_camera_bus).setOnClickListener(e ->{
                uploadImage();
            });

            view_position = activity.findViewById(R.id.edit_text_position);
            EditText editText_busNumber = view.findViewById(R.id.edit_text_number_bus);
            initializeLocation(getActivity());
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                Toast.makeText(getContext(), "Request Position", Toast.LENGTH_SHORT).show();
                                startLocationUpdates(getActivity());
                                Log.d("LAB", "PERMISSION GRANTED GPS");
                            } else {
                                Log.d("LAB", "PERMISSION DENIED GPS");
                                showDialog(activity);
                            }
                        }
                    });

            view.findViewById(R.id.button_gps).setOnClickListener(c -> {
                startLocationUpdates(activity);

            });
            view.findViewById(R.id.button_add_busstop).setOnClickListener(o -> {
                int id = sharedPreferences.getInt("id", 0);
                if (coordinates != null && editText_busNumber.getText() != null && image != null) {
                    if (id == 0)
                        Toast.makeText(getContext(), "ALL FIELD MUST BE FILLED, SORRY :(", Toast.LENGTH_SHORT).show();
                    else
                        busStopRepository.addBusStop(new BusStop(sharedPreferences.getInt("id", 0), String.valueOf(editText_busNumber.getText()), coordinates, image));

                }
            });
        }


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

                Toast.makeText(activity.getApplicationContext(), "Position getted", Toast.LENGTH_SHORT).show();
                try {
                    Address location = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1).get(0);
                    view_position.setText(location.getAddressLine(0));
                    coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                    sharedPreferences.edit()
                            .putString("last_coordinates", coordinates.toString())
                            .apply();

                } catch (IOException e) {
                    e.printStackTrace();
                }

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


    private void uploadImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = getView().findViewById(R.id.imageview_bus);
            imageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = stream.toByteArray();
        }
    }

}