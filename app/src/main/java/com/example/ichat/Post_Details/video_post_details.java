package com.example.ichat.Post_Details;

import static com.google.common.io.Files.getFileExtension;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ichat.MainScreenModel;
import com.example.ichat.R;
import com.example.ichat.chatModel;
import com.example.ichat.chat_screen;
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
import java.util.Date;
import java.util.SimpleTimeZone;

public class video_post_details extends AppCompatActivity {
    StyledPlayerView videoView;
    ExoPlayer exoPlayer;
    EditText videoCaption;
    Button postVideo;
    String url;
    Uri videoUri;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference reference;
    StorageReference root = FirebaseStorage.getInstance().getReference("User_Post_Videos");
    String getTime = "", userName = "", userPic = "";
    String pushId = "", displayName = "", about = " ", fcmToken = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_post_details);
        videoView = findViewById(R.id.selected_post_video);
        videoCaption = findViewById(R.id.post_video_caption);
        postVideo = findViewById(R.id.video_post_upload);
        reference = FirebaseDatabase.getInstance().getReference();

        url = getIntent().getStringExtra("postURI");
        videoUri = Uri.parse(url);

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        userName = sp.getString("username", "Please Set Username.");
        userPic = sp.getString("profileUri", "Invalid.");
        displayName = sp.getString("DisplayName", "Default");
        about = sp.getString("about", "Please Set About Yourself.");
        fcmToken = sp.getString("fcmToken", "not");

        exoPlayer = new ExoPlayer.Builder(video_post_details.this).build();
        videoView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
        postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoCaption.getText().toString().equals("")) {
                    Toast.makeText(video_post_details.this, "Please Fill Required Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    videoToDatabase(videoUri);
//                    startCompressing(videoUri);
                }
            }
        });

    }

    public void videoToDatabase(Uri uri) {

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
                        pushId = reference.push().getKey();
                        postModel model = new postModel(videoCaption.getText().toString(), String.valueOf(uri), getTime, userName, userPic, uid, pushId, postModel.VIDEO_POST, displayName, fcmToken, about);
                        reference.child("User_Post_Images").push().setValue(model);
                        reference.child("OurPosts").child(uid).push().setValue(model);

                        Toast.makeText(video_post_details.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(video_post_details.this, "Please Try Again Later!", Toast.LENGTH_SHORT).show();
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

//    public void startCompressing(Uri uri) {
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//        new CompressVideo().execute("false", uri.toString(), file.getPath());
//    }

//    public class CompressVideo extends AsyncTask<String, String, String> {
//        Dialog dialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = ProgressDialog.show(video_post_details.this, "", "Compressing...");
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String videoPath = null;
//            try {
//                Uri uri = Uri.parse(strings[1]);
//                videoPath = SiliCompressor.with(video_post_details.this).compressVideo(uri, strings[2]);
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            }
//            return videoPath;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            dialog.dismiss();
//
//            File file = new File(s);
//            Uri uri = Uri.fromFile(file);
//            videoToDatabase(uri);
//        }
//    }
}