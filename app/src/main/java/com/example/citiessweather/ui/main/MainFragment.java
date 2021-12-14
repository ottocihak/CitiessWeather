package com.example.citiessweather.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.CitiesAdapter;
import com.example.citiessweather.cities.City;

import java.util.ArrayList;
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

        citiesWeather = view.findViewById(R.id.citiesWeather);

        ArrayList<City> items = new ArrayList<>();

        citiesAdapter = new CitiesAdapter(
                getContext(),
                R.layout.cities_row,
                items
        );

        sharedViewModel = ViewModelProviders.of(requireActivity()).get(
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
        mViewModel.getCities().observe(getViewLifecycleOwner(), cities -> {
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

}