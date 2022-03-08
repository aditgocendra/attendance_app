package com.ark.attendanceapp.View.Employee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityHomeAppBinding;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
    }
}