package com.example.busapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



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
            actionBar.setTitle("Registration");
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
                requireActivity().getSupportFragmentManager().popBackStack();
                Utilities.insertFragment((AppCompatActivity) requireActivity(), new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view);
            });


            button_save.setOnClickListener(l -> {
                hideSoftKeyboard(requireView());
                User user = new User(String.valueOf(editText_username.getText()), String.valueOf(email.getText()), String.valueOf(password.getText()), new Coordinates(43.4, 343.3));
                Map<String, Object> map = new HashMap<>();
                map.put("username", user.getUsername());
                map.put("email", user.getEmail());
                map.put("password", user.getPassword());

                db.collection("User")
                        .get().addOnCompleteListener(task -> {

                    map.put("id", task.getResult().size());

                    db.collection("User")
                            .whereEqualTo("email", user.getEmail())
                            .get().addOnSuccessListener( doc ->{
                        if(doc.getDocuments().isEmpty()) {
                            db.collection("User").add(map)
                                    .addOnSuccessListener(documentReference -> {

                                        sharedPreferences.edit()
                                                .putInt("id", Integer.parseInt(String.valueOf(map.get("id"))))
                                                .putString("username", "")
                                                .putString("email", String.valueOf(email.getText()))
                                                .putBoolean("logged", true)
                                                .apply();

                                        Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_LONG).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                        Utilities.insertFragment((AppCompatActivity) requireActivity(), new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view);

                                    });

                        }
                        else{
                            Snackbar.make(requireContext(), requireView(), "This email is already registered", Snackbar.LENGTH_SHORT)
                                    .setAction("Sign in", a ->
                                        Utilities.insertFragment((AppCompatActivity) requireActivity(),
                                                new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view)
                                    ).show();
                        }
                    }).addOnFailureListener(f ->Toast.makeText(getContext(), "Error" + f, Toast.LENGTH_LONG).show());

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
                password.setVisibility(View.GONE);
                button_save.setText(R.string.logged);
                button_save.setEnabled(false);
                try {
                    setUpGamification();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                email.setText(sharedPreferences.getString("username", ""));
                button_save.setOnClickListener(v -> {
                    hideSoftKeyboard(requireView());
                    String sEmail = String.valueOf(email.getText());
                    String sPassword = String.valueOf(password.getText());
                    db.collection("User")
                            .whereEqualTo("email", sEmail)
                            .whereEqualTo("password", sPassword)
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.getResult().isEmpty()) {
                                    Snackbar.make(requireContext(), requireView(), "Credential are not correct, please retry!", Snackbar.LENGTH_SHORT)
                                            .setAction("Sign up", a ->
                                                Utilities.insertFragment((AppCompatActivity) requireActivity(),
                                                        new ProfileFragment(false, actionBar), "RegistrationFragment", R.id.fragment_container_view)
                                            ).show();

                                    password.setText("");
                                }
                                else {
                                    int id = Integer.parseInt(Objects.requireNonNull(task.getResult().getDocuments().get(0).get("id")).toString());
                                    sharedPreferences.edit()
                                            .putInt("id", id)
                                            .putString("username", String.valueOf(email.getText()))
                                            .putString("email", String.valueOf(email.getText()))
                                            .putBoolean("logged", true)
                                            .apply();
                                    Snackbar.make(requireContext(), requireView(), "Logged!", Snackbar.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                    try {
                                        setUpGamification();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                });


            }

            TextView textView_register = view.findViewById(R.id.redirect_register);

            if(logged){
                textView_register.setText(R.string.logout);
                view.findViewById(R.id.redirect_register).setOnClickListener(v ->  {
                    sharedPreferences.edit()
                            .putBoolean("logged", false)
                            .putInt("id", -1)
                            .apply();
                    requireActivity().getSupportFragmentManager().popBackStack();
                    Utilities.insertFragment((AppCompatActivity) requireActivity(), new ProfileFragment(true, actionBar), "LoginFragment", R.id.fragment_container_view);
                });

            }
            else{
                view.findViewById(R.id.redirect_register).setOnClickListener(v ->  {
                    requireActivity().getSupportFragmentManager().popBackStack();
                    Utilities.insertFragment((AppCompatActivity) requireActivity(), new ProfileFragment(false, actionBar), "RegistrationFragment", R.id.fragment_container_view);
                });
            }

        }

    }

    private void setUpGamification() throws IOException {
        TextView textView = requireActivity().findViewById(R.id.listView_myactivity);
        textView.setVisibility(View.VISIBLE);
        ListView listView = requireActivity().findViewById(R.id.listView_gramification);
        List<Gamification> list = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.gramification_detail)));
        String line = reader.readLine();
        while (line != null) {
            String[] data = line.split("-");//TITLE-DESCRIPTION-EXP
            list.add(new Gamification(data[0], data[1], Integer.parseInt(data[2])) );

            line = reader.readLine();
        }

        GramificationAdapter gramificationAdapter = new GramificationAdapter(getContext(), sharedPreferences.getInt("id", -1), list);
        listView.setAdapter(gramificationAdapter);
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}