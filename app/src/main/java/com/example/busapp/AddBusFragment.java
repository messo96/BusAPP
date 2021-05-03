package com.example.busapp;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.example.busapp.Utils.Week;
import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusRepository;

public class AddBusFragment extends Fragment {
    BusRepository busRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busRepository = new BusRepository(getActivity().getApplication());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_bus_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText text_number_bus = view.findViewById(R.id.text_number_bus);
        Spinner day_spinner = view.findViewById(R.id.select_day_spinner);
        TimePicker timePicker = view.findViewById(R.id.select_hour_time);
           //Toast.makeText(getContext(), String.valueOf(timePicker.getCurrentHour()), Toast.LENGTH_SHORT).show();

        view.findViewById(R.id.button_add_bus_stop).setOnClickListener(c ->{
            if(!text_number_bus.getText().toString().matches("")){
                int id_busStop = getActivity().getIntent().getIntExtra("busStop_id", 0);
                String name_bus = String.valueOf(text_number_bus.getText());
                String day = day_spinner.getSelectedItem().toString();
                String hour = timePicker.getCurrentHour() + ":"+timePicker.getCurrentMinute();

                busRepository.addBus(new Bus(name_bus, new Week(day_spinner.getSelectedItem().toString(), hour), id_busStop));

                Toast.makeText(getContext(), "Bus" + name_bus + " added :)", Toast.LENGTH_LONG).show();
                FragmentManager frag = getActivity().getSupportFragmentManager();
                    frag.popBackStack();
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
        });



    }
}