package com.ark.attendanceapp.View.Administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

    }
}