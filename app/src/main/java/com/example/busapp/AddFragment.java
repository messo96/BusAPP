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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.Utils.Utilities;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;
import com.example.busapp.database.DbConnector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private FirebaseFirestore db;
    private ActionBar actionBar;

    public AddFragment(final ActionBar actionBar){
        this.actionBar = actionBar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        busStopRepository = new BusStopRepository(getActivity().getApplication());
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

        if(sharedPreferences.getBoolean("logged", false)) {
            Activity activity = getActivity();

            if (activity != null) {
                view.findViewById(R.id.button_camera_bus).setOnClickListener(e -> {
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
                                    Log.d("GPS_LOG", "PERMISSION GRANTED GPS");
                                } else {
                                    Log.d("GPS_LOG", "PERMISSION DENIED GPS");
                                    showDialog(activity);
                                }
                            }
                        });

                view.findViewById(R.id.button_gps).setOnClickListener(c -> {
                    startLocationUpdates(activity);
                });
                view.findViewById(R.id.button_add_busstop).setOnClickListener(o -> {
                    int id = sharedPreferences.getInt("id", -1);
                    if (coordinates != null && editText_busNumber.getText() != null && image != null) {
                        if (id < 0)
                            Toast.makeText(getContext(), "ALL FIELD MUST BE FILLED, SORRY :(", Toast.LENGTH_SHORT).show();
                        else {
                            BusStop busStop = new BusStop(0, sharedPreferences.getInt("id", 0), String.valueOf(editText_busNumber.getText()), coordinates, image);
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", busStop.getName());
                            map.put("image", Base64.getEncoder().encodeToString(busStop.getImage()));
                            map.put("position", busStop.getPosition().toString());
                            map.put("user_created_id", busStop.getUser_created_id());
                            Task<QuerySnapshot> tResult = db.collection("BusStop").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    map.put("bus_stop_id", task.getResult().size());
                                    db.collection("BusStop").add(map).addOnSuccessListener(l -> {
                                        Toast.makeText(getContext(), "Bus Stop " + String.valueOf(editText_busNumber.getText()) + " created successfully", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();

                                    }).addOnFailureListener(l -> {
                                        Toast.makeText(getContext(), "Can't create Bus Stop", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                    }



                    /*
                    int id = sharedPreferences.getInt("id", 0);
                    if (coordinates != null && editText_busNumber.getText() != null && image != null) {
                        if (id == 0)
                            Toast.makeText(getContext(), "ALL FIELD MUST BE FILLED, SORRY :(", Toast.LENGTH_SHORT).show();
                        else {
                            busStopRepository.addBusStop(new BusStop(sharedPreferences.getInt("id", 0), String.valueOf(editText_busNumber.getText()), coordinates, image));
                            Toast.makeText(getContext(), "Bus Stop created successfully", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }

                    }
                    */
                });
            }

        }
        else{
            new AlertDialog.Builder(getContext()).setMessage("You must be logged for create new Bus Stop.")
                    .setPositiveButton("Ok ,log me in", (dialog, id) -> {
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(true, actionBar), "ProfileFragment", R.id.fragment_container_view);
                    })
                    .setNegativeButton("No, i won't", (dialog, id) -> dialog.cancel())
                    .create().show();
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

                Toast.makeText(activity.getApplicationContext(), "Position getted", Toast.LENGTH_SHORT).show();

                try {
                    Address location = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1).get(0);

                    busStopRepository.getAll().observe((LifecycleOwner) activity, new Observer<List<BusStop>>() {
                        boolean stillExist = false;
                        @Override
                        public void onChanged(List<BusStop> busStops) {
                            for(BusStop bus : busStops){
                                if(location.getLongitude() == bus.getPosition().getLongitudine() && location.getLatitude() == bus.getPosition().getLatitudine()){
                                    stillExist = true;
                                    break;
                                }
                            }
                            if(stillExist){
                                new AlertDialog.Builder(getContext())
                                        .setMessage("The Bus Stop in this position is already been created, please go to another. ")
                                        .setPositiveButton("Ok", ((dialog, which) -> dialog.cancel()))
                                        .create()
                                        .show();
                            }
                            else{
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