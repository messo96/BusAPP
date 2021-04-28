package com.example.busapp.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.R;

public class BusViewHolder extends RecyclerView.ViewHolder {

    TextView textViewNumberBus;
    TextView textViewOrari;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewNumberBus = itemView.findViewById(R.id.number_bus_card);
        textViewOrari = itemView.findViewById(R.id.orario_bus_card);

    }
}
