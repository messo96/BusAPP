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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.Utils.Utilities;
import com.example.busapp.ViewModel.ListViewModel;
import com.example.busapp.database.User;
import com.example.busapp.database.UserRepository;

import java.util.ArrayList;
import java.util.List;


    public class ProfileFragment extends Fragment {
        private UserRepository userRepository;
        private SharedPreferences sharedPreferences;
        private ListViewModel listViewModel;
        private List<User> list = new ArrayList<>();
        boolean login;

        public ProfileFragment(final boolean flagLogin) {
            super();
            this.login = flagLogin;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            userRepository = new UserRepository(getActivity().getApplication());
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            String email = sharedPreferences.getString("email", "email");
            if (!login)
                return inflater.inflate(R.layout.profile_registration, container, false);
            else
                return inflater.inflate(R.layout.profile_login, container, false);


        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (!login) { // Registration
                EditText editText_username = view.findViewById(R.id.username_registration);
                EditText email = view.findViewById(R.id.email_registration);
                EditText password = view.findViewById(R.id.password_registration);
                Button button_save = view.findViewById(R.id.button_registration_user);


                view.findViewById(R.id.redirect_login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(true), "LoginFragment", R.id.fragment_container_view);
                    }
                });


                listViewModel = new ViewModelProvider((ViewModelStoreOwner) getActivity()).get(ListViewModel.class);
                Activity activity = getActivity();

                editText_username.setText(sharedPreferences.getString("username", "Username"));


                editText_username.setText(sharedPreferences.getString("username", "Username"));
                button_save.setOnClickListener(l -> {
                    userRepository.checkUser(String.valueOf(email.getText())).observe((LifecycleOwner) activity, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer count) {
                            if (count == 0) {
                                userRepository.addUser(new User(String.valueOf(editText_username.getText()), String.valueOf(email.getText()), String.valueOf(password.getText()), new Coordinates(41.0, 200.0)));
                                sharedPreferences.edit()
                                        .putString("email", String.valueOf(email.getText()))
                                        .putBoolean("logged", true)
                                        .apply();
                            } else {
                                new AlertDialog.Builder(getContext()).setMessage("This email is already registered").show();
                            }
                        }
                    });
                });


            } else { // Login
                EditText email = view.findViewById(R.id.email_login);
                EditText password = view.findViewById(R.id.password_login);
                Button button_save = view.findViewById(R.id.button_login_user);

                boolean logged = sharedPreferences.getBoolean("logged", false);
                String email_text = sharedPreferences.getString("email", "email@email.it");

                if (logged) {
                    email.setEnabled(false);
                    email.setText(email_text);
                    password.setVisibility(View.INVISIBLE);
                    button_save.setText("Logged");
                    button_save.setEnabled(false);

                } else {
                    button_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userRepository.login(String.valueOf(email.getText()), String.valueOf(password.getText())).observe((LifecycleOwner) getActivity(), new Observer<User>() {
                                @Override
                                public void onChanged(User user) {
                                    if (user != null) {
                                        sharedPreferences.edit()
                                                .putString("email", String.valueOf(email.getText()))
                                                .putInt("id", user.getUser_id())
                                                .putBoolean("logged", true)
                                                .apply();

                                        Toast.makeText(getContext(), "Logged!", Toast.LENGTH_LONG).show();
                                        getActivity().getSupportFragmentManager().popBackStack();

                                        // Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(true), "LoginFragment", R.id.fragment_container_view);
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Credential are not correct, please retry!", Toast.LENGTH_LONG).show();
                                        password.setText("");
                                    }

                                }

                            });
                        }
                    });

                }

                view.findViewById(R.id.redirect_register).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new ProfileFragment(false), "RegistrationFragment", R.id.fragment_container_view);
                    }
                });
            }


        }
    }
