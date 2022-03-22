package com.example.citiessweather.ui.main;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.database.CitiesDao;
import com.example.citiessweather.database.DataManager;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final Application app;
    private final DataManager dataManager;
    private final CitiesDao citiesDao;

    public MainViewModel (Application application) {
        super(application);

        this.app = application;
        this.dataManager = DataManager.getDatabase(
                this.getApplication());
        this.citiesDao = dataManager.getCitiesDao();
    }

    public LiveData<List<City>> getCities(String name) {
        return citiesDao.getCities(name);
    }
    public LiveData<List<City>> getCitiesCloseBy(int lon, int lat) {
        return citiesDao.getCitiesCloseBy(lon,lat);
    }
    public LiveData<List<City>> getCitiesOrderedByTempC() {
        return citiesDao.getCitiesOrderedByTempC();
    }
    public LiveData<List<City>> getCitiesOrderedByTempH() {
        return citiesDao.getCitiesOrderedByTempH();
    }
    public LiveData<List<City>> getCitiesOrderedByTypeWeather() {
        return citiesDao.getCitiesOrderedByTypeWeather();
    }

    public void reload() {
        RefreshDataTask task = new RefreshDataTask();
        task.execute();
    }

    private class RefreshDataTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            CitiesAPI api = new CitiesAPI();
            ArrayList<City> result;

            result = api.getCitiesByCapital();


            citiesDao.deleteCities();
            citiesDao.addCities(result);

            return null;
        }
    }
}