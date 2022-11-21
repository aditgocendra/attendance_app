package com.ark.attendanceapp.View.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.ark.attendanceapp.Model.ModelOfficeLocation;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityLocationSettingOfficeBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationSettingOffice extends AppCompatActivity {
    
    private ActivityLocationSettingOfficeBinding binding;
    private LocationRequest locationRequest;
    private List<Address> addressList;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private double latitude = 0, longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationSettingOfficeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        listenerClick();
        setData();
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.previewLocationMap.setOnClickListener(view -> {
            String location = binding.locationOfficeEdt.getText().toString();
            Log.d("LatLong", String.valueOf(latitude + " "+ longitude));
            if (location.isEmpty()){
                Toast.makeText(LocationSettingOffice.this, "You haven't determined the location", Toast.LENGTH_SHORT).show();
            }else {
                if (latitude == 0 && longitude == 0){
                    LatLng officeLatLang = Utility.getLocationFromAddress(LocationSettingOffice.this, location);
                    if (officeLatLang != null){
                        latitude = officeLatLang.latitude;
                        longitude = officeLatLang.longitude;
                        Utility.previewStreetMaps(latitude, longitude, LocationSettingOffice.this);
                    }else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Utility.previewStreetMaps(latitude, longitude, LocationSettingOffice.this);
                }
            }
        });
        
        binding.saveLocOffice.setOnClickListener(view -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            String location = binding.locationOfficeEdt.getText().toString();
            if (location.isEmpty()){
                Toast.makeText(LocationSettingOffice.this, "You haven't determined the location", Toast.LENGTH_SHORT).show();
            }else {
                if (latitude == 0 && longitude == 0){
                    LatLng officeLatLang = Utility.getLocationFromAddress(LocationSettingOffice.this, location);
                    if (officeLatLang != null){
                        latitude = officeLatLang.latitude;
                        longitude = officeLatLang.longitude;
                        saveOfficeLocation(location);
                    }else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        binding.progressCircular.setVisibility(View.GONE);
                    }
                }else {
                    saveOfficeLocation(location);
                }
            }
        });

        binding.currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.currentLocationBtn.setEnabled(false);



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(LocationSettingOffice.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (Utility.isGPSEnabled(LocationSettingOffice.this)) {
                            LocationServices.getFusedLocationProviderClient(LocationSettingOffice.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(LocationSettingOffice.this).removeLocationUpdates(this);

                                    if (locationResult.getLocations().size() > 0){

                                        int index = locationResult.getLocations().size() - 1;

                                        // FOR API < 18
                                        if (Utility.isMockLocationEnabled(LocationSettingOffice.this)){
                                            Toast.makeText(LocationSettingOffice.this, "Deactivated your fake GPS", Toast.LENGTH_SHORT).show();
                                            binding.progressCircular.setVisibility(View.GONE);
                                            binding.currentLocationBtn.setEnabled(true);
                                            return;
                                        }
                                        // FOR API > 18
                                        if (Utility.isMockLocation(locationResult.getLocations().get(index))){
                                            Toast.makeText(LocationSettingOffice.this, "Deactivated your fake GPS", Toast.LENGTH_SHORT).show();
                                            binding.progressCircular.setVisibility(View.GONE);
                                            binding.currentLocationBtn.setEnabled(true);
                                            return;
                                        }

                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();

                                        Geocoder geocoder = new Geocoder(LocationSettingOffice.this, Locale.getDefault());
                                        try {
                                            addressList = geocoder.getFromLocation(latitude, longitude,1);
                                            binding.locationOfficeEdt.setText(addressList.get(0).getAddressLine(0));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        Toast.makeText(LocationSettingOffice.this, "Find location failed", Toast.LENGTH_SHORT).show();
                                    }

                                    binding.progressCircular.setVisibility(View.GONE);
                                    binding.currentLocationBtn.setEnabled(true);
                                }
                            }, Looper.getMainLooper());

                        } else {
                            Utility.turnOnGPS(locationRequest, LocationSettingOffice.this);
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.currentLocationBtn.setEnabled(true);
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.currentLocationBtn.setEnabled(true);
                    }
                }
            }
        });

    }

    private void saveOfficeLocation(String address){

        ModelOfficeLocation modelOfficeLocation = new ModelOfficeLocation(
                address,
                latitude,
                longitude
        );
        
        reference.child("office_location").setValue(modelOfficeLocation)
                .addOnSuccessListener(unused -> 
                        Toast.makeText(LocationSettingOffice.this, "Location office change", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> 
                        Toast.makeText(LocationSettingOffice.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        binding.progressCircular.setVisibility(View.GONE);
    }

    private void setData(){
       reference.child("office_location").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelOfficeLocation modelOfficeLocation = task.getResult().getValue(ModelOfficeLocation.class);
                if (modelOfficeLocation != null){
                    binding.locationOfficeEdt.setText(modelOfficeLocation.getAddress());
                    latitude = modelOfficeLocation.getLatitude();
                    longitude = modelOfficeLocation.getLongitude();
                }else {
                    Toast.makeText(this, "Nothing location set", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
       });
    }
}