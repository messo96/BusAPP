package com.example.busapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;

import com.example.busapp.RecyclerView.BusAdapter;

import com.example.busapp.Utils.MyProgressLoader;
import com.example.busapp.Utils.Utilities;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ListBusFragment extends Fragment {
    private BusAdapter busAdapter;
    private FirebaseFirestore db;
    private final ActionBar actionBar;
    private MyProgressLoader progressLoader;


    public ListBusFragment(final ActionBar actionBar){
        this.actionBar = actionBar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        progressLoader = new MyProgressLoader(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_bus, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int id_busStop = requireActivity().getIntent().getIntExtra("busStop_id", -1);
        int id_creator = requireActivity().getIntent().getIntExtra("id", -1);

        setRecyclerView(getActivity());

        /*
         *  Get data from Firestore
         */
        progressLoader.show();
        db.collection("Bus")
                .whereEqualTo("busStop_id", id_busStop)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Pair<String, Integer>> list = new ArrayList<>();
                for(QueryDocumentSnapshot q : queryDocumentSnapshots){
                        list.add(new Pair<>(String.valueOf(q.get("name_bus")), Integer.parseInt(String.valueOf(q.get("id_creator"))) ));
                }
                busAdapter.setData(list);
                progressLoader.hide();
        });

        progressLoader.show();
        db.collection("Bus")
                .whereEqualTo("busStop_id", id_busStop)
                .addSnapshotListener((value, error) -> {
                List<Pair<String, Integer>> list = new ArrayList<>();
                assert value != null;
                for(QueryDocumentSnapshot q : value){
                    list.add(new Pair<>(String.valueOf(q.get("name_bus")), Integer.parseInt(String.valueOf(q.get("id_creator"))) ));
                }
                busAdapter.setData(list);
                progressLoader.hide();

                });


        view.findViewById(R.id.fab_add_bus).setOnClickListener(w ->{
            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id", -1) == -1){

                new AlertDialog.Builder(getContext()).setMessage("You must be logged for add new Bus for "
                                                                                        + requireActivity().getIntent().getStringExtra("name_busStop"))
                        .setPositiveButton("Ok ,log me in", (dialog, identity) ->
                                Utilities
                                        .insertFragment((AppCompatActivity) requireActivity(),
                                                new ProfileFragment(true, actionBar), "ProfileFragment", R.id.fragment_container_view)
                        )
                        .setNegativeButton("No, i won't", (dialog, identity) -> {
                            dialog.cancel();
                            requireActivity().onBackPressed();
                        })
                        .create().show();
            }
            else {
                Intent intent = new Intent(getContext(), AddBusActivity.class);
                intent.putExtra("busStop_id", id_busStop);
                intent.putExtra("id", id_creator);
                startActivity(intent);
            }
        });


    }

    private void setRecyclerView(final Activity activity) {
        RecyclerView recyclerView = requireView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        busAdapter = new BusAdapter(activity);
        recyclerView.setAdapter(busAdapter);
    }

}
