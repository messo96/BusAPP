package com.example.busapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.busapp.Utils.Day;
import com.example.busapp.database.Bus.BusRepository;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
        private Context context;
        private Day day;
        private String orario[] = new String[8];
        private LayoutInflater inflter;
        private BusRepository busRepository;
        private Activity activity;

        private Integer idBusStop;
        private String nameBus;

        public CustomAdapter(Activity activity, Context applicationContext,int idBusStop,String nameBus) {
            this.context = applicationContext;
            this.activity = activity;
            this.inflter = (LayoutInflater.from(applicationContext));
            this.busRepository = new BusRepository(activity.getApplication());
            this.nameBus = nameBus;
            this.idBusStop = idBusStop;
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
        busRepository.getHours(idBusStop, nameBus, Day.getDay(position).getName()).observe((LifecycleOwner) activity, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                String s = "";
                for(String h : strings)
                    s += h + "\n";

                text_day.setText(Day.getDay(position).getName());
                text_orari.setText(s);
            }
        });



        return view;
    }
}
