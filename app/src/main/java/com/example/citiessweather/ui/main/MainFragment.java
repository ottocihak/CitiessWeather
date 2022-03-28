package com.example.citiessweather.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.citiessweather.databinding.MainFragmentBinding;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.citiessweather.DetailsActivity;
import com.example.citiessweather.R;
import com.example.citiessweather.SettingsActivity;
import com.example.citiessweather.cities.City;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.Query;

public class MainFragment extends Fragment {

    private View view;
    private MainFragmentBinding binding;

    private MainViewModel mainModel;
    private ListView citiesWeather;
    private SharedViewModel sharedViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = MainFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        mainModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
        if (id == R.id.action_refresh_city) {
            mainModel.addNewCity();
            return true;
        }
        if (id == R.id.settings) {
            Intent i = new Intent(getContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void refresh() {
        mainModel.reload();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String cityName = preferences.getString("cityNameKey","");

        Query queryBase = mainModel.getCitiesReferences().orderByChild("name");
        mainModel.setQuery(queryBase);

        if (!cityName.equals("")){
            Query query = mainModel.getCitiesReferences().orderByChild("name").equalTo(cityName);
            mainModel.setQuery(query);
        } else if (preferences.getBoolean("alphaOrder",false)){
            Query query = mainModel.getCitiesReferences().orderByChild("name");
            mainModel.setQuery(query);
        } else if (preferences.getBoolean("coldestToHottest",false)){
            Query query = mainModel.getCitiesReferences().orderByChild("temp");
            mainModel.setQuery(query);
        } else if (preferences.getBoolean("hottestToColdest",false)) {
            Query query = mainModel.getCitiesReferences().orderByChild("temp");
            mainModel.setQuery(query);
        } else if (preferences.getBoolean("typeOfWeather",false)) {
            Query query = mainModel.getCitiesReferences().orderByChild("main");
            mainModel.setQuery(query);
        }

        mainModel.getQuery().observe(getViewLifecycleOwner(), query -> {

            FirebaseListOptions<City> options = new FirebaseListOptions.Builder<City>()
                    .setQuery(query, City.class)
                    .setLayout(R.layout.cities_row)
                    .setLifecycleOwner(this)
                    .build();

            FirebaseListAdapter<City> adapter = new FirebaseListAdapter<City>(options) {
                @Override
                protected void populateView(View v, City city, int position) {
                    TextView cityName = v.findViewById(R.id.cityName);
                    TextView mainWeather = v.findViewById(R.id.cityMainWeather);
                    ImageView weatherIcon = v.findViewById(R.id.weatherIcon);
                    TextView cityTemp = v.findViewById(R.id.cityTemp);

                    cityName.setText(city.getName());
                    mainWeather.setText(city.getMain());
                    cityTemp.setText(city.getTemp()+" ºF");
                    Glide.with(requireContext())
                            .load(city.getIcon())
                            .into(weatherIcon);
                }
            };

            citiesWeather = binding.citiesWeather;
            citiesWeather.setAdapter(adapter);

            citiesWeather.setOnItemClickListener((adapter1, fragment, i, l) -> {
                City city = (City) adapter.getItem(i);
                if (!isTablet()) {
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("city", city);
                    startActivity(intent);
                } else {
                    sharedViewModel.select(city);
                }
            });
        });
    }
}