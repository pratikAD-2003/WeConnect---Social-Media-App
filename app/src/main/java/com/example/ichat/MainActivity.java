package com.example.ichat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ichat.tabItems.PageAdapter;
import com.example.ichat.tabItems.callFrage;
import com.example.ichat.tabItems.chatFrage;
import com.example.ichat.tabItems.reel_frage;
import com.example.ichat.tabItems.statusFrage;
import com.example.ichat.tabItems.upload_post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    //    ViewPager2 viewPager;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    PageAdapter adapter;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    statusFrage statusFrage = new statusFrage();
    reel_frage reelFrage = new reel_frage();
    upload_post uploadPost = new upload_post();
    chatFrage chatFrage = new chatFrage();
    callFrage callFrage = new callFrage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
//        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_view);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, statusFrage).commit(); // set frage when open app


        permissionCheck();

        fragmentArrayList.add(new statusFrage());
        fragmentArrayList.add(new chatFrage());
        fragmentArrayList.add(new upload_post());
        fragmentArrayList.add(new reel_frage());
        fragmentArrayList.add(new callFrage());

//        adapter = new PageAdapter(this, fragmentArrayList);
//        viewPager.setAdapter(adapter);
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        bottomNavigationView.setSelectedItemId(R.id.home_btn);
//                        break;
//                    case 1:
//                        bottomNavigationView.setSelectedItemId(R.id.reel_btn);
//                        break;
//                    case 2:
//                        bottomNavigationView.setSelectedItemId(R.id.post_upload_btn);
//                        break;
//                    case 3:
//                        bottomNavigationView.setSelectedItemId(R.id.chat_btn);
//                        break;
//                    case 4:
//                        bottomNavigationView.setSelectedItemId(R.id.profile_btn);
//                }
//                super.onPageSelected(position);
//            }
//        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home_btn) {
//                    viewPager.setCurrentItem(0);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, statusFrage).commit();
                } else if (id == R.id.reel_btn) {
//                    viewPager.setCurrentItem(1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, reelFrage).commit();
                } else if (id == R.id.post_upload_btn) {
//                    viewPager.setCurrentItem(2);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, uploadPost).commit();
                } else if (id == R.id.chat_btn) {
//                    viewPager.setCurrentItem(3);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, chatFrage).commit();
                } else {
//                    viewPager.setCurrentItem(4);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_frame, callFrage).commit();
                }
                return true;
            }
        });

    }


    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 33) {
            Dexter.withContext(this).withPermissions(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).onSameThread().check();
        } else {
            Dexter.withContext(this).withPermissions(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).onSameThread().check();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        reference.child("OnAppStatus").child(uid).child("active").setValue("true");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        reference.child("OnAppStatus").child(uid).child("active").setValue("false");
    }
}