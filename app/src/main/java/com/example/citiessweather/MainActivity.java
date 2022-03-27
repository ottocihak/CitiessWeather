package com.example.citiessweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.citiessweather.databinding.MainActivityBinding;
import com.example.citiessweather.ui.main.EvidenceFragment;
import com.example.citiessweather.ui.main.EvidenceListFragment;
import com.example.citiessweather.ui.main.MainFragment;
import com.example.citiessweather.ui.main.MainViewModel;
import com.example.citiessweather.ui.main.MapFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int RC_SIGN_IN = 0;
    FusedLocationProviderClient fusedLocationClient;

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

        model.setFusedLocationClient(fusedLocationClient);
        model.getCheckPermission().observe(this, permission -> checkPermission());

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

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )
                            )
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                model.setCurrentUser(user);
            }
        }
    }

    void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else {
            model.startTrackingLocation(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                model.switchTrackingLocation();
            } else {
                Toast.makeText(this,
                        "Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}