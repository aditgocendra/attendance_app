package com.ark.attendanceapp.View.Administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityLocationSettingOfficeBinding;

public class LocationSettingOffice extends AppCompatActivity {
    
    private ActivityLocationSettingOfficeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationSettingOfficeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
        
        listenerClick();
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

    }
}