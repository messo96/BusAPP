    package com.example.busapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.busapp.Utils.Coordinates;
import com.example.busapp.ViewModel.ListViewModel;
import com.example.busapp.database.User;
import com.example.busapp.database.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


    public class ProfileFragment extends Fragment {
    private UserRepository userRepository;
    private SharedPreferences sharedPreferences;
    private ListViewModel listViewModel;
    private List<User> list = new ArrayList<>();

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
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView_last_position = view.findViewById(R.id.textView_last_position);
        EditText editText_username = view.findViewById(R.id.profile_name);
        listViewModel = new ViewModelProvider((ViewModelStoreOwner) getActivity()).get(ListViewModel.class);
        Activity activity = getActivity();

        editText_username.setText(sharedPreferences.getString("username", "Username"));
        String last_position = sharedPreferences.getString("last_coordinates", "NOT YET EXPLORED");
        if(!last_position.equals("NOT YET EXPLORED") && !last_position.isEmpty()) {
            try {
                List<Address> geo = new Geocoder(getContext(), Locale.getDefault())
                        .getFromLocation(Double.parseDouble(last_position.split(";",2)[0]), Double.parseDouble(last_position.split(";",2)[1]), 1);

                if(!geo.isEmpty())
                    textView_last_position
                        .setText(geo.get(0).getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            textView_last_position.setText(last_position);
        Button button_save = view.findViewById(R.id.button_save_profile);
        editText_username.setText(sharedPreferences.getString("username", "Username"));

        button_save.setOnClickListener(l -> {
            //userRepository.deleteAll();
            userRepository.addUser(new User(String.valueOf(editText_username.getText()), new Coordinates((float)0.000,(float)  0.000)));

            //listViewModel.getAll().observe((LifecycleOwner) getActivity(), user1 -> list.addAll(user1));
            listViewModel.getUser(String.valueOf(editText_username.getText())).observe((LifecycleOwner) activity, user1 -> {
                list.add(user1);
                sharedPreferences.edit()
                        .putInt("id", list.get(0).getUser_id())
                        .putString("username", String.valueOf(editText_username.getText()))
                        .putString("last_coordinates", list.get(0).getLast_location().latitudine+";"+ list.get(0).getLast_location().longitudine)
                        .apply();
                Toast.makeText(getContext(), list.get(0).getLast_location().toString(), Toast.LENGTH_SHORT).show();

            });

        });
    }
}
