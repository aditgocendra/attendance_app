package com.ark.attendanceapp.View.Employee;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.View.Administrator.Dashboard;
import com.ark.attendanceapp.View.Auth.Authentication;
import com.ark.attendanceapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ActivityResultLauncher<String> imagePick;
    private boolean onImageChange = false;

    private String oldUrlPhoto;
    private String email, username, number_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();
        setDataProfile();
        pickImageSetup();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());
        binding.administratorBtn.setOnClickListener(view -> Utility.updateUI(Profile.this, Dashboard.class));
        binding.signOutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Utility.updateUI(Profile.this, Authentication.class);
        });

        binding.pickPhotoBtn.setOnClickListener(view -> imagePick.launch("image/*"));

        binding.saveBtn.setOnClickListener(view -> {
            email = binding.emailEditTi.getText().toString();
            username = binding.usernameEditTi.getText().toString();
            number_phone = binding.phoneNumberEditTi.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(Profile.this, "Email is null", Toast.LENGTH_SHORT).show();
            }else if (username.isEmpty()){
                Toast.makeText(Profile.this, "Username is null", Toast.LENGTH_SHORT).show();
            }else if (number_phone.isEmpty()){
                Toast.makeText(Profile.this, "Number phone is null", Toast.LENGTH_SHORT).show();
            }else if (!Utility.ValidateEmail(email)){
                Toast.makeText(Profile.this, "Email format wrong", Toast.LENGTH_SHORT).show();
            }else {
                if (onImageChange){
                    saveDataWithImage();
                }else {
                    saveData(oldUrlPhoto);
                }
            }
        });
    }

    private void setDataProfile(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(Utility.uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    // set data
                    binding.emailEditTi.setText(modelUser.getEmail());
                    binding.usernameEditTi.setText(modelUser.getUsername());
                    oldUrlPhoto = modelUser.getUrl_photo();
                    // photo
                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(binding.profileImg);
                    }

                    // number phone
                    if (!modelUser.getNumber_phone().equals("-")){
                        binding.phoneNumberEditTi.setText(modelUser.getNumber_phone());
                    }

                }else {
                    Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void pickImageSetup() {
        imagePick = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    binding.profileImg.setImageURI(result);
                    onImageChange = true;
                }
        );
    }

    private void saveDataWithImage() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("photo_profile/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.profileImg.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);


        if (!oldUrlPhoto.equals("-")){
            FirebaseStorage referenceStorage = FirebaseStorage.getInstance();
            String name_photo = referenceStorage.getReferenceFromUrl(oldUrlPhoto).getName();
            StorageReference deleteRef = referenceStorage.getReference("photo_profile/"+name_photo);
            deleteRef.delete();

        }



        uploadTask.addOnFailureListener(e -> Toast.makeText(Profile.this, "Upload : "+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveData(String.valueOf(uri)))
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Url Download :"+e.getMessage(), Toast.LENGTH_SHORT).show()));

    }

    private void saveData(String url) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        ModelUser modelUser = new ModelUser(
            username,
            email,
            Utility.roleUser,
            url,
            number_phone

        );

        reference.child("users").child(Utility.uid).setValue(modelUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(Profile.this, "Profile change success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        oldUrlPhoto = url;
        onImageChange = false;
    }
}