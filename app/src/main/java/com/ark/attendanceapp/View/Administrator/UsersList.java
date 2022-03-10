package com.ark.attendanceapp.View.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ark.attendanceapp.Adapter.AdapterUsersList;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityUsersListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {

    private ActivityUsersListBinding binding;
    private List<ModelUser> listUsers;
    private AdapterUsersList adapterUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recycleUserList.setLayoutManager(gridLayoutManager);
        binding.recycleUserList.setHasFixedSize(true);

        setDataUsers();

    }

    private void setDataUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                    modelUser.setKey(dataSnapshot.getKey());
                    listUsers.add(modelUser);
                }

                adapterUsersList = new AdapterUsersList(listUsers, UsersList.this);
                binding.recycleUserList.setAdapter(adapterUsersList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersList.this, "Database : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());
    }
}