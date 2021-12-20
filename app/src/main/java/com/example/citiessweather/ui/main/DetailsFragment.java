package com.example.citiessweather.ui.main;

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

public class DetailsFragment extends Fragment {
    private View view;
    private DetailsFragmentBinding binding;

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
        Log.d("CARD", city.toString());

        binding.cityNameDe.setText(city.getName());
        binding.cityMainDe.setText(city.getMain());
        binding.weatherDes.setText(city.getDescription());
        binding.temp.setText(city.getTemp()+"ºF");
        binding.tempDe.setText(city.getTemp_max()+"ºF Max/"+city.getTemp_min()+"ºF Min");
        binding.humility.setText(city.getHumidity());
        Glide.with(getContext()).load(
                city.getIcon()
        ).into(binding.cityWePic);
    }
}