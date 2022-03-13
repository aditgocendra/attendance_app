package com.ark.attendanceapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ark.attendanceapp.Model.ModelUser;
import com.ark.attendanceapp.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsersList extends RecyclerView.Adapter<AdapterUsersList.UsersListHolder> {

    private final List<ModelUser> listUsers;
    private Context mContext;

    public AdapterUsersList(List<ModelUser> listUsers, Context mContext) {
        this.listUsers = listUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UsersListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_grid_card_user, parent, false);
        return new UsersListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListHolder holder, int position) {
        ModelUser modelUser = listUsers.get(position);

        holder.textUsername.setText(modelUser.getUsername());
        if (!modelUser.getUrl_photo().equals("-")){
            Picasso.get().load(modelUser.getUrl_photo()).into(holder.circleImageProfile);
        }

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public static class UsersListHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageProfile;
        CardView cardUser;
        TextView textUsername;
        public UsersListHolder(@NonNull View itemView) {
            super(itemView);
            circleImageProfile = itemView.findViewById(R.id.image_user_profile_iv);
            textUsername = itemView.findViewById(R.id.username_tv);
            cardUser = itemView.findViewById(R.id.card_user);
        }
    }
}
