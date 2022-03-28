package com.example.citiessweather.ui.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.City;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {

    private final Application app;
    private MutableLiveData<City> currentCitys = new MutableLiveData<>();
    private String currentCity;
    private String currentCountry;

    private final MutableLiveData<String> currentAddress = new MutableLiveData<>();
    private final MutableLiveData<String> checkPermission = new MutableLiveData<>();
    private final MutableLiveData<String> checkPermissionAudio = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBar = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentPosition = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<Query> query = new MutableLiveData<>();
    private DatabaseReference citiesReferences;

    private boolean trackingLocation;
    FusedLocationProviderClient fusedLocationClient;


    public MainViewModel (Application application) {
        super(application);

        this.app = application;

        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        citiesReferences = data.child("cities");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication().getApplicationContext());
    }

    public void setFusedLocationClient(FusedLocationProviderClient fusedLocationClient) {
        this.fusedLocationClient = fusedLocationClient;
    }

    public LiveData<String> getCurrentAddress() {
        return currentAddress;
    }

    public MutableLiveData<Boolean> getProgressBar() {
        return progressBar;
    }

    public MutableLiveData<LatLng> getCurrentPosition () {return currentPosition;}

    public LiveData<String> getCheckPermission() {
        return checkPermission;
    }

    public LiveData<String> getCheckPermissionAudio() {
        return checkPermissionAudio;
    }

    public LiveData<FirebaseUser> getCurrentUser () {
        if (currentUser == null){
            currentUser = new MutableLiveData<>();
        }
        return currentUser;
    }

    public LiveData<Query> getQuery () {return query;}

    public DatabaseReference getCitiesReferences () {return citiesReferences;}

    public LiveData<City> getCurrentCity () {return currentCitys;}

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser.postValue(currentUser);
    }

    public void setQuery(Query query) {
        this.query.postValue(query);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (trackingLocation) {
                new FetchAddressTask(
                        getApplication().getApplicationContext()
                ).execute(
                        locationResult.getLastLocation()
                );
            }
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void switchTrackingLocation() {
        if (!trackingLocation) {
            startTrackingLocation(true);
        } else {
            stopTrackingLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void startTrackingLocation(boolean needsChecking) {
        if (needsChecking) {
            checkPermission.postValue("check");
        } else {
            fusedLocationClient.requestLocationUpdates(
                    getLocationRequest(),
                    locationCallback,
                    null
            );

            currentAddress.postValue("Loading...");

            progressBar.postValue(true);
            trackingLocation = true;
        }
    }


    public void stopTrackingLocation() {
        if (trackingLocation) {
            fusedLocationClient.removeLocationUpdates (locationCallback);
            trackingLocation = false;
            progressBar.postValue(false);
        }
    }

    public void reload() {
        RefreshDataTask task = new RefreshDataTask();
        task.execute();
    }

    public void addNewCity () {
        boolean[] found = {false};

        citiesReferences.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    if (name.equals(currentCity)) {
                        Log.e("TAG", "String found!");
                        found[0] = true;
                        City city = ds.getValue(City.class);
                        currentCitys.postValue(city);
                    }
                    list.add(name);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        try {
            if (!found[0]) {
                CitiesAPI api = new CitiesAPI();
                ArrayList<City> result;

                result = api.getCity(currentCity,currentCountry);
                City city = result.get(0);
                currentCitys.postValue(city);
                DatabaseReference citiesReference = citiesReferences.push();
                citiesReference.setValue(city);
            }
        } catch (Exception e) {
            Log.e("addNewCity: ", ""+ e.getMessage());
        }
    }

    private class RefreshDataTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            CitiesAPI api = new CitiesAPI();
            ArrayList<City> result;

            result = api.getCitiesByCapital();

            citiesReferences.removeValue();

            Log.e("doInBackground: ", ""+result.size() );

            for (City citiesList:
                 result) {
                DatabaseReference citiesReference = citiesReferences.push();
                citiesReference.setValue(citiesList);
            }

            if (currentCity != null) {
                addNewCity();
            }

            return null;
        }
    }

    private class FetchAddressTask extends AsyncTask<Location, Void, String> {
        private final String TAG = FetchAddressTask.class.getSimpleName();
        private Context myContext;

        FetchAddressTask(Context applicationContext) {
            myContext = applicationContext;
        }

        @Override
        protected String doInBackground(Location... locations) {
            Log.e("doIt", "do it ");
            Geocoder geocoder = new Geocoder(myContext,
                    Locale.getDefault());
            Location location = locations[0];

            List<Address> addresses = null;
            String resultMessage = "";

            try {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                currentPosition.postValue(latLng);
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                currentCity = addresses.get(0).getLocality();
                currentCountry = addresses.get(0).getCountryName();

                if (addresses == null || addresses.size() == 0) {
                    if (resultMessage.isEmpty()) {
                        resultMessage = "There was no address found";
                        Log.e(TAG, resultMessage);
                    }
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressParts = new ArrayList<>();

                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressParts.add(address.getAddressLine(i));
                    }

                    resultMessage = TextUtils.join("\n", addressParts);
                }
            } catch (IOException ioException) {
                resultMessage = "Service not available";
                Log.e(TAG, resultMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                resultMessage = "Coordinates no valid";
                Log.e(TAG, resultMessage + ". " +
                                "Latitude = " + location.getLatitude() +
                                ", Longitude = " + location.getLongitude(),
                        illegalArgumentException);
            }
            return resultMessage;
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            currentAddress.postValue(address);
            progressBar.postValue(false);
        }
    }
}