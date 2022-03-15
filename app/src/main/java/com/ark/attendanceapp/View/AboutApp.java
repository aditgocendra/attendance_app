package com.ark.attendanceapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityAboutAppBinding;
import com.ark.attendanceapp.databinding.ActivityForgotPasswordBinding;

public class AboutApp extends AppCompatActivity {

    private ActivityAboutAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        binding.backBtn.setOnClickListener(view -> finish());
    }
}