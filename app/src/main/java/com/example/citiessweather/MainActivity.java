package com.example.citiessweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.citiessweather.databinding.MainActivityBinding;
import com.example.citiessweather.ui.main.EvidenceFragment;
import com.example.citiessweather.ui.main.EvidenceListFragment;
import com.example.citiessweather.ui.main.MainFragment;
import com.example.citiessweather.ui.main.MainViewModel;
import com.example.citiessweather.ui.main.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;

    FragmentManager manager = getSupportFragmentManager();
    private MainViewModel model;

    final Fragment mainFragment = new MainFragment();
    final Fragment evidenceFragment = new EvidenceFragment();
    final Fragment listFragment = new EvidenceListFragment();
    final Fragment mapFragment = new MapFragment();

    Fragment active = mainFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_main:
                manager.beginTransaction().hide(active).show(mainFragment).commit();
                active = mainFragment;
                return true;
            case R.id.navigation_evidence:
                manager.beginTransaction().hide(active).show(evidenceFragment).commit();
                active = evidenceFragment;
                return true;
            case R.id.navigation_list:
                manager.beginTransaction().hide(active).show(listFragment).commit();
                active = listFragment;
                return true;
            case R.id.navigation_map:
                manager.beginTransaction().hide(active).show(mapFragment).commit();
                active = mapFragment;
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = new ViewModelProvider(this).get(MainViewModel.class);

        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        manager.beginTransaction()
                .add(R.id.fragment_selected, mainFragment, "1")
                .hide(mainFragment)
                .commit();

        manager.beginTransaction()
                .add(R.id.fragment_selected, evidenceFragment, "2")
                .hide(evidenceFragment)
                .commit();

        manager.beginTransaction()
                .add(R.id.fragment_selected, listFragment, "3")
                .hide(listFragment)
                .commit();

        manager.beginTransaction()
                .add(R.id.fragment_selected, mapFragment, "4")
                .hide(mapFragment)
                .commit();

        nav.setSelectedItemId(R.id.navigation_main);

//        shareViewModel.setFusedLocationClient(fusedLocationClient);
//        shareViewModel.getCheckPermission().observe(this, permission -> checkPermission());
    }
}