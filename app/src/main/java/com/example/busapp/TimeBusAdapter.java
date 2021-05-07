package com.example.busapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busapp.Utils.Day;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeBusAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private String nameBus;
    private Integer idBusStop;
    private Context context;
    private Day day;
    private List<Pair<String,Integer>> list;
    private FirebaseFirestore db;

   public TimeBusAdapter(final Context context, final Integer idBusStop, final String nameBus, Day day, List<Pair<String, Integer>> list){
       this.inflater = (LayoutInflater.from(context));
       this.context = context;
       this.idBusStop = idBusStop;
       this.nameBus = nameBus;
       this.day = day;
       this.list = list;
       db = FirebaseFirestore.getInstance();
   }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.activity_listview_time, null);
        Pair<String, Integer> pair = list.get(position);
        Button btn_like = view.findViewById(R.id.btn_like);
        Button btn_dislike = view.findViewById(R.id.btn_dislike);
        TextView textView_time = view.findViewById(R.id.text_time);
        TextView textView_feedback = view.findViewById(R.id.text_feedback);
        int id_creator = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
        textView_time.setText(pair.first);
        textView_feedback.setText(String.valueOf(pair.second));

        btn_like.setOnClickListener(l -> {
                    db.collection("Time")
                            .whereEqualTo("busStop_id", idBusStop)
                            .whereEqualTo("name_bus", nameBus)
                            .whereEqualTo("day", day)
                            .get().addOnSuccessListener(s -> {
                        db.collection("Time")
                                .document(s.getDocuments().get(0).getId())
                                .update("feedback", Integer.parseInt(String.valueOf(s.getDocuments().get(0).get("feedback"))) + 1);
                        textView_feedback.setText(String.valueOf(Integer.parseInt(String.valueOf(s.getDocuments().get(0).get("feedback"))) + 1)) ;

                    });
                });

            btn_dislike.setOnClickListener(l -> {
                        db.collection("Time")
                                .whereEqualTo("busStop_id", idBusStop)
                                .whereEqualTo("name_bus", nameBus)
                                .whereEqualTo("day", day)
                                .get().addOnSuccessListener(s -> {
                            db.collection("Time")
                                    .document(s.getDocuments().get(0).getId())
                                    .update("feedback", Integer.parseInt(String.valueOf(s.getDocuments().get(0).get("feedback"))) - 1);
                            textView_feedback.setText(String.valueOf(Integer.parseInt(String.valueOf(s.getDocuments().get(0).get("feedback"))) - 1)) ;

                        });
                    });


          /*  Map<String, Object> map = new HashMap<>();
            map.put("busStop_id", idBusStop);
            map.put("name_bus", nameBus);
            map.put("id", id_creator);
            map.put("day", day.getName());

            db.collection("Feedback")
                    .whereEqualTo("busStop_id", idBusStop)
                    .whereEqualTo("name_bus", nameBus)
                    .whereEqualTo("day", day)
                    .whereEqualTo("id", id_creator)
                    .get().addOnSuccessListener(s ->{

            }).addOnFailureListener(f ->{
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
            });
           */


        return view;
    }

}
