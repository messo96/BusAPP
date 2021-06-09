package com.example.busapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.Utils.Utilities;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import static android.app.Activity.RESULT_OK;


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
    private FirebaseFirestore db;
    private final ActionBar actionBar;
    Uri imageUri;

    public AddFragment(final ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        busStopRepository = new BusStopRepository(requireActivity().getApplication());
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBar.setTitle("Add new Bus Stop");
        Activity activity = requireActivity();
        view_position = activity.findViewById(R.id.edit_text_position);

        view.findViewById(R.id.fab_inside_add_bus_stop).setOnClickListener( l ->{
            requireActivity().onBackPressed();
        });

        EditText editText_busStopName = view.findViewById(R.id.edit_text_name_bus_stop);
        int id = sharedPreferences.getInt("id", -1);

        if (id == -1) {
            new AlertDialog.Builder(getContext()).setMessage("You must be logged for create new Bus Stop.")
                    .setPositiveButton("Ok ,log me in", (dialog, identity) ->
                        Utilities
                                .insertFragment((AppCompatActivity) requireActivity(),
                                        new ProfileFragment(true, actionBar), "ProfileFragment", R.id.fragment_container_view)
            )
                    .setNegativeButton("No, i won't", (dialog, identity) -> {
                        dialog.cancel();
                        requireActivity().onBackPressed();
                    })
                    .create().show();
        }

        if (sharedPreferences.getBoolean("logged", false)) {


            requireActivity();
            view.findViewById(R.id.button_camera_bus).setOnClickListener(e -> uploadImage() );


            initializeLocation(getActivity());
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                   result -> {
                            if (result) {
                                Toast.makeText(getContext(), "Request Position", Toast.LENGTH_SHORT).show();
                                startLocationUpdates(getActivity());
                                Log.d("GPS_LOG", "PERMISSION GRANTED GPS");
                            } else {
                                Log.d("GPS_LOG", "PERMISSION DENIED GPS");
                                showDialog(activity);
                            }
                    });

            view.findViewById(R.id.button_gps).setOnClickListener(c -> startLocationUpdates(activity) );

            view.findViewById(R.id.button_add_busstop).setOnClickListener(o -> {

                        if (checkInformation(coordinates, image, editText_busStopName.getText()) && id != -1) {
                            BusStop busStop = new BusStop(0, id, String.valueOf(editText_busStopName.getText()), coordinates, image);
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", busStop.getName());
                            map.put("image", Base64.getEncoder().encodeToString(busStop.getImage()));
                            map.put("position", busStop.getPosition().toString());
                            map.put("user_created_id", busStop.getUser_created_id());
                            db.collection("BusStop").get().addOnCompleteListener(task -> {
                                    map.put("bus_stop_id", task.getResult().size());
                                    db.collection("BusStop").add(map).addOnSuccessListener(l -> {
                                        Toast.makeText(getContext(), "Bus Stop " + editText_busStopName.getText() + " created successfully", Toast.LENGTH_SHORT).show();
                                        requireActivity().onBackPressed();

                                    }).addOnFailureListener(l -> Toast.makeText(getContext(), "Can't create Bus Stop" + l.getMessage(), Toast.LENGTH_SHORT).show() );
                            });
                        }
                    }
            );
        }

    }


    private void initializeLocation(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    Address location = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1).get(0);

                    busStopRepository.getAll().observe((LifecycleOwner) activity, new Observer<List<BusStop>>() {
                        boolean stillExist = false;

                        @Override
                        public void onChanged(List<BusStop> busStops) {
                            for (BusStop bus : busStops) {
                                if (location.getLongitude() == bus.getPosition().getLongitudine() && location.getLatitude() == bus.getPosition().getLatitudine()) {
                                    stillExist = true;
                                    break;
                                }
                            }
                            if (stillExist) {
                                new AlertDialog.Builder(getContext())
                                        .setMessage("The Bus Stop in this position is already been created, please go to another. ")
                                        .setPositiveButton("Ok", ((dialog, which) -> dialog.cancel()))
                                        .create()
                                        .show();
                            } else {
                                view_position.setText(location.getAddressLine(0));
                                coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
                                sharedPreferences.edit()
                                        .putString("last_coordinates", coordinates.toString())
                                        .apply();
                            }
                            locationRequest.setNumUpdates(1);

                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

    }

    private void startLocationUpdates(Activity activity) {
        String PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(activity, PERMISSION_REQUESTED) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                showDialog(activity);
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_REQUESTED))
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
        Toast.makeText(getContext(), "Take a photo of point of reference for find the bus stop", Toast.LENGTH_LONG).show();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Place_Picture_BUSAPP");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
        imageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap;
            ImageView imageView = requireView().findViewById(R.id.imageview_bus);
            imageView.setVisibility(View.VISIBLE);
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(imageBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                image = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }


    private boolean checkInformation(Coordinates coordinates, byte[] image, Editable nameBusStop) {
    boolean check = true;
        if (coordinates == null) {
            Snackbar.make(requireView(), "We have to get your position (GPS button)", Snackbar.LENGTH_SHORT).show();
            check = false;
        }
        if (String.valueOf(nameBusStop).matches("")) {
            Snackbar.make(requireView(), "You have to write name of the bus stop!", Snackbar.LENGTH_SHORT).show();
            check = false;
        }
        if (image == null) {
            Snackbar.make(requireView(), "Take a photo of the bus stop or in front of the bus stop", Snackbar.LENGTH_SHORT).show();
            check = false;
        }

        return check;
    }

}