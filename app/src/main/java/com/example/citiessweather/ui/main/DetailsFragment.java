package com.example.citiessweather.ui.main;

import com.example.citiessweather.DetailsActivity;
import com.example.citiessweather.R;
import com.example.citiessweather.cities.CitiesAdapter;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.databinding.DetailsFragmentBinding;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailsFragment extends Fragment {
    private View view;
    private DetailsFragmentBinding binding;

    private ListView citiesWeather;
    private CitiesAdapter citiesAdapter;
    private MainViewModel mViewModel;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DetailsFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        Intent i = getActivity().getIntent();

        if (i != null) {
            City city = (City) i.getSerializableExtra("city");

            if (city != null) {
                updateUi(city);

                citiesWeather = view.findViewById(R.id.citiesCloseByList);

                ArrayList<City> items = new ArrayList<>();

                citiesAdapter = new CitiesAdapter(
                        getContext(),
                        R.layout.cities_row,
                        items
                );

                citiesWeather.setAdapter(citiesAdapter);

                citiesWeather.setOnItemClickListener((adapter, fragment, j, l) -> {
                    City secondCity = (City) adapter.getItemAtPosition(j);
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("city", secondCity);
                        startActivity(intent);
                });

                mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
                mViewModel.getCitiesCloseBy(city.getLon(),city.getLat()).observe(getViewLifecycleOwner(), cities -> {
                    citiesAdapter.clear();
                    citiesAdapter.addAll(cities);
                });

            }
        }

        SharedViewModel sharedViewModel = ViewModelProviders.of(
                getActivity()
        ).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<City>() {
            @Override
            public void onChanged(City city) {
                updateUi(city);
            }
        });



        return view;
    }

    private void updateUi(City city) {

        binding.cityNameDe.setText(city.getName());
        binding.cityMainDe.setText(city.getMain());
        binding.weatherDes.setText("Weather:"+'\n'+city.getDescription());
        binding.citiesCloseBy.setText("Cities Close By");
        binding.temp.setText("Temp:"+'\n'+city.getTemp()+"ºF");
        binding.tempDe.setText('\n'+city.getTemp_max()+"ºF Max/"+city.getTemp_min()+"ºF Min");
        binding.humility.setText("Humility"+'\n'+city.getHumidity());
        Glide.with(getContext()).load(
                city.getIcon()
        ).into(binding.cityWePic);
    }
}