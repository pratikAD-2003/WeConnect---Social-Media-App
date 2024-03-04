package com.example.ichat.UsersActivities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ichat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.MyHolder> {
    Context context;
    Activity activity;

    public ActivityAdapter(Context context, Activity activity, ArrayList<ActivityModel> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }

    ArrayList<ActivityModel> list;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activities_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.activityText.setText(list.get(position).getActivity());
        holder.date.setText(list.get(position).getDate());
        Glide.with(context).load(list.get(position).getProfileUri()).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView activityText, date;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.activityList_user_profile);
            activityText = itemView.findViewById(R.id.activities_text);
            date = itemView.findViewById(R.id.activities_text_date);
        }
    }

}
