package com.example.ichat.tabItems;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ichat.Post_Details.img_post_details;
import com.example.ichat.Post_Details.reel_post_details;
import com.example.ichat.Post_Details.video_post_details;
import com.example.ichat.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;

public class upload_post extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Uri postUri;
    ImageButton upload_image, upload_video, reel_upload;

    public upload_post() {
        // Required empty public constructor
    }

    public static upload_post newInstance(String param1, String param2) {
        upload_post fragment = new upload_post();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_post, container, false);
        upload_image = view.findViewById(R.id.upload_image_post);
        upload_video = view.findViewById(R.id.upload_video_post);
        reel_upload = view.findViewById(R.id.upload_reel_post);

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 10112);

                startActivity(new Intent(getContext(), img_post_details.class));
            }
        });

        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 10113);
            }
        });

        reel_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 10114);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10112 && resultCode == RESULT_OK) {
            postUri = data.getData();
            Intent intent = new Intent(getContext(), img_post_details.class);
            intent.putExtra("postURI", String.valueOf(postUri));
            startActivity(intent);
        } else if (requestCode == 10113 && resultCode == RESULT_OK) {
            postUri = data.getData();
            Intent intent = new Intent(getContext(), video_post_details.class);
            intent.putExtra("postURI", String.valueOf(postUri));
            startActivity(intent);
        } else if (requestCode == 10114 && resultCode == RESULT_OK) {
            postUri = data.getData();
            Intent intent = new Intent(getContext(), reel_post_details.class);
            intent.putExtra("postURI", String.valueOf(postUri));
            startActivity(intent);
        }
    }

}