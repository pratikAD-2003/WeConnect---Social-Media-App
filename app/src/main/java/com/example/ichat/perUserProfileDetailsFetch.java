package com.example.ichat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class perUserProfileDetailsFetch extends AppCompatActivity {

    CircleImageView profileImg;
    TextView Name,about,num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_user_profile_details_fetch);
        profileImg = findViewById(R.id.aboutProfileImg);
        Name = findViewById(R.id.aboutOtherName);
        about = findViewById(R.id.aboutOtherDes);
        num = findViewById(R.id.aboutOtherNum);

        String uri = getIntent().getStringExtra("otherUri");
        String name = getIntent().getStringExtra("otherName");
        String des = getIntent().getStringExtra("otherDes");
        String number = getIntent().getStringExtra("otherNum");

        Glide.with(this).load(uri).into(profileImg);
        num.setText(number);
        Name.setText(name);
        about.setText(des);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}