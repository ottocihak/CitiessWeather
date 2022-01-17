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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.citiessweather.DetailsActivity;
import com.example.citiessweather.R;
import com.example.citiessweather.SettingsActivity;
import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.CitiesAdapter;
import com.example.citiessweather.cities.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
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

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String cityName = preferences.getString("cityNameKey","");

        Log.d("TAG", "onCreateView: " + cityName);

        citiesWeather = view.findViewById(R.id.citiesWeather);

        ArrayList<City> items = new ArrayList<>();

        citiesAdapter = new CitiesAdapter(
                getContext(),
                R.layout.cities_row,
                items
        );

        sharedViewModel = ViewModelProviders.of(getActivity()).get(
                SharedViewModel.class
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

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getCities(cityName).observe(getViewLifecycleOwner(), cities -> {
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

    private class RefreshDataTask extends AsyncTask<Void, Void, ArrayList<City>>  {
        @Override
        protected ArrayList<City> doInBackground(Void... voids) {
            CitiesAPI api = new CitiesAPI();
            ArrayList<City> result = api.getCitiesByCapital();
            Log.d("DEBUG", result.toString());
            return result;
        }
        @Override
        protected void onPostExecute(ArrayList<City> cities) {
            citiesAdapter.clear();
            for (City city : cities) {
                citiesAdapter.add(city);
            }
        }
    }

    private void refresh() {
        mViewModel.reload();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String cityName = preferences.getString("cityNameKey","");

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getCities(cityName).removeObservers(getViewLifecycleOwner());

        if (cityName.equals("")){
            mViewModel.getCities(cityName).observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        }

        if (preferences.getBoolean("alphaOrder",false)){
            mViewModel.getCities(cityName).observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        } else if (preferences.getBoolean("coldestToHottest",false)){
            mViewModel.getCitiesOrderedByTempC().observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        } else if (preferences.getBoolean("hottestToColdest",false)) {
            mViewModel.getCitiesOrderedByTempH().observe(getViewLifecycleOwner(), cities -> {
                citiesAdapter.clear();
                citiesAdapter.addAll(cities);
            });
        }



    }
}