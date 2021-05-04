package com.example.busapp.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.DetailBusActivity;
import com.example.busapp.R;
import com.example.busapp.Utils.Day;
import com.example.busapp.database.Bus.Bus;
import com.example.busapp.database.Bus.BusRepository;
import com.example.busapp.database.Bus.BusSimple;
import com.example.busapp.database.BusStop.BusStop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusAdapter extends RecyclerView.Adapter<BusViewHolder> implements Filterable {

    private List<String> busList = new ArrayList<>();
    private Activity activity;

    public BusAdapter(Activity activity){
        this.activity = activity;
    }


    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_layout, parent, false);
        return new BusViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        String busStop = busList.get(position);
        holder.textViewNumberBus.setText(busStop);
        holder.textViewCreator.setText(holder.textViewCreator.getText());
        holder.idBusStop = activity.getIntent().getIntExtra("busStop_id", 0);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setData(List<BusSimple> list) {
        this.busList = list.stream().map(b -> b.getNumber()).distinct().collect(Collectors.toList());
        notifyDataSetChanged();
    }

}
