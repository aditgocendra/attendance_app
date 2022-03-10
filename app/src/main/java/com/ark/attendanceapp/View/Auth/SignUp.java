package com.ark.attendanceapp.View.Auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.signUpBtn.setOnClickListener(view -> {
            // get data from form register
            String username = binding.usernameSignUp.getText().toString();
            String email = binding.emailSignUp.getText().toString();
            String pass = binding.passSignUp.getText().toString();
            String re_pass = binding.rePassSignUp.getText().toString();

            // validate form
            if (username.isEmpty()){
                Toast.makeText(SignUp.this, "Form username is null", Toast.LENGTH_SHORT).show();
            }else if (email.isEmpty()){
                Toast.makeText(SignUp.this, "Form email is null", Toast.LENGTH_SHORT).show();
            }else if (pass.isEmpty()){
                Toast.makeText(SignUp.this, "Form password is null", Toast.LENGTH_SHORT).show();
            }else if (re_pass.isEmpty()){
                Toast.makeText(SignUp.this, "Form Confirm password is null", Toast.LENGTH_SHORT).show();
            }
            // validate email, pass and re pass
            else if (!Utility.ValidateEmail(email)){
                Toast.makeText(SignUp.this, "Wrong email format", Toast.LENGTH_SHORT).show();
            }else if (!pass.equals(re_pass)){
                Toast.makeText(SignUp.this, "Password and confirm password not same", Toast.LENGTH_SHORT).show();
            }else {
                // sign up after validate success
                binding.signUpBtn.setEnabled(false);
                binding.progressCircular.setVisibility(View.VISIBLE);
                signUp(email, pass);
            }

        });
    }

    private void signUp(String email, String pass){
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this::saveDataUser);
    }

    private void saveDataUser(Task<AuthResult> task){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (task.isSuccessful()){
            FirebaseUser user = auth.getCurrentUser();
            String username = binding.usernameSignUp.getText().toString();

            assert user != null;
            ModelUser modelUser = new ModelUser(
                    username,
                    user.getEmail(),
                    "employee",
                    "-",
                    "-");

            reference.child("users").child(user.getUid()).setValue(modelUser).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()){
                    binding.signUpBtn.setEnabled(true);
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    Utility.updateUI(SignUp.this, SignIn.class);
                    finish();
                }else {
                    binding.signUpBtn.setEnabled(true);
                    binding.progressCircular.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this, "Database Failed : "+ Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            binding.signUpBtn.setEnabled(true);
            binding.progressCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"Auth Failed :"+Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}