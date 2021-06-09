package com.example.busapp;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GamificationAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final FirebaseFirestore db;
    private final Context context;
    private final int id;
    private final List<Gamification> list;
    private final List<Integer> listBadge;
    private final ProgressBar progressBar;


    public GamificationAdapter(final Context context, final int idUser, List<Gamification> list, final ProgressBar progressBar ){
        this.context = context;
        this.inflater = (LayoutInflater.from(this.context));
        this.db = FirebaseFirestore.getInstance();
        this.id = idUser;
        this.list = list;
        this.listBadge = getBadges();
        this.progressBar = progressBar;
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
        imageView.setColorFilter(Color.LTGRAY);

        if(position < listBadge.size()){
            imageView.setImageResource(listBadge.get(position));
            checkCompletedTask(position, imageView, textView_description, progressBar, gamification.getExp());
        }
        textView_title.setText(gamification.getTitle());
        textView_description.setText(gamification.getDescription());
        textView_xp.setText(gamification.getExp() + " XP" );


        return view;
    }

    private void checkCompletedTask(final int position, final ImageView imageView, TextView textView_description, ProgressBar progressBar, int exp) {

        switch (position){
            case 0:
                 if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("logged", false))
                     enableGam(imageView, textView_description, progressBar, exp);

                break;
            case 1:
                db.collection("BusStop").whereEqualTo("user_created_id", id).get().addOnSuccessListener(task -> {
                    if(!task.isEmpty())
                        enableGam(imageView, textView_description, progressBar, exp);
                });
                break;
            case 2:
                int id_2 = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
                db.collection("BusStop").whereEqualTo("user_created_id", id_2).get().addOnSuccessListener(task -> {
                    if(task.size() >= 10)
                        enableGam(imageView, textView_description, progressBar, exp);
                });
                break;
            case 3:
                int id_3 = PreferenceManager.getDefaultSharedPreferences(context).getInt("id", -1);
                db.collection("Bus").whereEqualTo("user_created_id", id_3).get().addOnSuccessListener(task -> {
                    if(task.size() >= 20)
                        enableGam(imageView, textView_description, progressBar, exp);
                });
                break;
            default:
                System.out.println("If you read this you have to add switch cases badge");
                break;
        }


    }

    private void enableGam(ImageView imageView, TextView textView_description, ProgressBar progressBar, int exp) {
        textView_description.setVisibility(View.VISIBLE);
        imageView.setColorFilter(null);
        progressBar.setProgress(progressBar.getProgress() + exp);
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
