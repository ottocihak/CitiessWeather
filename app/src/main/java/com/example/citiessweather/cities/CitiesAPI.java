package com.example.citiessweather.cities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CitiesAPI {

    private final String BASE_URL = "https://countriesnow.space/api/";

    public ArrayList<City> getCitiesByCapital() {
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("v0.1")
                .appendPath("countries")
                .appendPath("capital")
                .build();
        String url = builtUri.toString();
        return doCall(url);
    }

    HashMap<String,String> getWeather(String city){
        try {
            Log.d("DEBUG",""+city);
            String url = "https://api.openweathermap.org/data/2.5/weather?q="+city.toLowerCase(Locale.ROOT)+"&appid=0fa3458c2e34936b0dad44dc6699956c";
            String jsonResponse = HttpUtils.get(url);
            return processJsonWeather(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<City> doCall(String url) {
        try {
            String jsonResponse = HttpUtils.get(url);
            return processJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<String,String> processJsonWeather(String jsonResponse) {
        HashMap<String,String> weather = new HashMap<>();
        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONArray weatherCity = data.getJSONArray("weather");
            JSONObject main = data.getJSONObject("main");

            Log.d("DEBUG",""+data);

            weather.put("main",weatherCity.getJSONObject(0).getString("main"));
            Log.d("DEBUG",""+weather.get("main"));

            weather.put("description",weatherCity.getJSONObject(0).getString("description"));
            weather.put("temp",main.getString("temp"));
            weather.put("temp_min",main.getString("temp_min"));
            weather.put("temp_max",main.getString("temp_max"));
            weather.put("humidity", main.getString("humidity")+"%");

            Log.d("DEBUG",""+weather.get("main")+weather.get("description"));

            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<City> processJson(String jsonResponse) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONArray jsonCities = data.getJSONArray("data");

            Log.d("DEBUG",""+jsonCities.length());

            for (int i = 0; i < jsonCities.length(); i++) {

                JSONObject jsonCity = jsonCities.getJSONObject(i);

                if (jsonCity.getString("name").indexOf(" ") == -1) {
                City city = new City();
                city.setName(jsonCity.getString("name"));
                city.setCountry(jsonCity.getString("country"));

                HashMap<String,String> weather = getWeather(city.getName());

                city.setMain(weather.get("main"));
                city.setDescription(weather.get("description"));
                city.setTemp(weather.get("temp"));
                city.setTemp_max(weather.get("temp_max"));
                city.setTemp_min(weather.get("temp_min"));
                city.setHumidity(weather.get("humidity"));

                Log.d("DEBUG",""+i);

                cities.add(city);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cities;
    }

}
