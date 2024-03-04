package com.example.ichat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyMainHolder>  {
    Context context;
    ArrayList<MainScreenModel> list;
    ArrayList<MainScreenModel> backupList;

    public MainAdapter(Context context, ArrayList<MainScreenModel> list) {
        this.context = context;
        this.list = list;
        backupList = new ArrayList<>(list);
    }
    public void updateList(List<MainScreenModel> list2){
        list = (ArrayList<MainScreenModel>) list2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyMainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_screen_users, parent, false);
        return new MyMainHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMainHolder holder, int position) {
//        list = new ArrayList<>();
        holder.username.setText(list.get(position).getUsername());
        holder.number.setText(list.get(position).getAboutUser());
        Glide.with(context).load(list.get(position).getUri()).into(holder.profilePic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chat_screen.class);
                intent.putExtra("USER_NAME", list.get(position).getUsername());
                intent.putExtra("ABOUT_USER", list.get(position).getAboutUser());
                intent.putExtra("RECEIVER_UID", list.get(position).getUserUid());
                intent.putExtra("D_NAME", list.get(position).getDisplayName());
                intent.putExtra("USER_NUMBER", list.get(position).getUserNumber());
                intent.putExtra("PROFILE_URI", list.get(position).getUri());
                intent.putExtra("FCM_TOKEN_FOR_NOTI", list.get(position).getFcm_token());
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, fullImageOnMain.class);
                intent.putExtra("uriFullImg", list.get(position).getUri());
                intent.putExtra("userFullName", list.get(position).getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyMainHolder extends RecyclerView.ViewHolder {
        TextView username, number;
        CircleImageView profilePic;

        public MyMainHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.perUserNameOnMainScreen);
            number = itemView.findViewById(R.id.perUserNumberOnMainScreen);
            profilePic = itemView.findViewById(R.id.oppositeUserPic);
        }
    }
}
