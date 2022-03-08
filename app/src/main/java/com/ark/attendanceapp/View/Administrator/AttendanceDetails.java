package com.ark.attendanceapp.View.Administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityAttendanceDetailsBinding;

public class AttendanceDetails extends AppCompatActivity {

    private ActivityAttendanceDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);




    }
}