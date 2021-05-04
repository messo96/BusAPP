package com.example.busapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.RecyclerView.BusAdapter;
import com.example.busapp.Utils.Day;
import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusRepository;
import com.example.busapp.database.Bus.BusSimple;
import com.example.busapp.database.BusStop.BusStop;
import com.example.busapp.database.BusStop.BusStopRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ListBusFragment extends Fragment {
    private BusAdapter busAdapter;
    private BusRepository busRepository;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        busRepository = new BusRepository(getActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_bus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int id_busStop = getActivity().getIntent().getIntExtra("busStop_id", 0);


        setRecyclerView(getActivity());
        busRepository.getBus(id_busStop).observe((LifecycleOwner) getActivity(), new Observer<List<BusSimple>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<BusSimple> bus) {
                if(!bus.isEmpty()) {
                    busAdapter.setData(bus);
                }

            }
        });

        view.findViewById(R.id.fab_add_bus).setOnClickListener(w ->{
            Intent intent = new Intent(getContext(), AddBusActivity.class);
            intent.putExtra("busStop_id", id_busStop);
            startActivity(intent);
        });


    }

    private void setRecyclerView(final Activity activity) {
        recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        busAdapter = new BusAdapter(activity);
        recyclerView.setAdapter(busAdapter);
    }

}
