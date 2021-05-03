package com.example.busapp.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.DetailBusActivity;
import com.example.busapp.R;

public class BusViewHolder extends RecyclerView.ViewHolder {

    TextView textViewNumberBus;
    TextView textViewCreator;
    Integer idBusStop;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewNumberBus = itemView.findViewById(R.id.number_bus_card);
        textViewCreator = itemView.findViewById(R.id.creator_bus_card);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailBusActivity.class);
                intent.putExtra("bus_name", textViewNumberBus.getText());
                intent.putExtra("id_bus_stop", idBusStop);
                v.getContext().startActivity(intent);
            }
        });

    }
}
