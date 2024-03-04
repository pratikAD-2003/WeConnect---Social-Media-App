package com.example.ichat.Post_Details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ichat.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class reel_post_details extends AppCompatActivity {

    StyledPlayerView playerView;
    Button upload_reel;
    EditText getTitle;
    ExoPlayer exoPlayer;
    String url;
    Uri videoUri;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference reference;
    StorageReference root = FirebaseStorage.getInstance().getReference("User_Reel_Videos");
    String getTime = "", userName = "", userPic = "", about = " ", fcmToken = "", displayName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reel_post_details);
        playerView = findViewById(R.id.reel_player);
        upload_reel = findViewById(R.id.reel_upload);
        getTitle = findViewById(R.id.post_reel_title);
        reference = FirebaseDatabase.getInstance().getReference();

        url = getIntent().getStringExtra("postURI");
        videoUri = Uri.parse(url);

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        userName = sp.getString("username", "Please Set Username.");
        userPic = sp.getString("profileUri", "Invalid.");

        fcmToken = sp.getString("fcmToken", "not");
        about = sp.getString("about", "Please Set About Yourself.");
        displayName = sp.getString("DisplayName", "Default");


        exoPlayer = new ExoPlayer.Builder(reel_post_details.this).build();
        playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
        upload_reel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTitle.getText().toString().equals("")) {
                    Toast.makeText(reel_post_details.this, "Please Fill Required Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    reelToDatabase(videoUri);
                }
            }
        });
    }

    public void reelToDatabase(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dd = LocalDateTime.now();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            getTime = f.format(dd);
        }
        StorageReference fileRef = root.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String pushId = reference.push().getKey();
                        reelModel model = new reelModel(getTime, String.valueOf(uri), getTitle.getText().toString(), userName, uid, userPic, pushId, about, fcmToken, displayName);
                        reference.child("User_Post_Reels").child(pushId).setValue(model);
                        Toast.makeText(reel_post_details.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(reel_post_details.this, "Please Try Again Later!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float per = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded : " + (int) per + "%");
            }
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }
}