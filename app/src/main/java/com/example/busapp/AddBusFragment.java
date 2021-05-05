package com.example.busapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.Utils.Week;
import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddBusFragment extends Fragment {
    BusRepository busRepository;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null)
            busRepository = new BusRepository(getActivity().getApplication());

        db = FirebaseFirestore.getInstance();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_bus_simple_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getContext();
        EditText text_number_bus = view.findViewById(R.id.text_number_bus);

            view.findViewById(R.id.button_add_bus_stop).setOnClickListener(c -> {
                if (!text_number_bus.getText().toString().matches("") && getActivity() != null){
                    int id_busStop = getActivity().getIntent().getIntExtra("busStop_id", -1);
                    int id_user = getActivity().getIntent().getIntExtra("id", -1);
                    String name_bus = String.valueOf(text_number_bus.getText());
                    db.collection("Bus").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            boolean stillExist = false;
                            for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                if (Integer.parseInt(String.valueOf(q.get("busStop_id"))) == id_busStop
                                        && String.valueOf(q.get("name_bus")).matches(name_bus)) {
                                    stillExist = true;
                                    break;
                                }
                            }
                            if (stillExist) {
                                Toast.makeText(context, "Sorry but this bus still exist", Toast.LENGTH_LONG).show();
                            } else {
                                Map<String, Object> map = new HashMap<>();
                                map.put("name_bus", name_bus);
                                map.put("busStop_id", id_busStop);
                                map.put("id_creator", id_user);
                                db.collection("Bus").add(map)
                                        .addOnSuccessListener(l -> {
                                            Toast.makeText(context, "Bus " + name_bus + " added successfully!", Toast.LENGTH_LONG).show();
                                            getActivity().onBackPressed();
                                        })
                                        .addOnFailureListener(l -> Toast.makeText(context, "Can't create this bus.", Toast.LENGTH_LONG).show());
                            }

                        }
                    });

                }
            });
    }

}


