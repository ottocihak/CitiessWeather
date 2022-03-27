package com.example.citiessweather.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.R;
import com.example.citiessweather.databinding.EvidenceFragmentBinding;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class EvidenceListFragment extends Fragment {

    private View view;
    private EvidenceFragmentBinding binding;
    private MainViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = EvidenceFragmentBinding.inflate(inflater);
        view = binding.getRoot();
        Log.e("onCreateView: ", "hola1");

        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = data.child("users");
        model.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            DatabaseReference uid = users.child(user.getUid());
            DatabaseReference evidences = uid.child("evidences");

            Log.e("onCreateView: ", "hola2");

            FirebaseListOptions<Evidence> options = new FirebaseListOptions.Builder<Evidence>()
                    .setQuery(evidences, Evidence.class)
                    .setLayout(R.layout.evidence_row)
                    .setLifecycleOwner(this)
                    .build();

            FirebaseListAdapter<Evidence> adapter = new FirebaseListAdapter<Evidence>(options) {
                @Override
                protected void populateView(View v, Evidence evidence, int position) {
                    TextView addressText = v.findViewById(R.id.AddressTextRow);
                    TextView warningText = v.findViewById(R.id.DateTextRow);
                    ImageView pic = v.findViewById(R.id.evidencePic);

                    addressText.setText(evidence.getAddress());
                    warningText.setText("warning: " + evidence.getWarning());
                    if (evidence.getPic()!=null) {
                        Glide.with(requireContext())
                                .load(evidence.getPic())
                                .into(pic);
                    } else {
                        Glide.with(requireContext())
                                .load(R.drawable.ic_baseline_warning_amber_24)
                                .into(pic);
                    }
                }
            };

            Log.e("onCreateView: ", "hola2");

            ListView listView = binding.evidenceList;
            listView.setAdapter(adapter);
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
