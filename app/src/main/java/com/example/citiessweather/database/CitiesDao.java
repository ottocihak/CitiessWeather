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

    @Query("SELECT * FROM city WHERE name LIKE '%' || :cityName || '%' ORDER BY name")
    LiveData<List<City>> getCities(String cityName);

    @Query("SELECT * FROM city WHERE (lon < :lonC+15 AND lon > :lonC-15) AND (lat < :latC+15 AND lat > :latC-15)  ORDER BY name")
    LiveData<List<City>> getCitiesCloseBy(int lonC, int latC);

    @Query("SELECT * FROM city ORDER BY `temp` ASC")
    LiveData<List<City>> getCitiesOrderedByTempC();

    @Query("SELECT * FROM city ORDER BY `temp` DESC")
    LiveData<List<City>> getCitiesOrderedByTempH();

    @Query("SELECT * FROM city ORDER BY `main`")
    LiveData<List<City>> getCitiesOrderedByTypeWeather();


    @Insert
    void addCity(City city);

    @Insert
    void addCities(List<City> cities);

    @Delete
    void deleteCity(City city);

    @Query("DELETE FROM city")
    void deleteCities();
}