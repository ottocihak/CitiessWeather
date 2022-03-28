package com.example.citiessweather.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.citiessweather.cities.City;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<City> selected = new MutableLiveData<City>();

    public void select(City city) {
        selected.setValue(city);
    }
    public LiveData<City> getSelected() {
        return selected;
    }

}
