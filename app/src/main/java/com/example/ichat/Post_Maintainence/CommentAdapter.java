package com.example.ichat.Post_Maintainence;

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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyHolderView> {
    Context context;
    ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_items, parent, false);
        return new MyHolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolderView holder, int position) {
        Glide.with(context).load(list.get(position).getUserProfile()).into(holder.profile);
        holder.userComment.setText(list.get(position).getUserComment());
        holder.userName.setText(list.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView userName, userComment;

        public MyHolderView(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.comment_user_profile);
            userName = itemView.findViewById(R.id.comment_user_username);
            userComment = itemView.findViewById(R.id.comment_user_comment);
        }
    }
}
