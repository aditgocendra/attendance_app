package com.ark.attendanceapp.View.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.ark.attendanceapp.DistanceMath.Euclidean;
import com.ark.attendanceapp.DistanceMath.Haversine;
import com.ark.attendanceapp.Model.ModelOfficeLocation;
import com.ark.attendanceapp.R;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityCompareDistanceBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CompareDistance extends AppCompatActivity {

    private ActivityCompareDistanceBinding binding;

    private LocationRequest locationRequest;
    private List<Address> addressList;

    private double latitudeOffice, longitudeOffice;
    private double latitudeCurrent, longitudeCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompareDistanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();
        setDataLocationOffice();
    }

    private void listenerClick() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        binding.backBtn.setOnClickListener(view -> finish());

        binding.currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(CompareDistance.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (Utility.isGPSEnabled(CompareDistance.this)) {
                            LocationServices.getFusedLocationProviderClient(CompareDistance.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(CompareDistance.this).removeLocationUpdates(this);
                                    if (locationResult.getLocations().size() > 0){
                                        int index = locationResult.getLocations().size() - 1;
                                        latitudeCurrent = locationResult.getLocations().get(index).getLatitude();
                                        longitudeCurrent = locationResult.getLocations().get(index).getLongitude();

                                        Geocoder geocoder = new Geocoder(CompareDistance.this, Locale.getDefault());
                                        try {
                                            addressList = geocoder.getFromLocation(latitudeCurrent, longitudeCurrent,1);
                                            binding.yourLocationEdt.setText(addressList.get(0).getAddressLine(0));
                                            binding.latitudeYourLocationTv.setText("Latitude : "+latitudeCurrent);
                                            binding.longitudeYourLocationTv.setText("Longitude : "+longitudeCurrent);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        binding.progressCircular.setVisibility(View.GONE);
                                        binding.currentLocation.setEnabled(true);
                                    }else {
                                        Toast.makeText(CompareDistance.this, "Find location failed", Toast.LENGTH_SHORT).show();
                                        binding.progressCircular.setVisibility(View.GONE);
                                        binding.currentLocation.setEnabled(true);
                                    }
                                }
                            }, Looper.getMainLooper());

                        } else {
                            Utility.turnOnGPS(locationRequest, CompareDistance.this);
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.currentLocation.setEnabled(true);
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.currentLocation.setEnabled(true);
                    }
                }
            }
        });

//        binding.setLocation.setOnClickListener(view -> {
//            String location = binding.yourLocationEdt.getText().toString();
//
//            if (location.isEmpty()){
//                Toast.makeText(CompareDistance.this, "Location edit text is null", Toast.LENGTH_SHORT).show();
//            }else {
//                LatLng officeLatLang = Utility.getLocationFromAddress(CompareDistance.this, location);
//
//                if (officeLatLang != null){
//                    latitudeCurrent = officeLatLang.latitude;
//                    longitudeCurrent = officeLatLang.longitude;
//                    binding.latitudeYourLocationTv.setText("Latitude : "+latitudeCurrent);
//                    binding.longitudeYourLocationTv.setText("Longitude : "+longitudeCurrent);
//
//                }else {
//                    Toast.makeText(CompareDistance.this, "Location not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        binding.previewLocationMap.setOnClickListener(view -> {
            if (latitudeCurrent != 0 && longitudeCurrent != 0){
                Utility.previewStreetMaps(latitudeCurrent, longitudeCurrent, CompareDistance.this);
            }else {
                Toast.makeText(CompareDistance.this, "Nothing location set", Toast.LENGTH_SHORT).show();
            }
        });

        binding.compareBtn.setOnClickListener(view -> {
            if (latitudeCurrent != 0 && longitudeCurrent != 0){
                float euclideanResult = (float) Euclidean.euclidean(latitudeCurrent, longitudeCurrent, latitudeOffice, longitudeOffice);
                float haversineResult = (float) Haversine.haversine(latitudeCurrent, longitudeCurrent, latitudeOffice, longitudeOffice);

                binding.haversineResult.setText(String.valueOf(haversineResult));
                binding.euclideanResult.setText(String.valueOf(euclideanResult));

                Log.d("Distance Result", euclideanResult+" / "+haversineResult);
                // km value
                float oneDegreesEarth = (float) 111.322;
                float haversineKm =  haversineResult;
                float euclideanKm = euclideanResult * oneDegreesEarth;

                Log.d("Distance Km Result", euclideanKm+" / "+haversineKm);

                // meters value
                float haversineMeters = haversineKm * 1000;
                float euclideanMeters = euclideanKm * 1000;

                Log.d("Distance Meters Result", euclideanMeters+" / "+haversineMeters);

                // set value to meters
                binding.haversineConvertMeters.setText(String.valueOf(haversineMeters));
                binding.euclideanConvertMeters.setText(String.valueOf(euclideanMeters));

            }else {
                Toast.makeText(CompareDistance.this, "Nothing location set", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataLocationOffice(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("office_location").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelOfficeLocation modelOfficeLocation = task.getResult().getValue(ModelOfficeLocation.class);
                if (modelOfficeLocation != null){
                    binding.addressOfficeTv.setText(modelOfficeLocation.getAddress());
                    binding.latitudeOfficeTv.setText("Latitude : "+modelOfficeLocation.getLatitude());
                    binding.longitudeOfficeTv.setText("Longitude : "+modelOfficeLocation.getLongitude());
                    latitudeOffice = modelOfficeLocation.getLatitude();
                    longitudeOffice = modelOfficeLocation.getLongitude();
                }else {
                    //Create the Dialog here
                    Dialog dialog = new Dialog(CompareDistance.this);
                    dialog.setContentView(R.layout.custom_information_dialog);
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_custom_dialog));

                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    dialog.setCancelable(false); //Optional
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

                    Button Okay = dialog.findViewById(R.id.btn_okay);

                    dialog.show();
                    Okay.setOnClickListener(v -> {
                        finish();
                        dialog.dismiss();
                    });
                }
            }else {
                Toast.makeText(CompareDistance.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}