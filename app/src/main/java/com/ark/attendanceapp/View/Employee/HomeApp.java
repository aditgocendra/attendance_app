package com.ark.attendanceapp.View.Employee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityHomeAppBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();
        setUserData();
        setDateTime();
    }

    private void listenerClick() {
        binding.accountImg.setOnClickListener(view -> Utility.updateUI(HomeApp.this, Profile.class));
    }

    private void setUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    Utility.uid = task.getResult().getKey();
                    Utility.roleUser = modelUser.getRole();

                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(binding.accountImg);
                    }
                }else {
                    Toast.makeText(HomeApp.this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(HomeApp.this, "Database Realtime : "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());

        binding.dateText.setText(String.valueOf(cal.get(Calendar.DATE)));
        String monthYear = month_name+" "+cal.get(Calendar.YEAR);
        binding.monthAndYear.setText(monthYear);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        binding.timeText.setText(currentTime);

        Log.d("date", String.valueOf(cal.getTime()));
    }
}