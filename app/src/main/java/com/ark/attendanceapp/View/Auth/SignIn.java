package com.ark.attendanceapp.View.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.View.Employee.HomeApp;
import com.ark.attendanceapp.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.forgotPassRedirect.setOnClickListener(view -> Utility.updateUI(SignIn.this, ForgotPassword.class));

        binding.signInBtn.setOnClickListener(view -> {
            String email = binding.emailSignIn.getText().toString();
            String pass = binding.passSignIn.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(this, "Email is null", Toast.LENGTH_SHORT).show();
            }else if (pass.isEmpty()){
                Toast.makeText(this, "Password is null", Toast.LENGTH_SHORT).show();
            }else if (!Utility.ValidateEmail(email)){
                Toast.makeText(this, "Format email not valid", Toast.LENGTH_SHORT).show();
            }else {
                signIn(email, pass);
            }
        });
    }

    private void signIn(String email, String pass){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            Utility.updateUI(SignIn.this, HomeApp.class);
            finish();
        }).addOnFailureListener(e -> Toast.makeText(SignIn.this, "Sign In : "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}