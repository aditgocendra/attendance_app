package com.ark.attendanceapp.View.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.ark.attendanceapp.Model.ModelPresenceTime;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityPresenceTimeSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class PresenceTimeSettings extends AppCompatActivity {

    private ActivityPresenceTimeSettingsBinding binding;
    private int hourIn, minuteIn, hourOut, minuteOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresenceTimeSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();
        setDataPresenceTime();
    }

    private void listenerClick(){

        binding.backBtn.setOnClickListener(v -> finish());
        // pick time in
        binding.cardPickTimeIn.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog;
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(PresenceTimeSettings.this, (timePicker, hourDay, minuteOfDay) -> {

                hourIn = hourDay;
                minuteIn = minuteOfDay;

                calendar.set(0,0,0, hourIn, minuteIn);
                binding.timeInResult.setText(DateFormat.format("HH:mm", calendar));
            }, 24, 0, true);

            timePickerDialog.show();
        });

        // pick time out
        binding.cardPickTimeOut.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog;
            Calendar calendar = Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(PresenceTimeSettings.this, (timePicker, hourDay, minuteOfDay) -> {

                hourOut = hourDay;
                minuteOut = minuteOfDay;

                calendar.set(0,0,0, hourOut, minuteOut);
                binding.timeOutResult.setText(DateFormat.format("HH:mm", calendar));
            }, 24, 0, true);

            timePickerDialog.show();
        });

        binding.savePresenceTime.setOnClickListener(v -> saveDatePresenceTime());
    }

    private void setDataPresenceTime(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("presence_time").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                ModelPresenceTime modelPresenceTime = task.getResult().getValue(ModelPresenceTime.class);
                binding.timeInResult.setText(modelPresenceTime.getTime_in());
                binding.timeOutResult.setText(modelPresenceTime.getTime_out());
            }
        });
    }

    private void saveDatePresenceTime(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ModelPresenceTime modelPresenceTime = new ModelPresenceTime(binding.timeInResult.getText().toString(), binding.timeOutResult.getText().toString());
        ref.child("presence_time").setValue(modelPresenceTime).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(PresenceTimeSettings.this, "Success update presence time", Toast.LENGTH_SHORT).show();
            }
        });
    }
}