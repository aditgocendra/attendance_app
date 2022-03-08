package com.ark.attendanceapp.View.Employee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

    }
}