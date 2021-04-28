package com.example.busapp.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.R;
import com.example.busapp.database.Bus.Bus;

import java.util.ArrayList;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusViewHolder> implements Filterable {

    private List<Bus> busList = new ArrayList<>();
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
        Bus busStop = busList.get(position);
        holder.textViewNumberBus.setText(busStop.getNumber());
        holder.textViewOrari.setText(String.valueOf(busStop.getCalendar().getDay()) );
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public void setData(List<Bus> list) {
        this.busList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

}
