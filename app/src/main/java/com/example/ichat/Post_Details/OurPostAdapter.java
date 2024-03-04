package com.example.ichat.Post_Details;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ichat.Post_Maintainence.TapOnImage;
import com.example.ichat.Post_Maintainence.TapOnVideo;
import com.example.ichat.R;

import java.util.ArrayList;

public class OurPostAdapter extends RecyclerView.Adapter<OurPostAdapter.MViewHolder> {
    Context context;
    ArrayList<postModel> list;

    public OurPostAdapter(Context context, ArrayList<postModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.our_post_items, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getAudioUri()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getType() == 0) {
                    Intent intent = new Intent(context, TapOnImage.class);
                    intent.putExtra("ONTAP_POST_URI", list.get(position).getAudioUri());
                    intent.putExtra("ONTAP_POST_TIME", list.get(position).getPostTime());
                    intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getCaption());
                    intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUsername());
                    intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUserPic());
                    intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                    intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                    intent.putExtra("ONTAP_POST_UID",list.get(position).getUid());
                    intent.putExtra("ONTAP_FCM_TOKEN",list.get(position).getFcmToken());
                    intent.putExtra("ONTAP_ABOUT_USER",list.get(position).getAboutUser());
                    intent.putExtra("ONTAP_DISPLAY_NAME",list.get(position).getDisplayName());
                    context.startActivity(intent);
                } else if (list.get(position).getType() == 1){
                    Intent intent = new Intent(context, TapOnVideo.class);
                    intent.putExtra("ONTAP_POST_URI", list.get(position).getAudioUri());
                    intent.putExtra("ONTAP_POST_TIME", list.get(position).getPostTime());
                    intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getCaption());
                    intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUsername());
                    intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUserPic());
                    intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                    intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                    intent.putExtra("ONTAP_POST_UID",list.get(position).getUid());
                    intent.putExtra("ONTAP_FCM_TOKEN",list.get(position).getFcmToken());
                    intent.putExtra("ONTAP_ABOUT_USER",list.get(position).getAboutUser());
                    intent.putExtra("ONTAP_DISPLAY_NAME",list.get(position).getDisplayName());
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.our_post_uri_call_fragement);
        }
    }
}
