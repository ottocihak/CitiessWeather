package com.example.citiessweather.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.citiessweather.databinding.EvidenceFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EvidenceListFragment extends Fragment {

    private View view;
    private EvidenceFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EvidenceFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        return view;
    }
}
