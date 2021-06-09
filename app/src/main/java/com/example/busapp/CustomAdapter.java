package com.example.busapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.Utils.Day;

import com.example.busapp.Utils.Utilities;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private final int MAX_HEIGHT = 150;
    private final Context context;
    private Day day;
    private final String[] orario;
    private final LayoutInflater inflater;
    private final Activity activity;
    private final FirebaseFirestore db;
    private final Integer idBusStop;
    private final String nameBus;
    private int length = Day.values().length - 1;


    public CustomAdapter(Activity activity, Context applicationContext, int idBusStop, String nameBus) {
        this.orario = new String[length];
        this.context = applicationContext;
        this.activity = activity;
        this.inflater = (LayoutInflater.from(applicationContext));
        this.nameBus = nameBus;
        this.idBusStop = idBusStop;
        this.db = FirebaseFirestore.getInstance();
        this.day = Day.All;
    }


    @Override
    public int getCount() {
        return this.length;
    }

    @Override
    public Object getItem(int i) {
        return orario[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.activity_listview, null);

        Day tmp_day;
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        if(this.day.getIndex() == Day.All.getIndex() ){
            tmp_day = Day.getDay(position+1);
        }
        else{
            tmp_day = this.day;
            view.getLayoutParams().height = 8000;

        }


        TextView text_day =  view.findViewById(R.id.text_day);
        ListView listViewTime = view.findViewById(R.id.listView_time);
        text_day.setText(tmp_day.getName());


        /*
         *  Get data from Firestore
         */
        db.collection("Time")
                .whereEqualTo("busStop_id", idBusStop)
                .whereEqualTo("name_bus", nameBus)
                .whereEqualTo("day", tmp_day.getName())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pair<String, Integer>> list = new ArrayList<>();
                    for(QueryDocumentSnapshot q : queryDocumentSnapshots)
                        list.add(new Pair<>(String.valueOf(q.get("time")), Integer.parseInt(String.valueOf(q.get("feedback"))) ));

                    TimeBusAdapter timeBusAdapter = new TimeBusAdapter(context, idBusStop, nameBus, tmp_day, list);
                    listViewTime.setAdapter(timeBusAdapter);
                    listViewTime.getLayoutParams().height = list.size() * MAX_HEIGHT;

        }).addOnFailureListener(f -> Toast.makeText(context, "Fail", Toast.LENGTH_LONG).show());


        /*
         * Get real time changes in Firestore
         */
        db.collection("Time")
                .whereEqualTo("busStop_id", idBusStop)
                .whereEqualTo("name_bus", nameBus)
                .whereEqualTo("day", tmp_day.getName())
                .addSnapshotListener((value, error) -> {
                        List<Pair<String, Integer>> list = new ArrayList<>();
                    assert value != null : "Error to read updates of Time";
                    for(QueryDocumentSnapshot q : value)
                            list.add(new Pair<>(String.valueOf(q.get("time")), Integer.parseInt(String.valueOf(q.get("feedback"))) ));
                        TimeBusAdapter timeBusAdapter = new TimeBusAdapter(context, idBusStop, nameBus, tmp_day, list);
                        listViewTime.setAdapter(timeBusAdapter);
                        listViewTime.getLayoutParams().height = list.size() * MAX_HEIGHT;
                });


        view.findViewById(R.id.btn_add_hour).setOnClickListener(l ->{
            int id_creator = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
            if(id_creator == -1){

                new AlertDialog.Builder(context).setMessage("You must be logged for add new Time for Bus " + nameBus)
                        .setPositiveButton("Ok ,log me in", (dialog, identity) ->
                                Utilities
                                        .insertFragment((AppCompatActivity) activity,
                                                new ProfileFragment(true, ((AppCompatActivity) activity).getSupportActionBar()), "ProfileFragment", R.id.fragment_container_view)
                        )
                        .setNegativeButton("No, i won't", (dialog, identity) -> {
                            dialog.cancel();
                            activity.onBackPressed();
                        })
                        .create().show();
            }
            else {
                Intent intent = new Intent(activity.getApplicationContext(), AddTimeActivity.class);
                intent.putExtra("busStop_id", idBusStop);
                intent.putExtra("id", id_creator);
                intent.putExtra("bus_name", nameBus);
                intent.putExtra("name_busStop", activity.getIntent().getStringExtra("name_busStop"));
                intent.putExtra("day", tmp_day.getName());

                activity.startActivity(intent);
            }
        });


        return view;
    }


    void setDay(final Day day){
        this.day = day;

        if(this.day.getName().matches(Day.All.getName())){
            this.length = Day.values().length - 1;
        }
        else{
            this.length = 1;
        }

    }
}
