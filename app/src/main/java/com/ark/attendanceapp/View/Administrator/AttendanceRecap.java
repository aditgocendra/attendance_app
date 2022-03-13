package com.ark.attendanceapp.View.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ark.attendanceapp.Adapter.AdapterAttendanceRecap;
import com.ark.attendanceapp.Model.ModelAttendanceUsers;
import com.ark.attendanceapp.R;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityAttendanceRecapBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceRecap extends AppCompatActivity {

    private ActivityAttendanceRecapBinding binding;
    private AdapterAttendanceRecap adapterAttendanceRecap;
    private List<ModelAttendanceUsers> listAttendanceUsers;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private TextView textDate;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceRecapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recycleUserAttendance.setLayoutManager(gridLayoutManager);
        binding.recycleUserAttendance.setHasFixedSize(true);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        String month_name = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String day = month_name.substring(0, 3)+" "+cal.get(Calendar.DATE)+", "+cal.get(Calendar.YEAR);
        textDate.setText(day);

        setAttendanceData(day);

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        bottomSheetDialog = new BottomSheetDialog(AttendanceRecap.this);
        setBottomDialogFilter();
        binding.filterBtn.setOnClickListener(view -> bottomSheetDialog.show());
    }

    private void setAttendanceData(String day) {
        reference.child("attendance_users").child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAttendanceUsers = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelAttendanceUsers modelAttendanceUsers = dataSnapshot.getValue(ModelAttendanceUsers.class);
                    if (modelAttendanceUsers != null){
                        modelAttendanceUsers.setKey(dataSnapshot.getKey());
                        listAttendanceUsers.add(modelAttendanceUsers);
                    }else {
                        Toast.makeText(AttendanceRecap.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                if (listAttendanceUsers.size() < 1){
                    binding.textNothing.setVisibility(View.VISIBLE);
                }else {
                    binding.textNothing.setVisibility(View.GONE);
                }
                adapterAttendanceRecap = new AdapterAttendanceRecap(listAttendanceUsers, AttendanceRecap.this);
                binding.recycleUserAttendance.setAdapter(adapterAttendanceRecap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendanceRecap.this, "Database"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottomDialogFilter(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_filter_attendance, null, false);
        textDate = viewBottomDialog.findViewById(R.id.text_date_tv);

        CardView cardPickDate = viewBottomDialog.findViewById(R.id.card_pick_date);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        Locale.setDefault(Locale.ENGLISH);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date Attendance");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker<Long> materialDatePicker = builder.build();


        cardPickDate.setOnClickListener(view -> materialDatePicker.show(getSupportFragmentManager(), "DATE PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> textDate.setText(materialDatePicker.getHeaderText()));

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filterDay = textDate.getText().toString();
                setAttendanceData(filterDay);
                bottomSheetDialog.hide();
            }
        });

        bottomSheetDialog.setContentView(viewBottomDialog);

    }


}