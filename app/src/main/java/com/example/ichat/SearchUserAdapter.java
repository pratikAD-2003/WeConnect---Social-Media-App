package com.example.ichat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyUserSearchHolder> {
    Context context;
    ArrayList<ProfileModel> models;
    public SearchUserAdapter(Context context, ArrayList<ProfileModel> models) {
        this.context = context;
        this.models = models;
    }

    public void updateList(List<ProfileModel> list2){
        models = (ArrayList<ProfileModel>) list2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyUserSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_interface_list, parent, false);
        return new MyUserSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserSearchHolder holder, int position) {
        holder.name.setText(models.get(position).getUsername());
        holder.number.setText(models.get(position).getDisplayName());
        Glide.with(context).load(models.get(position).getProfileUri()).into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUserProfileFollow.class);
                intent.putExtra("perUserN", models.get(position).getUsername());
                intent.putExtra("DisplayName", models.get(position).getDisplayName());
                intent.putExtra("aboutUser", models.get(position).getAboutUser());
                intent.putExtra("userUid", models.get(position).getUserId());
                intent.putExtra("userNumber", models.get(position).getUserNumber());
                intent.putExtra("ProfileUri", models.get(position).getProfileUri());
                intent.putExtra("FCM_TOKEN", models.get(position).getFcmToken());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyUserSearchHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView name, number;

        public MyUserSearchHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.perUserImg);
            name = itemView.findViewById(R.id.perUserName);
            number = itemView.findViewById(R.id.perUserNumber);
        }
    }
}
