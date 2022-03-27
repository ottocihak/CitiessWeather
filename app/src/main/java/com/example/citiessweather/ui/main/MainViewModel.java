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
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.citiessweather.R;
import com.example.citiessweather.cities.CitiesAPI;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.database.CitiesDao;
import com.example.citiessweather.database.DataManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {

    private final Application app;
    private final DataManager dataManager;
    private final CitiesDao citiesDao;
    private LiveData<List<City>> cities;

    private final MutableLiveData<String> currentAddress = new MutableLiveData<>();
    private final MutableLiveData<String> checkPermission = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBar = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentPosition = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<DatabaseReference> evidenceRef = new MutableLiveData<>();
    private DatabaseReference citiesReferences;

    private boolean trackingLocation;
    FusedLocationProviderClient fusedLocationClient;


    public MainViewModel (Application application) {
        super(application);

        this.app = application;
        this.dataManager = DataManager.getDatabase(
                this.getApplication());
        this.citiesDao = dataManager.getCitiesDao();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication().getApplicationContext());
    }

    //---------------------------------------------------
    public LiveData<List<City>> getCities(String name) {
        return citiesDao.getCities(name);
    }
    public LiveData<List<City>> getCitiesCloseBy(int lon, int lat) {
        return citiesDao.getCitiesCloseBy(lon,lat);
    }
    public LiveData<List<City>> getCitiesOrderedByTempC() {
        return citiesDao.getCitiesOrderedByTempC();
    }
    public LiveData<List<City>> getCitiesOrderedByTempH() {
        return citiesDao.getCitiesOrderedByTempH();
    }
    //---------------------------------------------------

    public void cityAdapter (Query query) {
        FirebaseListOptions<City> options = new FirebaseListOptions.Builder<City>()
                .setQuery(query, City.class)
                .setLayout(R.layout.cities_row)
                .setLifecycleOwner(getApplication())
                .build();

        FirebaseListAdapter<City> adapter = new FirebaseListAdapter<City>(options) {
            @Override
            protected void populateView(View v, City city, int position) {
                TextView cityName = v.findViewById(R.id.cityName);
                TextView mainWeather = v.findViewById(R.id.cityMainWeather);
                ImageView weatherIcon = v.findViewById(R.id.weatherIcon);

                cityName.setText(city.getName());
                mainWeather.setText(city.getMain());
                Glide.with(getApplication())
                        .load(city.getIcon())
                        .into(weatherIcon);
            }
        };

//        ListView listView = binding.notificationList;
//        listView.setAdapter(adapter);
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

    public LiveData<String> getCheckPermission() {
        return checkPermission;
    }

    public LiveData<LatLng> getCurrentPosition () {return currentPosition;}

    public LiveData<FirebaseUser> getCurrentUser () {
        if (currentUser == null){
            currentUser = new MutableLiveData<>();
        }
        return currentUser;
    }

    public LiveData<DatabaseReference> getEvidenceRef () {return evidenceRef;}

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser.postValue(currentUser);
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


    private void stopTrackingLocation() {
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

    private class RefreshDataTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            CitiesAPI api = new CitiesAPI();
            ArrayList<City> result;

            result = api.getCitiesByCapital();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            DatabaseReference data = FirebaseDatabase.getInstance().getReference();

            citiesReferences = data.child("cities");
            citiesReferences.removeValue();

            Log.e("doInBackground: ", ""+result.size() );

            for (City citiesList:
                 result) {
                DatabaseReference citiesReference = citiesReferences.push();
                citiesReference.setValue(citiesList);
            }

            citiesDao.deleteCities();
            citiesDao.addCities(result);

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