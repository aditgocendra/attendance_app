package com.ark.attendanceapp.View.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityAuthenticationBinding;

public class Authentication extends AppCompatActivity {

    private ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

    }

    private void listenerClick() {
        binding.signUpBtn.setOnClickListener(view -> Utility.updateUI(Authentication.this, SignUp.class));

        binding.signInBtn.setOnClickListener(view -> Utility.updateUI(Authentication.this, SignIn.class));
    }


}