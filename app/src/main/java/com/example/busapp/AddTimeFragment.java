package com.example.busapp;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


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
        assert activity != null;
        int id_busStop = activity.getIntent().getIntExtra("busStop_id", -1);
        int id_creator = activity.getIntent().getIntExtra("id", -1);
        String name_bus = activity.getIntent().getStringExtra("bus_name");
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
            map.put("feedback", 0);


            db.collection("Time")
                    .whereEqualTo("time", hour)
                    .whereEqualTo("day", day)
                    .get()
                    .addOnSuccessListener(task ->{
                        if(task.isEmpty()){
                            db.collection("Time").add(map)
                                    .addOnSuccessListener(s -> {
                                        Toast.makeText(getContext(), "Time added successfully", Toast.LENGTH_LONG).show();
                                        requireActivity().onBackPressed();

                                    })
                                    .addOnFailureListener(f -> Toast.makeText(getContext(), "Error, can't add Time", Toast.LENGTH_LONG).show());
                        }
                        else{
                            Toast.makeText(getContext(), "Time is still added.", Toast.LENGTH_LONG).show();
                        }
                    });

        });


    }

}
