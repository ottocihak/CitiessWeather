package com.example.citiessweather.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.citiessweather.DetailsActivity;
import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.R;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.databinding.MapFragmentBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Objects;

public class MapFragment extends Fragment {

    private View view;
    private MapFragmentBinding binding;
    private MainViewModel model;
    private SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = MapFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.googleMap);

        return view;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }

    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh_map) {
            onStart();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = data.child("users");
        Log.e("map", "map2 ");
        model.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            DatabaseReference uid = users.child(user.getUid());
            DatabaseReference evidences = uid.child("evidences");

            Log.e("map", "map3 ");

            mapFragment.getMapAsync(map -> {

                Log.e("map", "map4 ");

                if (ActivityCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                } else {
                    map.setMyLocationEnabled(true);
                    MutableLiveData<LatLng> currentPosition = model.getCurrentPosition();

                    Log.e("TAG", "onCreateView: " + currentPosition.getValue());
                    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
                    currentPosition.observe(lifecycleOwner, latLng -> {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        map.animateCamera(cameraUpdate);
                        currentPosition.removeObservers(lifecycleOwner);
                    });

                    evidences.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Evidence evidence = snapshot.getValue(Evidence.class);

                            LatLng position = new LatLng(
                                    Double.parseDouble(evidence.getLat()),
                                    Double.parseDouble(evidence.getLon())
                            );

                            InfoWindowAdapter customInfoWindow = new InfoWindowAdapter(
                                    getActivity()
                            );

                            Marker marker = map.addMarker(new MarkerOptions()
                                    .title(evidence.getWarning())
                                    .position(position)
                                    .snippet(evidence.getAddress())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            marker.setTag(evidence);
                            map.setInfoWindowAdapter(customInfoWindow);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    ArrayList<City> cities = new ArrayList<>();
                    model.getCitiesReferences().addChildEventListener(new ChildEventListener() {


                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            City city = snapshot1.getValue(City.class);
                            cities.add(city);
                            Log.e( "onChildAdded: ", city.getName());

                            LatLng position = new LatLng(
                                    city.getLat(),
                                    city.getLon()
                            );

                            InfoWindowAdapter InfoWindowAdapter = new InfoWindowAdapter(
                                    getActivity()
                            );

                            Marker marker1 = map.addMarker(new MarkerOptions()
                                    .title(city.getMain())
                                    .position(position)
                                    .snippet(city.getTemp())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            marker1.setTag(city);
                            map.setInfoWindowAdapter(InfoWindowAdapter);

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                    map.setOnInfoWindowClickListener(marker -> {

                        LatLng latLon = marker.getPosition();

                        for (City city : cities) {
                            LatLng position = new LatLng(
                                    city.getLat(),
                                    city.getLon()
                            );
                            if (latLon.equals(position)) {
                                Intent intent = new Intent(getContext(), DetailsActivity.class);
                                intent.putExtra("city", city);
                                startActivity(intent);
                            }

                        }
                    });
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
