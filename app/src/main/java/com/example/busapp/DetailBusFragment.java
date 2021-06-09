package com.example.busapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.busapp.Utils.Day;

public class DetailBusFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bus_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int idBusStop = requireActivity().getIntent().getIntExtra("id_bus_stop", 0);
        String nameBus = requireActivity().getIntent().getStringExtra("bus_name");
        TextView textView_busName = view.findViewById(R.id.bus_name);
        textView_busName.setText(nameBus);
        ListView listView = requireActivity().findViewById(R.id.list_hour);
        Spinner spinner = view.findViewById(R.id.spinner_day);
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), getContext(), idBusStop, nameBus);
        listView.setAdapter(customAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Day day = Day.getDay(position);
                customAdapter.setDay(day);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



    }
}
