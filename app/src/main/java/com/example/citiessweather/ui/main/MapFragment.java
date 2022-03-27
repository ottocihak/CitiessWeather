package com.example.citiessweather.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.Evidence.EvidenceInfoWindowAdapter;
import com.example.citiessweather.R;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.cities.CityInfoWindowAdapter;
import com.example.citiessweather.databinding.MapFragmentBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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

public class MapFragment extends Fragment {

    private View view;
    private MapFragmentBinding binding;
    private MainViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = MapFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.googleMap);

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

                }else {
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

                            EvidenceInfoWindowAdapter customInfoWindow = new EvidenceInfoWindowAdapter(
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

                    model.getCitiesReferences().addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            City city = snapshot.getValue(City.class);

                            LatLng position = new LatLng(
                                    city.getLat(),
                                    city.getLon()
                            );

                            CityInfoWindowAdapter customInfoWindow = new CityInfoWindowAdapter(
                                    getActivity()
                            );

                            Marker marker = map.addMarker(new MarkerOptions()
                                    .title(city.getMain())
                                    .position(position)
                                    .snippet(city.getTemp())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            marker.setTag(city);
                            map.setInfoWindowAdapter(customInfoWindow);
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
                }
            });
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
