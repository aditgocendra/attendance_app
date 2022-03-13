package com.ark.attendanceapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ark.attendanceapp.Model.ModelAttendanceUsers;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAttendanceRecap extends RecyclerView.Adapter<AdapterAttendanceRecap.AttendanceRecapViewHolder> {

    private List<ModelAttendanceUsers> listAttendanceUsers;
    private Context mContext;
    private BottomSheetDialog bottomSheetDialog;

    public AdapterAttendanceRecap(List<ModelAttendanceUsers> listAttendanceUsers, Context mContext) {
        this.listAttendanceUsers = listAttendanceUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterAttendanceRecap.AttendanceRecapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_grid_card_user, parent, false);
        return new AttendanceRecapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAttendanceRecap.AttendanceRecapViewHolder holder, int position) {
        ModelAttendanceUsers modelAttendanceUsers = listAttendanceUsers.get(position);
        getDataUsers(modelAttendanceUsers.getKeyUser(), holder);

        holder.cardUser.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(mContext);
            setBottomDialogDetail(modelAttendanceUsers);
            bottomSheetDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return listAttendanceUsers.size();
    }

    public static class AttendanceRecapViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageProfile;
        CardView cardUser;
        TextView textUsername;
        public AttendanceRecapViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageProfile = itemView.findViewById(R.id.image_user_profile_iv);
            textUsername = itemView.findViewById(R.id.username_tv);
            cardUser = itemView.findViewById(R.id.card_user);
        }
    }

    private void getDataUsers(String keyUser, AttendanceRecapViewHolder holder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(keyUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                if (modelUser != null){
                    holder.textUsername.setText(modelUser.getUsername());
                    if (!modelUser.getUrl_photo().equals("-")){
                        Picasso.get().load(modelUser.getUrl_photo()).into(holder.circleImageProfile);
                    }
                }else {
                    Toast.makeText(mContext, "Not data user found", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottomDialogDetail(ModelAttendanceUsers modelAttendanceUsers){
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewBottomDialog = li.inflate(R.layout.layout_dialog_detail_attendance, null, false);

        TextView textDateAttend, textTimeAttend, textDistanceAttend;
        textDateAttend = viewBottomDialog.findViewById(R.id.text_date_attend);
        textTimeAttend = viewBottomDialog.findViewById(R.id.text_time_attend);
        textDistanceAttend = viewBottomDialog.findViewById(R.id.text_distance_attend);

        Button closeBtn = viewBottomDialog.findViewById(R.id.close_btn);

        textDateAttend.setText(modelAttendanceUsers.getDay());
        textTimeAttend.setText(modelAttendanceUsers.getTime());
        textDistanceAttend.setText(modelAttendanceUsers.getDistance());

        closeBtn.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(viewBottomDialog);

    }
}
