package com.ark.attendanceapp.View.Administrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

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

        listenerClick();
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.cardListUser.setOnClickListener(view -> Utility.updateUI(Dashboard.this, UsersList.class));
        binding.cardLocationOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.updateUI(Dashboard.this, LocationSettingOffice.class);
            }
        });
    }
}