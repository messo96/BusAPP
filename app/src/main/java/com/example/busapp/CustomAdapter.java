package com.example.busapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.Utils.Day;
import com.example.busapp.Utils.Utilities;
import com.example.busapp.database.Bus.BusRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
        private Context context;
        private Day day;
        private String orario[] = new String[Day.values().length];
        private LayoutInflater inflter;
        private BusRepository busRepository;
        private Activity activity;
        FirebaseFirestore db;

        private Integer idBusStop;
        private String nameBus;

        public CustomAdapter(Activity activity, Context applicationContext,int idBusStop,String nameBus) {
            this.context = applicationContext;
            this.activity = activity;
            this.inflter = (LayoutInflater.from(applicationContext));
            this.busRepository = new BusRepository(activity.getApplication());
            this.nameBus = nameBus;
            this.idBusStop = idBusStop;
            this.db = FirebaseFirestore.getInstance();
        }

        @Override
        public int getCount() {
            return orario.length;
        }

        @Override
        public Object getItem(int i) {
            return orario[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.activity_listview, null);

        TextView text_day =  view.findViewById(R.id.text_day);
        TextView text_orari =  view.findViewById(R.id.text_orari);
        //Toast.makeText(activity.getApplicationContext(), String.valueOf(idBusStop), Toast.LENGTH_SHORT).show();
        db.collection("Time")
                .whereEqualTo("busStop_id", idBusStop)
                .whereEqualTo("name_bus", nameBus)
                .whereEqualTo("day", Day.getDay(position).getName())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String s = "";
                for(QueryDocumentSnapshot q : queryDocumentSnapshots)
                    s += String.valueOf(q.get("time")) + "\n";
                text_day.setText(Day.getDay(position).getName());
                text_orari.setText(s);
            }
        });

        view.findViewById(R.id.btn_add_hour).setOnClickListener(l ->{
            int id_creator = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
            Intent intent = new Intent(activity.getApplicationContext(), AddTimeActivity.class);
            intent.putExtra("busStop_id", idBusStop);
            intent.putExtra("id", id_creator);
            intent.putExtra("name_bus", nameBus);
            intent.putExtra("day", Day.getDay(position).getName());

            activity.startActivity(intent);
        });


        return view;
    }
}
