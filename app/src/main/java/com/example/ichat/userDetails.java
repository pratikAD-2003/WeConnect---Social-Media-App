package com.example.ichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class userDetails extends AppCompatActivity {

    CircleImageView profileImg;
    EditText username, description, number, displayName;
    Button update;
    Uri newProfileImg;
    ProgressBar progressBar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid().toString();
    Toolbar toolbar;
    String imageUri;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserList").child(uid);
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UserProfilePics").child(uid);
    FirebaseFirestore db;
    String token = "";

    ProgressBar update_bar;

    @Override
    protected void onStart() {
        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        String status = sp.getString("status", "false");

        token = sp.getString("fcmToken", "not");

        if (status.equals("true")) {
            username.setText(sp.getString("username", "Please Set Username."));
            displayName.setText(sp.getString("DisplayName", "Default"));
            description.setText(sp.getString("about", "Please Set About Yourself."));
            number.setText(sp.getString("number", "+91XXXXXXXXXX"));
            Glide.with(this).load(sp.getString("profileUri", "Invalid.")).into(profileImg);
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        profileImg = findViewById(R.id.userUploadedImg);
        username = findViewById(R.id.storedUsesN);
        description = findViewById(R.id.storedDes);
        number = findViewById(R.id.storedNumber);
        update = findViewById(R.id.updateProfile);
        toolbar = findViewById(R.id.toolBarUserDetails);
        progressBar = findViewById(R.id.progressUpdateProf);
        db = FirebaseFirestore.getInstance();
        update_bar = findViewById(R.id.update_profile_bar);
        displayName = findViewById(R.id.storedDisplayN);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        number.setEnabled(false);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileImg();
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                db.collection("UserList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                String check = snapshot.getString("username");
                                if (Objects.equals(check, username.getText().toString())) {
                                    username.setError("Username has been taken already!");
                                } else {
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                db.collection("UserList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                String check = snapshot.getString("username");
                                if (Objects.equals(check, username.getText().toString())) {
                                    username.setError("Username has been taken already!");
                                } else {
                                }
                            }
                        }
                    }
                });
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String DisplayN = displayName.getText().toString();
                String des = description.getText().toString();
                String number = user.getPhoneNumber().toString();
                if (newProfileImg == null) {
                    SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
                    newProfileImg = Uri.parse(sp.getString("profileUri", "Invalid."));

                    ProfileModel model = new ProfileModel(DisplayN, String.valueOf(newProfileImg), name, des, number, uid, token);
                    db.collection("UserList").document(uid).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("username", name);
                            ed.putString("about", des);
                            ed.putString("DisplayName", DisplayN);
                            ed.putString("profileUri", String.valueOf(newProfileImg));
                            ed.putString("number", number);
                            ed.putString("status", "true");
                            ed.apply();
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(userDetails.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(userDetails.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(userDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    storageReference.putFile(newProfileImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUri = uri.toString();
                                        ProfileModel model = new ProfileModel(DisplayN, imageUri, name, des, number, uid, token);
                                        db.collection("UserList").document(uid).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
                                                SharedPreferences.Editor ed = sp.edit();
                                                ed.putString("username", name);
                                                ed.putString("about", des);
                                                ed.putString("DisplayName", DisplayN);
                                                ed.putString("profileUri", imageUri);
                                                ed.putString("number", number);
                                                ed.putString("status", "true");
                                                ed.apply();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(userDetails.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(userDetails.this, MainActivity.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(userDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                update_bar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                });
                                update_bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            update_bar.setVisibility(View.VISIBLE);
                            float per2 = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            update_bar.setProgress((int) per2);
                        }
                    });

                }
            }

        });

    }

    public void uploadProfileImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                newProfileImg = data.getData();
                Glide.with(getApplicationContext()).load(newProfileImg).into(profileImg);
            }
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}