package com.example.citiessweather.ui.main;

import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.MainActivity;
import com.example.citiessweather.R;
import com.example.citiessweather.cities.City;
import com.example.citiessweather.databinding.DetailsFragmentBinding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DetailsFragment extends Fragment {
    private View view;
    private DetailsFragmentBinding binding;

    private ListView citiesWeather;
    private MainViewModel model;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = DetailsFragmentBinding.inflate(inflater);
        view = binding.getRoot();

        Intent i = getActivity().getIntent();

        if (i != null) {
            City city = (City) i.getSerializableExtra("city");

            if (city != null) {
                updateUi(city);

                Log.e("onCreateView: ", city.getName());

                DatabaseReference data = FirebaseDatabase.getInstance().getReference();
                DatabaseReference users = data.child("users");
                model.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                    DatabaseReference uid = users.child(user.getUid());
                    DatabaseReference evidences = uid.child("evidences");

                    Log.e("onCreateView: ", city.getName()+" 1");

                    Query query = evidences.orderByChild("city").equalTo(city.getName());


                    FirebaseListOptions<Evidence> options = new FirebaseListOptions.Builder<Evidence>()
                            .setQuery(query, Evidence.class)
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
                            if (evidence.getPic() != null) {
                                Glide.with(requireContext())
                                        .load(evidence.getPic())
                                        .into(pic);
                            }
                        }
                    };

                    ListView listView = binding.citiesWarnings;
                    listView.setAdapter(adapter);
                });
            }
        }

        SharedViewModel sharedViewModel = ViewModelProviders.of(
                getActivity()
        ).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<City>() {
            @Override
            public void onChanged(City city) {
                updateUi(city);
            }
        });

        return view;
    }

    private void updateUi(City city) {

        binding.cityNameDe.setText(city.getName());
        binding.cityMainDe.setText(city.getMain());
        binding.weatherDes.setText("Weather:"+'\n'+city.getDescription());
        binding.citiesCloseBy.setText("Warnings");
        binding.temp.setText("Temp:"+'\n'+city.getTemp()+"ºF");
        binding.tempDe.setText('\n'+city.getTemp_max()+"ºF Max/"+city.getTemp_min()+"ºF Min");
        binding.humility.setText("Humility"+'\n'+city.getHumidity());
        Glide.with(getContext()).load(
                city.getIcon()
        ).into(binding.cityWePic);
    }
}