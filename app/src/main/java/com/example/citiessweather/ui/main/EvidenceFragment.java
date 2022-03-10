package com.example.citiessweather.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.citiessweather.databinding.EvidenceBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EvidenceFragment extends Fragment {

    private View view;
    private EvidenceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EvidenceBinding.inflate(inflater);
        view = binding.getRoot();

        return view;
    }
}
