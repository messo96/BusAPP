package com.example.busapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.busapp.Utils.Coordinates;
import com.example.busapp.Utils.Utilities;

import com.example.busapp.database.User;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ProfileFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    boolean login;
    private FirebaseFirestore db;
    private final ActionBar actionBar;

    public ProfileFragment(final boolean flagLogin, final androidx.appcompat.app.ActionBar actionBar) {
        super();
        this.login = flagLogin;
        this.actionBar = actionBar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        db = FirebaseFirestore.getInstance();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!login) {
            actionBar.setTitle("Registation");
            return inflater.inflate(R.layout.profile_registration, container, false);

        }
        else{
            actionBar.setTitle("Login");
            return inflater.inflate(R.layout.profile_login, container, false);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean logged = sharedPreferences.getBoolean("logged", false);

        if (!login) { // Registration
            EditText editText_username = view.findViewById(R.id.username_registration);
            EditText email = view.findViewById(R.id.email_registration);
            EditText password = view.findViewById(R.id.password_registration);
            Button button_save = view.findViewById(R.id.button_registration_user);


            view.findViewById(R.id.redirect_login).setOnClickListener(l -> {
                    getActivity().getSupportFragmentManager().popBackStack();
                    Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view);
            });

            Activity activity = getActivity();


            button_save.setOnClickListener(l -> {
                User user = new User(String.valueOf(editText_username.getText()), String.valueOf(email.getText()), String.valueOf(password.getText()), new Coordinates(43.4, 343.3));
                Map<String, Object> map = new HashMap<>();
                map.put("username", user.getUsername());
                map.put("email", user.getEmail());
                map.put("password", user.getPassword());

                db.collection("User")
                        .get().addOnCompleteListener(task -> {

                    map.put("id", task.getResult().size());

                    db.collection("User").add(map)
                            .addOnSuccessListener(documentReference -> {

                                Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_LONG).show();
                                sharedPreferences.edit()
                                        .putInt("id", Integer.parseInt(String.valueOf(map.get("id"))))
                                        .putString("username", "")
                                        .putString("email", String.valueOf(email.getText()))
                                        .putBoolean("logged", true)
                                        .apply();
                            })
                            .addOnFailureListener(f -> {
                                new AlertDialog.Builder(getContext()).setMessage("Cannot create User\nThis email is already registered").show();
                            });
                });

            });

        } else { // Login
            EditText email = view.findViewById(R.id.email_login);
            EditText password = view.findViewById(R.id.password_login);
            Button button_save = view.findViewById(R.id.button_login_user);

            String email_text = sharedPreferences.getString("email", "email@email.it");

            if (logged) {
                email.setEnabled(false);
                email.setText(email_text);
                password.setVisibility(View.INVISIBLE);
                button_save.setText("Logged");
                button_save.setEnabled(false);
                try {
                    setUpGramification();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                email.setText(sharedPreferences.getString("username", ""));
                button_save.setOnClickListener(v -> {

                        String sEmail = String.valueOf(email.getText());
                        String sPassword = String.valueOf(password.getText());
                        Task<QuerySnapshot> tResult = db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            boolean check = false;
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot query : task.getResult()) {
                                    if (sEmail.matches(query.getData().get("email").toString())
                                            && sPassword.matches(query.getData().get("password").toString())) {
                                        sharedPreferences.edit()
                                                .putInt("id", Integer.parseInt(query.get("id").toString()))
                                                .putString("username", String.valueOf(email.getText()))
                                                .putString("email", String.valueOf(email.getText()))
                                                .putBoolean("logged", true)
                                                .apply();
                                        check = true;
                                        break;
                                    }
                                }
                                if(check){

                                    Toast.makeText(getContext(), "Logged!", Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    try {
                                        setUpGramification();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                                else{
                                    Toast.makeText(getContext(), "Credential are not correct, please retry!", Toast.LENGTH_LONG).show();
                                    password.setText("");
                                }
                            }
                        });

                });


            }

            TextView textView_register = view.findViewById(R.id.redirect_register);

            if(logged){
                textView_register.setText("Log out");
                view.findViewById(R.id.redirect_register).setOnClickListener(v ->  {
                    sharedPreferences.edit()
                            .putBoolean("logged", false)
                            .apply();
                        getActivity().getSupportFragmentManager().popBackStack();
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view);
                });

            }
            else{
                view.findViewById(R.id.redirect_register).setOnClickListener(v ->  {
                        getActivity().getSupportFragmentManager().popBackStack();
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(false, actionBar), "RegistrationFragment", R.id.fragment_container_view);
                });
            }

        }

    }

    private void setUpGramification() throws IOException {
        TextView textView = getActivity().findViewById(R.id.listView_myactivity);
        textView.setVisibility(View.VISIBLE);
        ListView listView = getActivity().findViewById(R.id.listView_gramification);
        List<Gramification> list = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.gramification_detail)));
        String line = reader.readLine();
        while (line != null) {
            String[] data = line.split("-");//TITLE-DESCRIPTION-EXP
            list.add(new Gramification(data[0], data[1], Integer.parseInt(data[2])) );

            line = reader.readLine();
        }

        GramificationAdapter gramificationAdapter = new GramificationAdapter(getContext(), sharedPreferences.getInt("id", -1), list);
        listView.setAdapter(gramificationAdapter);
    }
}