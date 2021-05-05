package com.example.busapp;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.busapp.Utils.Day;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddTimeFragment extends Fragment {
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_bus_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        TextView day_text = view.findViewById(R.id.day_selected);
        TimePicker timePicker = view.findViewById(R.id.select_hour_time);
        int id_busStop = activity.getIntent().getIntExtra("busStop_id", -1);
        int id_creator = activity.getIntent().getIntExtra("id", -1);
        String name_bus = activity.getIntent().getStringExtra("name_bus");
        String day = activity.getIntent().getStringExtra("day");
        day_text.setText(day);

        view.findViewById(R.id.button_add_bus_stop).setOnClickListener(c ->{
            String hour = String.format(Locale.ITALY, "%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            Map<String, Object> map = new HashMap<>();
            map.put("busStop_id", id_busStop);
            map.put("name_bus", name_bus);
            map.put("day", day);
            map.put("id_creator", id_creator);
            map.put("time", hour);

            db.collection("Time").add(map)
                    .addOnSuccessListener(s -> {
                        Toast.makeText(getContext(), "Time added successfully", Toast.LENGTH_LONG).show();

                    })
                    .addOnFailureListener(f -> Toast.makeText(getContext(), "Error, can't add Time", Toast.LENGTH_LONG).show());

            /*
                busRepository.getHours(id_busStop, name_bus, day_spinner.getSelectedItem().toString()).observe((LifecycleOwner) getActivity(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        boolean stillExist = false;
                        for(String h : strings){
                            if(h.matches(hour)){
                                stillExist = true;
                                break;
                            }
                        }
                        if(stillExist)
                            Toast.makeText(context, "This hour for this bus still exist", Toast.LENGTH_LONG).show();
                        else{
                            busRepository.addBus(new Bus(name_bus, new Week(day_spinner.getSelectedItem().toString(), hour), id_busStop));

                            Toast.makeText(context, "Bus" + name_bus + " added :)", Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager().popBackStack();

                        }
                    }

                });

            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setMessage("You must write the name or number of the bus")
                        .setCancelable(false)
                        .setPositiveButton("Ok, sorry", (dialog, id) -> dialog.cancel())
                        .setNegativeButton("No I won't", (dialog, id) -> getActivity().getSupportFragmentManager().popBackStack())
                        .create()
                        .show();
            }

            */

        });


    }

}
