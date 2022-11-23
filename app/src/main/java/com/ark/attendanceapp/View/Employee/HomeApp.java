package com.ark.attendanceapp.View.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.ark.attendanceapp.DistanceMath.Euclidean;
import com.ark.attendanceapp.DistanceMath.Haversine;
import com.ark.attendanceapp.Model.ModelAttendanceDistance;
import com.ark.attendanceapp.Model.ModelAttendanceUsers;
import com.ark.attendanceapp.Model.ModelOfficeLocation;
import com.ark.attendanceapp.Model.ModelPresenceTime;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.NetworkChangeListener;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.View.Administrator.CompareDistance;
import com.ark.attendanceapp.View.Administrator.LocationSettingOffice;
import com.ark.attendanceapp.databinding.ActivityHomeAppBinding;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private LocationRequest locationRequest;
    private double latitudeOffice, longitudeOffice;

    // distance attendance attr
    private float max_distance_attendance = 0;
    private String math = null;

    private ModelAttendanceUsers attendanceUser;
    private ModelPresenceTime presenceTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        binding.accountImg.setEnabled(false);
        setUserData();


        setPresenceTime();
        setDistanceAttendance();

        setLocationOffice();
        setDateTime();

        Handler handler = new Handler();
        handler.postDelayed(() -> listenerClick(), 2000);
    }

    @Override
    protected void onStart() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeListener, filter);
        }, 1000);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void listenerClick() {
        binding.accountImg.setOnClickListener(view -> Utility.updateUI(HomeApp.this, Profile.class));

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = sdf.format(new Date());

        String hour = time.substring(0, 2);
        String minute = time.substring(3, 5);

        int timeIn = Integer.parseInt(presenceTime.getTime_in().substring(0,2));
        int minuteIn= Integer.parseInt(presenceTime.getTime_in().substring(3,5));
        int timeOut= Integer.parseInt(presenceTime.getTime_out().substring(0,2));
        int minuteOut = Integer.parseInt(presenceTime.getTime_out().substring(3,5));

        if (minuteIn + 15 > 60){
            minuteIn = 60;
        }else{
            minuteIn += 15;
        }

        if (minuteOut + 15 > 60){
            minuteOut = 60;
        }else{
            minuteOut += 15;
        }
        boolean timeInCondition = Integer.parseInt(hour) == timeIn && Integer.parseInt(minute) <= minuteIn;
        boolean timeOutCondition = Integer.parseInt(hour) == timeOut && Integer.parseInt(minute) <= minuteOut;

        binding.attendanceBtnIn.setOnClickListener(view -> {
            if (!timeInCondition){
                Toast.makeText(this, "Not presence time", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.attendanceBtnIn.setEnabled(false);
            if (binding.attendanceSuccessIn.getVisibility() == View.VISIBLE){
                binding.attendanceBtnIn.setEnabled(true);
                Toast.makeText(this, "You have done attendance today, please come back tomorrow :)", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please wait until the attendance process is complete", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }

        });

        binding.attendanceBtnOut.setOnClickListener(view -> {
            if (!timeOutCondition){
                Toast.makeText(this, "Not presence time", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.attendanceBtnOut.setEnabled(false);
            if (binding.attendanceSuccessOut.getVisibility() == View.VISIBLE){
                binding.attendanceBtnOut.setEnabled(true);
                Toast.makeText(this, "You have done attendance today, please come back tomorrow :)", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please wait until the attendance process is complete", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }

        });
    }

    private void getCurrentLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomeApp.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (Utility.isGPSEnabled(HomeApp.this)) {
                    LocationServices.getFusedLocationProviderClient(HomeApp.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(HomeApp.this).removeLocationUpdates(this);
                            if (locationResult.getLocations().size() > 0){
                                int index = locationResult.getLocations().size() - 1;
                                // FOR API < 18
                                if (Utility.isMockLocationEnabled(HomeApp.this)){
                                    Toast.makeText(HomeApp.this, "Deactivated your fake GPS", Toast.LENGTH_SHORT).show();

                                    return;
                                }
                                // FOR API > 18
                                if (Utility.isMockLocation(locationResult.getLocations().get(index))){
                                    Toast.makeText(HomeApp.this, "Deactivated your fake GPS", Toast.LENGTH_SHORT).show();

                                    return;
                                }
                                double latitudeCurrent = locationResult.getLocations().get(index).getLatitude();
                                double longitudeCurrent = locationResult.getLocations().get(index).getLongitude();

                                attendanceEmployee(latitudeCurrent, longitudeCurrent);



                            }else {
                                Toast.makeText(HomeApp.this, "Find location failed", Toast.LENGTH_SHORT).show();
                                binding.attendanceBtnIn.setEnabled(true);
                                binding.attendanceBtnOut.setEnabled(true);
                            }
                        }
                    }, Looper.getMainLooper());

                } else {
                    Utility.turnOnGPS(locationRequest, HomeApp.this);
                    binding.attendanceBtnIn.setEnabled(true);
                    binding.attendanceBtnOut.setEnabled(true);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1005);
                binding.attendanceBtnIn.setEnabled(true);
                binding.attendanceBtnOut.setEnabled(true);
            }
        }
    }

    private void attendanceEmployee(double latitudeCurrent, double longitudeCurrent) {
        float distanceResult;
        
        if (math == null || max_distance_attendance == 0){
            Toast.makeText(this, "Error, rangefinder, please come back later", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (math.equals("Manhattan")){
            distanceResult = (float) Haversine.haversine(latitudeCurrent, longitudeCurrent, latitudeOffice, longitudeOffice);
        }else {
            distanceResult = (float) Euclidean.euclidean(latitudeCurrent, longitudeCurrent, latitudeOffice, longitudeOffice);
        }

        Log.d("Distance Result", String.valueOf(distanceResult));

        // km value
        float oneDegreesEarth = (float) 111.322;
        float distanceKm =  distanceResult * oneDegreesEarth;
        Log.d("Distance Km Result", String.valueOf(distanceKm));

        // meters value
        float distanceMeters = distanceKm * 1000;
        Log.d("Distance Meters Result", String.valueOf(distanceMeters));

        if (distanceMeters <= max_distance_attendance){
            saveDataAttendance(latitudeCurrent, longitudeCurrent, distanceMeters);
        }else {
            Toast.makeText(this, "Your distance is too far from the office", Toast.LENGTH_LONG).show();
        }
    }

    private void saveDataAttendance(double latitudeCurrent, double longitudeCurrent, float distanceMeters) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String day = month_name.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);
        Log.d("test", day);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = sdf.format(new Date());

        ModelAttendanceUsers modelAttendanceUsers;

        String hour = time.substring(0, 2);
        String minute = time.substring(3, 5);

        int timeIn = Integer.parseInt(presenceTime.getTime_in().substring(0,2));
        int minuteIn= Integer.parseInt(presenceTime.getTime_in().substring(3,5));
        int timeOut= Integer.parseInt(presenceTime.getTime_out().substring(0,2));
        int minuteOut = Integer.parseInt(presenceTime.getTime_out().substring(3,5));

        if (minuteIn + 15 > 60){
            minuteIn = 60;
        }else{
            minuteIn += 15;
        }

        if (minuteOut + 15 > 60){
            minuteOut = 60;
        }else{
            minuteOut += 15;
        }
        boolean timeInCondition = Integer.parseInt(hour) == timeIn && Integer.parseInt(minute) <= minuteIn;
        boolean timeOutCondition = Integer.parseInt(hour) == timeOut && Integer.parseInt(minute) <= minuteOut;

        if (attendanceUser == null) {
            // User Not Absence
            if (timeInCondition) {
                // In
                modelAttendanceUsers = new ModelAttendanceUsers(
                        latitudeCurrent,
                        longitudeCurrent,
                        0,
                        0,
                        distanceMeters+" Meters",
                        "-",
                        day,
                        time,
                        "-",
                        Utility.uid
                );
            }else{
                // Out
                if (timeOutCondition){
                    modelAttendanceUsers = new ModelAttendanceUsers(
                            0,
                            0,
                            latitudeCurrent,
                            longitudeCurrent,
                            "-",
                            distanceMeters+" Meters",
                            day,
                            "-",
                            time,
                            Utility.uid
                    );

                }else{
                    Toast.makeText(this, "Not presence time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else{

            if (timeInCondition) {
                // In
                attendanceUser.setLatitudeIn(latitudeCurrent);
                attendanceUser.setLongitudeIn(longitudeCurrent);
                attendanceUser.setDistanceIn(distanceMeters+" Meters");
                attendanceUser.setTimeIn(time);
            }else{
                // Out
                if (timeOutCondition){
                    attendanceUser.setLatitudeOut(latitudeCurrent);
                    attendanceUser.setLongitudeOut(longitudeCurrent);
                    attendanceUser.setDistanceOut(distanceMeters+" Meters");
                    attendanceUser.setTimeOut(time);

                }else{
                    Toast.makeText(this, "Not presence time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            modelAttendanceUsers = attendanceUser;
        }

        reference.child("attendance_users").child(day).child(Utility.uid).setValue(modelAttendanceUsers).addOnSuccessListener(unused -> {
            Toast.makeText(HomeApp.this, "Attendance Success", Toast.LENGTH_SHORT).show();
            if (!binding.attendanceBtnIn.isEnabled()){
                binding.attendanceBtnIn.setEnabled(true);
                binding.attendanceSuccessIn.setVisibility(View.VISIBLE);
                binding.attendanceTextIn.setVisibility(View.GONE);
            }

            if (!binding.attendanceBtnOut.isEnabled()){
                binding.attendanceBtnOut.setEnabled(true);
                binding.attendanceSuccessOut.setVisibility(View.VISIBLE);
                binding.attendanceTextOut.setVisibility(View.GONE);
            }

            binding.textDirection.setText("Thank you for taking attendance today");

        }).addOnFailureListener(e -> Toast.makeText(HomeApp.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    Utility.uid = task.getResult().getKey();
                    Utility.roleUser = modelUser.getRole();

                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(binding.accountImg);
                    }
                    binding.accountImg.setEnabled(true);
                    checkUserAttendance();
                }else {
                    Toast.makeText(HomeApp.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(HomeApp.this, "Database Realtime : "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserAttendance() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String day = month_name.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = sdf.format(new Date());
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));

        reference.child("attendance_users").child(day).child(Utility.uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                 attendanceUser = task.getResult().getValue(ModelAttendanceUsers.class);

                 int timeIn = Integer.parseInt(presenceTime.getTime_in().substring(0,2));
                 int minuteIn= Integer.parseInt(presenceTime.getTime_in().substring(3,5));
                 int timeOut= Integer.parseInt(presenceTime.getTime_out().substring(0,2));
                 int minuteOut = Integer.parseInt(presenceTime.getTime_out().substring(3,5));

                 if (minuteIn + 15 > 60){
                     minuteIn = 60;
                 }else{
                     minuteIn += 15;
                 }

                if (minuteOut + 15 > 60){
                    minuteOut = 60;
                }else{
                    minuteOut += 15;
                }

                 if (attendanceUser != null) {
                        // In
                        if (attendanceUser.getTimeIn().equals("-") && attendanceUser.getTimeOut().equals("_")){
                             binding.attendanceSuccessIn.setVisibility(View.VISIBLE);
                             binding.attendanceSuccessOut.setVisibility(View.VISIBLE);
                             binding.attendanceTextIn.setVisibility(View.GONE);
                             binding.attendanceTextOut.setVisibility(View.GONE);
                             binding.textDirection.setText("Thank you for taking attendance today");
                             binding.attendanceBtnIn.setVisibility(View.VISIBLE);
                             binding.attendanceBtnOut.setVisibility(View.VISIBLE);
                        }

                        if (attendanceUser.getTimeIn().equals("-")){
                            binding.attendanceSuccessIn.setVisibility(View.GONE);
                            binding.attendanceTextIn.setVisibility(View.VISIBLE);
                            binding.attendanceBtnIn.setVisibility(View.VISIBLE);
                            binding.textDirection.setText("Welcome, please attendance in for today, minimum distance " + (double) max_distance_attendance + " Meters");
                        }else{

                                binding.attendanceSuccessIn.setVisibility(View.VISIBLE);
                                binding.attendanceTextIn.setVisibility(View.GONE);
                                binding.attendanceBtnIn.setVisibility(View.VISIBLE);

                        }
                        // Out
                         if (attendanceUser.getTimeOut().equals("-")){
                             binding.attendanceSuccessOut.setVisibility(View.GONE);
                             binding.attendanceTextOut.setVisibility(View.VISIBLE);
                             binding.attendanceBtnOut.setVisibility(View.VISIBLE);
                             binding.textDirection.setText("Welcome, please attendance out for today, minimum distance " + (double) max_distance_attendance + " Meters");
                         }else{

                                 binding.attendanceSuccessOut.setVisibility(View.VISIBLE);
                                 binding.attendanceTextOut.setVisibility(View.GONE);
                                 binding.attendanceBtnOut.setVisibility(View.VISIBLE);

                         }

                 }else{
                     binding.textDirection.setText("Welcome, please attendance for today, minimum distance " + (double) max_distance_attendance + " Meters");
                     binding.attendanceSuccessIn.setVisibility(View.GONE);
                     binding.attendanceTextIn.setVisibility(View.VISIBLE);
                     binding.attendanceBtnIn.setVisibility(View.VISIBLE);

                     binding.attendanceSuccessOut.setVisibility(View.GONE);
                     binding.attendanceTextOut.setVisibility(View.VISIBLE);
                     binding.attendanceBtnOut.setVisibility(View.VISIBLE);
                 }
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDistanceAttendance() {
        reference.child("distance_attendance").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelAttendanceDistance modelAttendanceDistance = task.getResult().getValue(ModelAttendanceDistance.class);
                if (modelAttendanceDistance != null){
                    math = modelAttendanceDistance.getMath_distance();
                    max_distance_attendance = Float.parseFloat(modelAttendanceDistance.getMax_distance());
                }else {
                    Toast.makeText(HomeApp.this, "Distance Attendance not found", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDateTime() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        binding.dateText.setText(String.valueOf(cal.get(Calendar.DATE)));
        String monthYear = month_name+" "+cal.get(Calendar.YEAR);
        binding.monthAndYear.setText(monthYear);

        Log.d("date", String.valueOf(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)));
    }

    private void setLocationOffice(){
        reference.child("office_location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelOfficeLocation modelOfficeLocation = snapshot.getValue(ModelOfficeLocation.class);
                if (modelOfficeLocation != null){
                    latitudeOffice = modelOfficeLocation.getLatitude();
                    longitudeOffice = modelOfficeLocation.getLongitude();

                }else {
                    Toast.makeText(HomeApp.this, "Office location not set", Toast.LENGTH_SHORT).show();
                    binding.attendanceBtnIn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        reference.child("office_location").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                ModelOfficeLocation modelOfficeLocation = task.getResult().getValue(ModelOfficeLocation.class);
//                if (modelOfficeLocation != null){
//                    latitudeOffice = modelOfficeLocation.getLatitude();
//                    longitudeOffice = modelOfficeLocation.getLongitude();
//                }else {
//                    Toast.makeText(HomeApp.this, "Office location not set", Toast.LENGTH_SHORT).show();
//                    binding.attendanceBtnIn.setEnabled(false);
//                }
//            }else {
//                Toast.makeText(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setPresenceTime(){
        reference.child("presence_time").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    presenceTime = task.getResult().getValue(ModelPresenceTime.class);
                }
            }
        });
    }
}