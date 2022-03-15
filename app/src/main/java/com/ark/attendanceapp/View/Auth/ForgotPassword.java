package com.ark.attendanceapp.View.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
        listenerClick();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.haveAccountBtn.setOnClickListener(view -> finish());
        
        binding.forgotPassBtn.setOnClickListener(view -> {
            String email = binding.emailForgotPass.getText().toString();
            if (email.isEmpty()){
                Toast.makeText(ForgotPassword.this, "Email is null", Toast.LENGTH_SHORT).show();
            }else {
                forgotPass(email);
            }
        });
    }
    
    private void forgotPass(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(ForgotPassword.this, "We have sent help to your email, please open your email", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ForgotPassword.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}