package com.example.busapp.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Build;
import android.util.Pair;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BusAdapter extends RecyclerView.Adapter<BusViewHolder> implements Filterable {

    private  List<Pair<String, Integer>> busList = new ArrayList<>();
    private Activity activity;
    private FirebaseFirestore db;
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
        Pair<String, Integer> busStop = busList.get(position);
        holder.textViewNumberBus.setText(busStop.first);
        writeCreator(holder, busStop.second);
        holder.idBusStop = activity.getIntent().getIntExtra("busStop_id", -1);
    }

    private void writeCreator(BusViewHolder holder, Integer id_creator) {
        db.collection("User").whereEqualTo("id", id_creator).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot q : queryDocumentSnapshots)
                holder.textViewCreator.setText(holder.textViewCreator.getText() + String.valueOf(q.get("username")) );
            }
        });

    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setData( List<Pair<String, Integer>> list) {
        this.busList = list;
        this.db = FirebaseFirestore.getInstance();
        notifyDataSetChanged();
    }

}
