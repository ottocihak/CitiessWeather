package com.example.citiessweather.cities;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CityInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity activity;

    public CityInfoWindowAdapter(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        View view = activity.getLayoutInflater()
                .inflate(R.layout.info_view, null);

        City city = (City) marker.getTag();

        ImageView markerPic = view.findViewById(R.id.marker_pic);
        TextView markerTitle = view.findViewById(R.id.marker_title);
        TextView markerDes = view.findViewById(R.id.marker_description);

        markerTitle.setText(city.getMain());
        markerDes.setText(city.getTemp()+" ºF");

        Glide.with(activity).load(city.getIcon()).into(markerPic);

        return view;
    }
}
