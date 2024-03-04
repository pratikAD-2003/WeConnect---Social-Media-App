package com.example.ichat.Post_Details;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ichat.MainScreenModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.example.ichat.R;
import com.example.ichat.chatModel;
import com.example.ichat.chat_screen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class img_post_details extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference reference;
    ImageView selected_img;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference root = FirebaseStorage.getInstance().getReference("User_Post_Images");
    EditText caption;
    Button upload_post;
    String getTime = "", userName = "", userPic = "", about = "", displayName = "",fcmToken = "";
    String pushID = "";
    TextView textView;
    ImageView imageView;
    Uri cropped;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        setContentView(R.layout.activity_img_post_details);
        selected_img = findViewById(R.id.selected_post_img);
        caption = findViewById(R.id.img_post_caption);
        upload_post = findViewById(R.id.img_post_upload);
        textView = findViewById(R.id.upload_post_img_text);
        imageView = findViewById(R.id.upload_post_select_btn);
        reference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        userName = sp.getString("username", "Please Set Username.");
        userPic = sp.getString("profileUri", "Invalid.");
        displayName = sp.getString("DisplayName", "Default");
        about = sp.getString("about", "Please Set About Yourself.");
        fcmToken = sp.getString("fcmToken", "not");

//        postUri = Uri.parse(getIntent().getStringExtra("postURI"));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10112);

            }
        });

        upload_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!caption.getText().toString().equals("") || cropped != null) {
                    imageToUri(cropped);
                }
            }
        });
    }

    public void imageToUri(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dd = LocalDateTime.now();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            getTime = f.format(dd);
        }
        StorageReference fileRef = root.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pushID = reference.push().getKey();
                        postModel model = new postModel(caption.getText().toString(), String.valueOf(uri), getTime, userName, userPic, uid, pushID, postModel.IMAGE_POST,displayName,fcmToken,about);
                        reference.child("User_Post_Images").child(pushID).setValue(model);
                        reference.child("OurPosts").child(uid).child(pushID).setValue(model);
                        Toast.makeText(img_post_details.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        notifyPostUploaded(userName);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(img_post_details.this, "Please Try Again Later!", Toast.LENGTH_SHORT).show();
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

    public void notifyPostUploaded(String title) {
        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topic/all", title, "Checkout My New Post!", this, img_post_details.this);
        fcmNotificationsSender.SendNotifications();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10112 && resultCode == RESULT_OK) {
            Uri cropUri = data.getData();
            if (cropUri != null) {
                startCrop(cropUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            cropped = UCrop.getOutput(data);
            if (cropped != null) {
                selected_img.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
//                Glide.with(this).load(cropped).into(selected_img);
                selected_img.setImageURI(cropped);
            }
        }
    }

    public void startCrop(Uri uri) {
        String destinationFileName = "WeConnectCropImage";
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
//        uCrop.useSourceImageAspectRatio();
        uCrop.withMaxResultSize(1000, 1000);
        uCrop.withOptions(getCropOptions());
        uCrop.start(img_post_details.this);

    }


    public UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setMaxBitmapSize(10000);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);

        options.setStatusBarColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary_dark));
        options.setStatusBarColor(getResources().getColor(com.google.android.material.R.color.design_dark_default_color_primary));

        options.setToolbarTitle("Cropped Image");

        return options;

    }


}