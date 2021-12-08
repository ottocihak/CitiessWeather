package com.example.citiessweather.cities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class CitiesAPI {

    private final String BASE_URL = "https://api.magicthegathering.io";

    ArrayList<City> getCities() {
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("v1")
                .appendPath("cards")
                .build();
        String url = builtUri.toString();
        return doCall(url);
    }

    private ArrayList<City> doCall(String url) {
        try {
            String JsonResponse = HttpUtils.get(url);
            return processJson(JsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<City> processJson(String jsonResponse) {
        ArrayList<City> cities = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONArray jsonCards = data.getJSONArray("cards");

            Log.d("DEBUG",""+jsonCards.length());

            for (int i = 0; i < jsonCards.length(); i++) {
                JSONObject jsonCard = jsonCards.getJSONObject(i);

                City city = new City();
                city.setName(jsonCard.has("name")?jsonCard.getString("name"):"Desconocido");

                Log.d("DEBUG",""+i);

                cities.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cities;
    }

}
