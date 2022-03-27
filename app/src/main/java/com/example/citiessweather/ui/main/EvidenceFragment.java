package com.example.citiessweather.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.citiessweather.Evidence.Evidence;
import com.example.citiessweather.R;
import com.example.citiessweather.databinding.EvidenceBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EvidenceFragment extends Fragment {

    private View view;
    private EvidenceBinding binding;
    private MainViewModel model;
    private String city = "";

    private TextInputEditText mainWeatherText;
    private TextInputEditText minTemText;
    private TextInputEditText maxTemText;
    private TextInputEditText latText;
    private TextInputEditText lonText;
    private TextInputEditText addressText;
    private TextInputEditText warningText;
    private Button button;

    private ImageButton picBtn;
    private ImageView picView;
    private String picTook;
    private String photoPath;
    private Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;

    private ImageButton micBtn;
    private ImageButton playBtn;
    private MediaRecorder mediaRecorder;
    private MediaPlayer player;
    private File recordName;
    private String recorded;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EvidenceBinding.inflate(inflater);
        view = binding.getRoot();
        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

//        ActivityCompat.requestPermissions(requireActivity(), permissions,
//                REQUEST_RECORD_AUDIO_PERMISSION);

        mainWeatherText = binding.evidenceMainWeather;
        minTemText = binding.evidenceMinTemp;
        maxTemText = binding.evidenceMaxTemp;
        latText = binding.evidenceLat;
        lonText = binding.evidenceLon;
        addressText = binding.address;
        warningText = binding.warnigWeather;
        picBtn = binding.picBtn;
        picView = binding.picView;
        playBtn = binding.playBtn;
        micBtn = binding.micBtn;
        button = binding.NotifyEv;

        model.getCurrentUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            model.getCurrentAddress().observe(getViewLifecycleOwner(), address -> {
                addressText.setText(getString(R.string.address_text, address, System.currentTimeMillis()));
            });

            model.getCurrentPosition().observe(getViewLifecycleOwner(), latLng -> {
                latText.setText(String.valueOf(latLng.latitude));
                lonText.setText(String.valueOf(latLng.longitude));
            });

            model.switchTrackingLocation();

            model.getCurrentCity().observe(getViewLifecycleOwner(), city -> {
                mainWeatherText.setText(city.getMain());
                maxTemText.setText(city.getTemp_max());
                minTemText.setText(city.getTemp_min());
                this.city = city.getName();
            });

//            micBtn.setOnClickListener(button -> {
//                if (mediaRecorder == null) {
//                    updateView("rec");
//                    mediaRecorder = new MediaRecorder();
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    newFile();
//                    mediaRecorder.setOutputFile(recordName.getAbsolutePath());
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//                    try {
//                        mediaRecorder.prepare();
//                    } catch (IOException e) {
//                        Log.e("onClick: ","not able to record");
//                    }
//                    mediaRecorder.start();
//                } else {
//                    updateView("stop");
//                    mediaRecorder.stop();
//                    mediaRecorder.reset();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    StorageReference reference = storage.getReference();
//                    StorageReference recRef = reference.child(recordName.getAbsolutePath());
//                    Uri recUri = FileProvider.getUriForFile(requireContext(),
//                            "com.example.android.fileprovider",
//                            recordName);
//                    UploadTask uploadTask = recRef.putFile(recUri);
//                    uploadTask.addOnSuccessListener(taskSnapshot -> {
//                        recRef.getDownloadUrl().addOnCompleteListener(task -> {
//                            Uri downloadUri = task.getResult();
//                            recorded = downloadUri.toString();
//                            Log.e("XXXXX",""+recorded);
//                        });
//                    });
//                }
//            });
//
//            playBtn.setOnClickListener(button -> {
//                if (mediaRecorder == null && player == null) {
//                    player = new MediaPlayer();
//
//                    try {
//                        player.setDataSource(String.valueOf(recordName));
//                        player.prepare();
//                        player.start();
//
//                        player.setOnCompletionListener(mediaPlayer -> {
//                            player.stop();
//                            player.release();
//                            player = null;
//                        });
//
//                    }catch (Exception e){
//                        Log.e("onClick: ", "not able to play media");
//                    }
//                }
//            });

            picBtn.setOnClickListener(button -> {
                dispatchTakePictureIntent();
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference reference = storage.getReference();

                StorageReference imaRef = reference.child(photoPath);
                UploadTask uploadTask = imaRef.putFile(photoURI);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imaRef.getDownloadUrl().addOnCompleteListener(task -> {
                        Uri downloadUri = task.getResult();
                        Glide.with(this).load(downloadUri).into(picView);
                        picTook = downloadUri.toString();
                        Log.e("XXXXX",""+picTook);
                    });
                });
            });

            button.setOnClickListener(view ->{
                Evidence evidence = new Evidence();
                evidence.setLat(latText.getText().toString());
                evidence.setLon(lonText.getText().toString());
                evidence.setAddress(addressText.getText().toString());
                evidence.setWarning(warningText.getText().toString());
                evidence.setMainWeather(mainWeatherText.getText().toString());
                evidence.setMaxTem(maxTemText.getText().toString());
                evidence.setMinTem(minTemText.getText().toString());
                evidence.setPic(picTook);
                evidence.setCity(city);
//                evidence.setRecord();
                Log.e("------------",""+evidence.getPic());

                DatabaseReference data = FirebaseDatabase.getInstance().getReference();
                DatabaseReference users = data.child("users");
                DatabaseReference uid = users.child(firebaseUser.getUid());
                DatabaseReference notify = uid.child("evidences");

                Log.e("XXXXX",notify.toString());

                DatabaseReference notificationReference = notify.push();
                notificationReference.setValue(evidence);

                Toast.makeText(getContext(), "Notified successfully", Toast.LENGTH_SHORT).show();
            });
        });

        return view;
    }

    // picture taking

    private File createImageFile() {

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            photoPath = image.getAbsolutePath();
            Log.e("createImageFile: ", image.getAbsolutePath());
            return image;
        }catch (Exception e){
            return null;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(
                requireContext().getPackageManager()) != null) {
            Log.e( "hola","hola" );
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(requireContext(),
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (Exception ex) {
                Log.e("dispatchTakePicture", ex.getMessage());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(this).load(photoURI).into(picView);
            } else {
                Toast.makeText(getContext(),
                        "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // audio recording

    private void newFile () {
        String path = getContext().getExternalFilesDir(null).getAbsolutePath();
        recordName = new File(path +
                "/"+new SimpleDateFormat("dd/MM/yyyy_HH:mm").format(System.currentTimeMillis())+".3gp");

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateView(String state) {
        switch (state){
            case "rec":
                micBtn.setImageDrawable
                        (getResources().getDrawable(R.drawable.ic_baseline_record_voice_over_24));
                micBtn.setImageTintList(getResources().getColorStateList(R.color.red));
                break;
            case "stop":
                micBtn.setImageDrawable
                        (getResources().getDrawable(R.drawable.ic_baseline_mic_24));
                micBtn.setImageTintList(getResources().getColorStateList(R.color.black));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) {
            Toast.makeText(
                    getContext(),
                    "Permission required",
                    Toast.LENGTH_LONG
            ).show();
            requireActivity().finish();
        }
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
