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

import java.util.List;
import java.util.Locale;

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
        Context context = getContext();
        EditText text_number_bus = view.findViewById(R.id.text_number_bus);
        Spinner day_spinner = view.findViewById(R.id.select_day_spinner);
        TimePicker timePicker = view.findViewById(R.id.select_hour_time);
           //Toast.makeText(getContext(), String.valueOf(timePicker.getCurrentHour()), Toast.LENGTH_SHORT).show();

        view.findViewById(R.id.button_add_bus_stop).setOnClickListener(c ->{
            if(!text_number_bus.getText().toString().matches("")){
                int id_busStop = getActivity().getIntent().getIntExtra("busStop_id", 0);
                String name_bus = String.valueOf(text_number_bus.getText());
                String day = day_spinner.getSelectedItem().toString();
                String hour = String.format(Locale.ITALY, "%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());

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
        });



    }
}
