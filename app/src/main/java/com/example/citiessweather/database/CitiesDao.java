package com.example.citiessweather.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.citiessweather.cities.City;

import java.util.List;

@Dao
public interface CitiesDao {

    @Query("SELECT * FROM city")
    LiveData<List<City>> getCities();

    @Insert
    void addCity(City city);

    @Insert
    void addCities(List<City> cities);

    @Delete
    void deleteCity(City city);

    @Query("DELETE FROM city")
    void deleteCards();
}