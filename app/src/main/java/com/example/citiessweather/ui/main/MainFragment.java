package com.example.citiessweather.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.citiessweather.databinding.MainFragmentBinding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.citiessweather.DetailsActivity;
import com.example.citiessweather.R;
import com.example.citiessweather.SettingsActivity;
import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.CitiesAdapter;
import com.example.citiessweather.cities.City;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private View view;
    private MainFragmentBinding binding;

    private MainViewModel mainModel;
    private ListView citiesWeather;
    private CitiesAdapter citiesAdapter;
    private SharedViewModel sharedViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = MainFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        mainModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        citiesWeather = binding.citiesWeather;

        ArrayList<City> items = new ArrayList<>();

        citiesAdapter = new CitiesAdapter(
                getContext(),
                R.layout.cities_row,
                items
        );

        citiesWeather.setAdapter(citiesAdapter);

        citiesWeather.setOnItemClickListener((adapter, fragment, i, l) -> {
            City city = (City) adapter.getItemAtPosition(i);
            if (!isTablet()) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            } else {
                sharedViewModel.select(city);
            }
        });

        mainModel.getCities("").observe(getViewLifecycleOwner(), cities -> {
            citiesAdapter.clear();
            citiesAdapter.addAll(cities);
        });

        return view;
    }

    boolean isTablet() {
        return getResources().getBoolean(R.bool.tablet);
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }
        if (id == R.id.settings) {
            Intent i = new Intent(getContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void refresh() {
        mainModel.reload();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String cityName = preferences.getString("cityNameKey","");

        mainModel.getCities(cityName).removeObservers(getViewLifecycleOwner());

        if (cityName.equals("")){
            mainModel.getCities(cityName).observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        }

        if (preferences.getBoolean("alphaOrder",false)){
            mainModel.getCities(cityName).observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        } else if (preferences.getBoolean("coldestToHottest",false)){
            mainModel.getCitiesOrderedByTempC().observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        } else if (preferences.getBoolean("hottestToColdest",false)) {
            mainModel.getCitiesOrderedByTempH().observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        } else if (preferences.getBoolean("typeOfWeather",false)) {
            mainModel.getCitiesOrderedByTempH().observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        }

        FirebaseListOptions<City> options = new FirebaseListOptions.Builder<City>()
                .setQuery(query, City.class)
                .setLayout(R.layout.cities_row)
                .setLifecycleOwner(this)
                .build();

        FirebaseListAdapter<City> adapter = new FirebaseListAdapter<City>(options) {
            @Override
            protected void populateView(View v, City city, int position) {
                TextView cityName = v.findViewById(R.id.cityName);
                TextView mainWeather = v.findViewById(R.id.cityMainWeather);
                ImageView weatherIcon = v.findViewById(R.id.weatherIcon);

                cityName.setText(city.getName());
                mainWeather.setText(city.getMain());
                Glide.with(requireContext())
                        .load(city.getIcon())
                        .into(weatherIcon);
            }
        };

        ListView listView = binding.notificationList;
        listView.setAdapter(adapter);

    }
}