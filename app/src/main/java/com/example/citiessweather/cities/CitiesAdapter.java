package com.example.citiessweather.cities;

import com.example.citiessweather.R;
import com.example.citiessweather.databinding.CitiesRowBinding;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import java.util.List;

public class CitiesAdapter extends ArrayAdapter<City> {
    public CitiesAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        City city = getItem(position);

        CitiesRowBinding binding = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            binding = DataBindingUtil.inflate(inflater, R.layout.cities_row,parent,false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        binding.cityName.setText(city.getName());
        binding.cityMainWeather.setText(city.getMain());
        binding.tempRow.setText(city.getTemp()+ "ºF");

        Glide.with(getContext())
                .load(city.getIcon())
                .into(binding.weatherIcon);

        return binding.getRoot();
    }
}
