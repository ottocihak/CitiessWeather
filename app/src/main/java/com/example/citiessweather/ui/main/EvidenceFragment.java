package com.example.citiessweather.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.citiessweather.databinding.EvidenceBinding;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EvidenceFragment extends Fragment {

    private View view;
    private EvidenceBinding binding;
    private TextInputEditText mainWeatherText;
    private TextInputEditText minTemText;
    private TextInputEditText maxTemText;
    private TextInputEditText latText;
    private TextInputEditText lonText;
    private TextInputEditText addressText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EvidenceBinding.inflate(inflater);
        view = binding.getRoot();

        mainWeatherText = binding.evidenceMainWeather;
        minTemText = binding.evidenceMinTemp;
        maxTemText = binding.evidenceMaxTemp;
        latText = binding.evidenceLat;
        lonText = binding.evidenceLon;
        addressText = binding.address;



        return view;
    }
}
