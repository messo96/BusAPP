package com.example.busapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ListBusFragment extends Fragment {
    private BusAdapter busAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_bus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int id_busStop = getActivity().getIntent().getIntExtra("busStop_id", -1);
        int id_creator = getActivity().getIntent().getIntExtra("id", -1);

        setRecyclerView(getActivity());

        /**
         *  Get data from Firestore
         */
        db.collection("Bus").whereEqualTo("busStop_id", id_busStop).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Pair<String, Integer>> list = new ArrayList<>();
                for(QueryDocumentSnapshot q : queryDocumentSnapshots){
                        list.add(new Pair<>(String.valueOf(q.get("name_bus")), Integer.parseInt(String.valueOf(q.get("id_creator"))) ));
                }
                busAdapter.setData(list);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("Bus").whereEqualTo("busStop_id", id_busStop).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Pair<String, Integer>> list = new ArrayList<>();
                for(QueryDocumentSnapshot q : value){
                    list.add(new Pair<>(String.valueOf(q.get("name_bus")), Integer.parseInt(String.valueOf(q.get("id_creator"))) ));
                }
                busAdapter.setData(list);
            }
        });


        view.findViewById(R.id.fab_add_bus).setOnClickListener(w ->{
            Intent intent = new Intent(getContext(), AddBusActivity.class);
            intent.putExtra("busStop_id", id_busStop);
            intent.putExtra("id", id_creator);
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
