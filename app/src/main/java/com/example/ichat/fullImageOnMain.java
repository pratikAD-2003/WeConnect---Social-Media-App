package com.example.ichat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class fullImageOnMain extends AppCompatActivity {
    PhotoView profileImgFull;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_on_main);
        toolbar = findViewById(R.id.fullImgTool);
        profileImgFull = findViewById(R.id.fullScreenImgProfile);

        String title = getIntent().getStringExtra("userFullName");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        String uri = getIntent().getStringExtra("uriFullImg");
        Glide.with(this).load(uri).into(profileImgFull);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}