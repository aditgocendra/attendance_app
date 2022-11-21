package com.ark.attendanceapp.View.Administrator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.ark.attendanceapp.Model.ModelAttendanceDistance;
import com.ark.attendanceapp.R;
import com.ark.attendanceapp.Utility;
import com.ark.attendanceapp.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private BottomSheetDialog bottomSheetDialog;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private AutoCompleteTextView autoCompleteMathDistance;

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
        binding.cardLocationOffice.setOnClickListener(view -> Utility.updateUI(Dashboard.this, LocationSettingOffice.class));

        binding.cardDistanceSetting.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(Dashboard.this);
            setBottomDialog();
            bottomSheetDialog.show();
        });

        binding.cardCompareDistance.setOnClickListener(view -> Utility.updateUI(Dashboard.this, CompareDistance.class));

        binding.cardAttendanceRecap.setOnClickListener(view -> Utility.updateUI(Dashboard.this, AttendanceRecap.class));

        binding.cardTimePresenceSetting.setOnClickListener(v -> Utility.updateUI(Dashboard.this, PresenceTimeSettings.class));

    }

    private void setBottomDialog(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_distance_manage, null, false);
        TextInputEditText minDistanceTi = viewBottomDialog.findViewById(R.id.min_ti_distance);
        TextInputEditText maxDistanceTi = viewBottomDialog.findViewById(R.id.max_ti_distance);
        autoCompleteMathDistance = viewBottomDialog.findViewById(R.id.auto_complete_choice_math_distance);
        Button finishBtn = viewBottomDialog.findViewById(R.id.finish_btn);

        String[] condition = {"Haversine", "Euclidean"};
        ArrayAdapter<String> conditionAdapter;
        conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, condition);
        autoCompleteMathDistance.setAdapter(conditionAdapter);

        reference.child("distance_attendance").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelAttendanceDistance modelAttendanceDistance = task.getResult().getValue(ModelAttendanceDistance.class);
                if (modelAttendanceDistance != null){
                    minDistanceTi.setText(modelAttendanceDistance.getMin_distance());
                    maxDistanceTi.setText(modelAttendanceDistance.getMax_distance());
                    autoCompleteMathDistance.setText(modelAttendanceDistance.getMath_distance(), false);
                    autoCompleteMathDistance.setSelection(autoCompleteMathDistance.getText().length());
                }
            }else {
                Toast.makeText(Dashboard.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        finishBtn.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            String minDistance = minDistanceTi.getText().toString();
            String maxDistance = maxDistanceTi.getText().toString();
            String mathDistance = autoCompleteMathDistance.getText().toString();

            if (minDistance.isEmpty()){
                Toast.makeText(this, "Minimum distance is null", Toast.LENGTH_SHORT).show();
            }else if (maxDistance.isEmpty()){
                Toast.makeText(this, "Maximum distance is null", Toast.LENGTH_SHORT).show();
            }else if (mathDistance.isEmpty()){
                Toast.makeText(this, "Math distance is null", Toast.LENGTH_SHORT).show();
            }else {
                saveDataDistance(minDistance, maxDistance, mathDistance);
            }

        });

        bottomSheetDialog.setContentView(viewBottomDialog);
    }

    private void saveDataDistance(String minDistance, String maxDistance, String mathDistance){
        ModelAttendanceDistance modelAttendanceDistance = new ModelAttendanceDistance(
                minDistance,
                maxDistance,
                mathDistance
        );

        reference.child("distance_attendance").setValue(modelAttendanceDistance)
                .addOnSuccessListener(unused ->
                        Toast.makeText(Dashboard.this, "Distance saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}