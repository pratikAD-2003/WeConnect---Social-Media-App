package com.example.ichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    CircleImageView profileImage;
    Button createProfile;
    EditText userName, aboutUser, givenNumber, onDisplayName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid().toString();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserList").child(uid);
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UserProfilePics").child(uid);
    Uri profileUri;
    String imageUri;
    Toolbar toolbar;
    String token = "";
    FirebaseFirestore db;
    String userN = "", about = " ", DisplayName = "", number = " ";

    @Override
    protected void onStart() {

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        String status = sp.getString("status", "false");
        SharedPreferences.Editor ed = sp.edit();

        if (status.equals("true")) {
            startActivity(new Intent(UserProfile.this, MainActivity.class));
            finish();
        } else {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Wait...");
            dialog.setMessage("We are fetching your details.");
            dialog.setCancelable(false);
            dialog.show();
            getFCMToken();

            try {
                db.collection("UserList").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                String userID = documentSnapshot.getString("userId");
                                if (Objects.equals(uid, userID)) {
                                    profileUri = Uri.parse(documentSnapshot.getString("profileUri"));
                                    aboutUser.setText(documentSnapshot.getString("aboutUser"));
                                    userName.setText(documentSnapshot.getString("username"));
                                    givenNumber.setText(documentSnapshot.getString("userNumber"));
                                    onDisplayName.setText(documentSnapshot.getString("displayName"));
                                    Glide.with(UserProfile.this).load(documentSnapshot.getString("profileUri")).into(profileImage);
                                    db.collection("UserList").document(uid).update("fcmToken", token);

                                    ed.putString("username", documentSnapshot.getString("username"));
                                    ed.putString("DisplayName", documentSnapshot.getString("displayName"));
                                    ed.putString("about", documentSnapshot.getString("aboutUser"));
                                    ed.putString("profileUri", String.valueOf(profileUri));
                                    ed.putString("status", "true");
                                    ed.putString("number", documentSnapshot.getString("userNumber"));
                                    ed.putString("fcmToken", token);
                                    ed.apply();
                                    dialog.dismiss();
                                    startActivity(new Intent(UserProfile.this, MainActivity.class));
                                    finish();
                                } else {
                                    dialog.dismiss();
                                }
                            } else {
                                dialog.dismiss();
                            }
                        }
                    }
                });
            } catch (Exception e) {

            }
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileImage = findViewById(R.id.profileImg);
        createProfile = findViewById(R.id.createProfile);
        userName = findViewById(R.id.userName);
        aboutUser = findViewById(R.id.about);
        toolbar = findViewById(R.id.createProfileDetails);
        givenNumber = findViewById(R.id.given_Number);
        onDisplayName = findViewById(R.id.onDisplayName);

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();


        db = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        givenNumber.setText(user.getPhoneNumber());
        givenNumber.setEnabled(false);

        getFCMToken(); // generate token

        if (Build.VERSION.SDK_INT >= 33) {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dexter.withContext(getApplicationContext()).withPermissions(android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_MEDIA_VIDEO, android.Manifest.permission.READ_MEDIA_AUDIO).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            uploadProfileImg();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).onSameThread().check();
                }
            });
        } else {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dexter.withContext(getApplicationContext()).withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            uploadProfileImg();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).onSameThread().check();
                }
            });
        }

        userName.addTextChangedListener(new TextWatcher() {
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
                                if (Objects.equals(check, userName.getText().toString())) {
                                    userName.setError("Username has been taken already!");
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
                                if (Objects.equals(check, userName.getText().toString())) {
                                    userName.setError("Username has been taken already!");
                                } else {
                                }
                            }
                        }
                    }
                });
            }
        });

        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userN = userName.getText().toString();
                about = aboutUser.getText().toString();
                DisplayName = onDisplayName.getText().toString();
                number = user.getPhoneNumber();

                if (userN.equals("") && about.equals("") && DisplayName.equals("")) {
                    Toast.makeText(UserProfile.this, "Please Fill All Required Fields.", Toast.LENGTH_SHORT).show();
                } else if (userN.equals("")) {
                    userName.setError("Please Enter Username.");
                } else if (DisplayName.equals("")) {
                    userName.setError("Please Enter Name To Be Display!");
                } else if (about.equals("")) {
                    aboutUser.setError("Please Enter Description Message.");
                } else {
                    if (profileUri == null) {
                        Toast.makeText(UserProfile.this, "Please Select Profile Picture.", Toast.LENGTH_SHORT).show();
                    } else {
                        storageReference.putFile(profileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUri = uri.toString();
                                            ProfileModel model = new ProfileModel(DisplayName, imageUri, userN, about, number, uid, token);
                                            db.collection("UserList").document(uid).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    ed.putString("username", userN);
                                                    ed.putString("DisplayName", DisplayName);
                                                    ed.putString("about", about);
                                                    ed.putString("profileUri", imageUri);
                                                    ed.putString("status", "true");
                                                    ed.putString("number", number);
                                                    ed.putString("fcmToken", token);
                                                    ed.apply();
                                                    Toast.makeText(UserProfile.this, "Profile Created!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(UserProfile.this, MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });
                                }
                            }
                        });
                    }
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
                profileUri = data.getData();
                Glide.with(getApplicationContext()).load(profileUri).into(profileImage);
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

    // creating fcm token
    public void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                token = task.getResult();
//                Log.d("FCM",token);
//                Intent intent = new Intent(UserProfile.this,userDetails.class);
//                intent.putExtra("FCM",token);
//                startActivity(intent);
            }
        });
    }

    public void isAvailable(String username) {
        db.collection("UserList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        String check = snapshot.getString("username");
                        if (Objects.equals(check, username)) {
//                            isAvailable = false;
                            Toast.makeText(UserProfile.this, "not", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfile.this, "yes", Toast.LENGTH_SHORT).show();
//                            isAvailable = true;
                        }
                    }
                }
            }
        });
    }
}