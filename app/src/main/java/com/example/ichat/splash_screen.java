package com.example.ichat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash_screen extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.logo_anim);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.logo_anim);
        logo.startAnimation(animation);

        if (user != null) {
            Intent UP = new Intent(splash_screen.this, UserProfile.class);
            new Handler().postDelayed(new Runnable() { // It helps to hold the screen
                @Override
                public void run() {
                    startActivity(UP);
                    finish(); // It restricted to back main intent
                }
            }, 500);  // Set Screen Holds Timing
        }
        if (user == null) {
            Intent intent = new Intent(splash_screen.this, login.class);
            new Handler().postDelayed(new Runnable() { // It helps to hold the screen
                @Override
                public void run() {
                    startActivity(intent);
                    finish(); // It restricted to back main intent
                }
            }, 500);  // Set Screen Holds Timing
        }

    }
}