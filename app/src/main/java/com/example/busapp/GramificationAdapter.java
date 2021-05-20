package com.example.busapp;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GramificationAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final FirebaseFirestore db;
    private final Context context;
    private final int idUser;
    private List<Gamification> list;
    private List<Integer> listBadge;


    public GramificationAdapter(final Context context, final int idUser, List<Gamification> list ){
        this.context = context;
        this.inflater = (LayoutInflater.from(this.context));
        this.db = FirebaseFirestore.getInstance();
        this.idUser = idUser;
        this.list = list;
        this.listBadge = getBadges();
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
        view = inflater.inflate(R.layout.gramification_listview, null);

        Gamification gamification = list.get(position);
        ImageView imageView = view.findViewById(R.id.image_gramification);
        TextView textView_xp = view.findViewById(R.id.text_view_xp);
        TextView textView_title = view.findViewById(R.id.text_view_gramification_title);
        TextView textView_description = view.findViewById(R.id.text_view_gramification_description);

        if(position < listBadge.size()){
            imageView.setImageResource(listBadge.get(position));
            checkCompletedTask(position, imageView);
        }
        textView_title.setText(gamification.getTitle());
        textView_description.setText(gamification.getDescription());
        textView_xp.setText(String.valueOf(gamification.getExp()) + textView_xp.getText() );



        return view;
    }

    private void checkCompletedTask(final int position, final ImageView imageView) {
        final int id = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);

        switch (position){
            case 0:
                 if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("logged", false))
                     imageView.setColorFilter(Color.LTGRAY);

                break;
            case 1:
                Task<QuerySnapshot> t = db.collection("BusStop").whereEqualTo("user_created_id", id).get().addOnSuccessListener(task -> {
                    if(task.isEmpty())
                        imageView.setColorFilter(Color.LTGRAY);
                });
                break;
            case 2:
                int id_2 = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
                Task<QuerySnapshot> ta = db.collection("BusStop").whereEqualTo("user_created_id", id_2).get().addOnSuccessListener(task -> {
                    if(task.size() < 10)
                        imageView.setColorFilter(Color.LTGRAY);
                });
                break;
            case 3:
                int id_3 = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
                Task<QuerySnapshot> tas = db.collection("Bus").whereEqualTo("user_created_id", id_3).get().addOnSuccessListener(task -> {
                    if(task.size() < 20)
                        imageView.setColorFilter(Color.LTGRAY);
                });
                break;
            default:
                System.out.println("If read this you have to add switch badges");
                break;
        }


    }


    private List<Integer> getBadges() {
        List<Integer> list = new ArrayList<>();
        list.add(R.mipmap.ic_gramification_1_round);
        list.add(R.mipmap.ic_gramification_2);
        list.add(R.mipmap.ic_gramification_3);
        list.add(R.mipmap.ic_gramification_4);
        return list;
    }
}
