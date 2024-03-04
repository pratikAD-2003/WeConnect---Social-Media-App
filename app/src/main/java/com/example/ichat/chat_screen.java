package com.example.ichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chat_screen extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText messageToSend;
    ImageButton send, back, sendPic;
    ChatAdapter adapter;
    ArrayList<chatModel> list;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference reference;
    FirebaseFirestore db;
    CircleImageView pic;
    TextView name, user_status, typing_status;
    LinearLayoutManager manager;
    String receiverRoom = null;
    String senderRoom = null;
    String fcmToken, username, oppositeUserName = "", oppositeNum = "", userNumber = "", receiverUid = "", displayName = "";
    private StorageReference reference2 = FirebaseStorage.getInstance().getReference("ChatImages");
    private StorageReference reference3 = FirebaseStorage.getInstance().getReference("ChatVideos");
    private StorageReference reference4 = FirebaseStorage.getInstance().getReference("ChatAudios");
    Dialog send_img_dialog, select_to_send, send_video_dialog;
    PhotoView imageView1;
    Uri imgUri;
    VideoView videoView;
    //    ProgressBar uploadingBar;
    TextView v_uploading_status;
    ImageButton recordAudio;
    TextView recording_timer_text;
    boolean isRecording = false;
    int seconds = 0;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    LottieAnimationView voice_mic_anim;
    Handler handler;
    MediaRecorder mediaRecorder;
    String oppositeAbout = "", oppositeUri = "", PushId = "", receiverPushId = "", otherFcm = "", ourAbout = "", ourUri = "", ourDisplay = "";

    boolean checkOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        recyclerView = findViewById(R.id.recyclerChat);
        recyclerView.setHasFixedSize(true);
        messageToSend = findViewById(R.id.messageToSend);
        send = findViewById(R.id.sendMsg);
        back = findViewById(R.id.backToMain);
        pic = findViewById(R.id.userOnChatPic);
        name = findViewById(R.id.userNameOnChat);
        user_status = findViewById(R.id.user_status);
        typing_status = findViewById(R.id.typing_status);
        sendPic = findViewById(R.id.send_pic);
//        uploadingBar = findViewById(R.id.upload_video_dialog);
        v_uploading_status = findViewById(R.id.upload_video_status_text);
        recordAudio = findViewById(R.id.record_audio);
        recording_timer_text = findViewById(R.id.recording_text_time);
        voice_mic_anim = findViewById(R.id.audio_mic_anim);

        recordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // start recording
                    recording_timer_text.setVisibility(View.VISIBLE);
                    recordAudio.setVisibility(View.GONE);
                    voice_mic_anim.setVisibility(View.VISIBLE);
                    startRecordingAudio();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // stop recording
                    recording_timer_text.setVisibility(View.GONE);
                    recordAudio.setVisibility(View.VISIBLE);
                    voice_mic_anim.setVisibility(View.GONE);
                    stopRecordingAudio();
                }
                return true;
            }
        });


        select_to_send = new Dialog(chat_screen.this);
        select_to_send.setContentView(R.layout.select_dialog);
        select_to_send.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        select_to_send.setCancelable(true);
        ImageButton selectImg = select_to_send.findViewById(R.id.selectImg);
        ImageButton selectVideo = select_to_send.findViewById(R.id.selectVideo);
        ImageButton cameraPhoto = select_to_send.findViewById(R.id.cameraPhotoUpload);


        send_img_dialog = new Dialog(chat_screen.this);
        send_img_dialog.setContentView(R.layout.custom_dialog);
        send_img_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        send_img_dialog.setCancelable(true);
        imageView1 = send_img_dialog.findViewById(R.id.imgToUpload);
        Button upload = send_img_dialog.findViewById(R.id.upload);

        send_video_dialog = new Dialog(chat_screen.this);
        send_video_dialog.setContentView(R.layout.video_dialog);
        send_video_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        send_video_dialog.setCancelable(true);
        Button uploadVideo = send_video_dialog.findViewById(R.id.upload_video);
        videoView = send_video_dialog.findViewById(R.id.videoToUpload);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgUri != null) {
                    imageToUri(imgUri);
                } else {
                    Toast.makeText(chat_screen.this, "Please select image!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgUri != null) {
                    videoToDatabase(imgUri);
                    send_video_dialog.dismiss();
                } else {
                    Toast.makeText(chat_screen.this, "Select Video!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        manager = new LinearLayoutManager(this);
        manager.setReverseLayout(false);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ChatAdapter(this, list);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference();

        Date d = new Date(); // to get times

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        fcmToken = sp.getString("fcmToken", "false");
        username = sp.getString("username", "Please Set Username.");
        userNumber = sp.getString("number", "Invalid");
        ourAbout = sp.getString("about", "Please Set About Yourself.");
        ourUri = sp.getString("profileUri", "Invalid.");
        ourDisplay = sp.getString("DisplayName", "Default");


        oppositeUserName = getIntent().getStringExtra("USER_NAME");
        oppositeAbout = getIntent().getStringExtra("ABOUT_USER");
        oppositeNum = getIntent().getStringExtra("USER_NUMBER");
        oppositeUri = getIntent().getStringExtra("PROFILE_URI");
        receiverUid = getIntent().getStringExtra("RECEIVER_UID");
        otherFcm = getIntent().getStringExtra("FCM_TOKEN_FOR_NOTI");
        displayName = getIntent().getStringExtra("D_NAME");

        // On Typing Status Check

        messageToSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length() == 0) {
                    // set text to Not typing
                } else {
                    reference.child("OnAppTypingStatus").child(receiverUid + uid).child("isTyping").setValue("true");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                reference.child("OnAppTypingStatus").child(receiverUid + uid).child("isTyping").setValue("true");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    // set text to Stopped typing
                    reference.child("OnAppTypingStatus").child(receiverUid + uid).child("isTyping").setValue("false");
                }
            }
        });

        onAppStatus();
        onTypingStatus();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        name.setText(displayName);
        Glide.with(getApplicationContext()).load(oppositeUri).into(pic);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chat_screen.this, OtherUserProfileFollow.class);
                intent.putExtra("ProfileUri", oppositeUri);
                intent.putExtra("perUserN", oppositeUserName);
                intent.putExtra("aboutUser", oppositeAbout);
                intent.putExtra("userNumber", oppositeNum);
                intent.putExtra("FCM_TOKEN", otherFcm);
                intent.putExtra("DisplayName", displayName);
                intent.putExtra("userUid", receiverUid);
                startActivity(intent);
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chat_screen.this, OtherUserProfileFollow.class);
                intent.putExtra("ProfileUri", oppositeUri);
                intent.putExtra("perUserN", oppositeUserName);
                intent.putExtra("aboutUser", oppositeAbout);
                intent.putExtra("userNumber", oppositeNum);
                intent.putExtra("FCM_TOKEN", otherFcm);
                intent.putExtra("DisplayName", displayName);
                intent.putExtra("userUid", receiverUid);
                startActivity(intent);
            }
        });

        senderRoom = receiverUid + uid;
        receiverRoom = uid + receiverUid;

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    scrollToBottom();
                }
            }
        });

        if (list != null) {
            reference.child("Chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot d : snapshot.getChildren()) {
                        chatModel model = d.getValue(chatModel.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String times = d.getHours() + ":" + d.getMinutes();
                String message = messageToSend.getText().toString();
                PushId = reference.push().getKey();
                chatModel model = new chatModel(chatModel.CHAT_LAYOUT, message, uid, times, PushId);
                MainScreenModel model1 = new MainScreenModel(oppositeUri, oppositeUserName, oppositeNum, oppositeAbout, receiverUid, uid, displayName, otherFcm);
                MainScreenModel model2 = new MainScreenModel(ourUri, username, userNumber, ourAbout, uid, receiverUid, ourDisplay, fcmToken);
                reference.child("Chats").child(senderRoom).child("messages").child(PushId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        PushId = reference.push().getKey();
                        receiverPushId = PushId;
                        chatModel model = new chatModel(chatModel.CHAT_LAYOUT, message, uid, times, PushId);
                        reference.child("Chats").child(receiverRoom).child("messages").child(PushId).setValue(model);
                        try {
                            db.collection("OnScreenUser").document(uid + receiverUid).set(model1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("OnScreenUser").document(receiverUid + uid).set(model2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!checkOnline) {
                                                sendNotification(message);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {

                        }

                    }
                });
                messageToSend.setText("");
            }
        });


        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_to_send.show();
            }
        });

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1011);
            }
        });

        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1012);
            }
        });

        cameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1013);
            }
        });

    }

    private void scrollToBottom() {
        manager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAppStatus() {
        reference.child("OnAppStatus").child(receiverUid).child("active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.getValue(String.class);
                if (Objects.equals(check, "true")) {
                    user_status.setText("online");
                    checkOnline = true;
                } else {
                    user_status.setText("offline");
                    checkOnline = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onTypingStatus() {
        String combined = uid + receiverUid;
        reference.child("OnAppTypingStatus").child(combined).child("isTyping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.getValue(String.class);
                if (Objects.equals(check, "true")) {
                    typing_status.setText("~  typing...");
                } else {
                    typing_status.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1011 && resultCode == RESULT_OK) {
            imgUri = data.getData();
            imageView1.setImageURI(imgUri);
            select_to_send.dismiss();
            send_img_dialog.show();
        } else if (requestCode == 1012 && resultCode == RESULT_OK) {
            imgUri = data.getData();
            videoView.setVideoURI(imgUri);
            videoView.pause();
            select_to_send.dismiss();
            send_video_dialog.show();
        } else if (requestCode == 1013 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), photo, "val", null);
            imgUri = Uri.parse(path);
            Glide.with(this).load(imgUri).into(imageView1);
            select_to_send.dismiss();
            send_img_dialog.show();
        }
    }

    public void imageToUri(Uri uri) {
        Date d = new Date();
        String times = d.getHours() + ":" + d.getMinutes();
        StorageReference fileRef = reference2.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        chatModel model = new chatModel(chatModel.IMAGE_LAYOUT, times, uri.toString(), uid);
                        MainScreenModel model1 = new MainScreenModel(oppositeUri, oppositeUserName, oppositeNum, oppositeAbout, receiverUid, uid, displayName, otherFcm);
                        MainScreenModel model2 = new MainScreenModel(ourUri, username, userNumber, ourAbout, uid, receiverUid, ourDisplay, fcmToken);
                        reference.child("Chats").child(senderRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child("Chats").child(receiverRoom).child("messages").push().setValue(model);
                                db.collection("OnScreenUser").document(uid + receiverUid).set(model1);
                                db.collection("OnScreenUser").document(receiverUid + uid).set(model2);
                                if (!checkOnline) {
                                    sendNotification("Send image by " + username);
                                }
                            }
                        });
                        send_img_dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(chat_screen.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        send_img_dialog.dismiss();
                    }
                });
            }
        });

    }

    public void videoToDatabase(Uri uri) {
//        uploadingBar = new ProgressBar(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Date d = new Date();
        String times = d.getHours() + ":" + d.getMinutes();
        StorageReference fileRef = reference3.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        chatModel model = new chatModel(uid, chatModel.VIDEO_LAYOUT, times, uri.toString());
                        MainScreenModel model1 = new MainScreenModel(oppositeUri, oppositeUserName, oppositeNum, oppositeAbout, receiverUid, uid, displayName, otherFcm);
                        MainScreenModel model2 = new MainScreenModel(ourUri, username, userNumber, ourAbout, uid, receiverUid, ourDisplay, fcmToken);
                        reference.child("Chats").child(senderRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child("Chats").child(receiverRoom).child("messages").push().setValue(model);
                                db.collection("OnScreenUser").document(uid + receiverUid).set(model1);
                                db.collection("OnScreenUser").document(receiverUid + uid).set(model2);
                                progressDialog.dismiss();
                                if (!checkOnline) {
                                    sendNotification("Send video by " + username);
                                }
                            }
                        });
                        send_video_dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(chat_screen.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        send_video_dialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float per = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Send : " + (int) per + "%");
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

    private void startRecordingAudio() {
        isRecording = true;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(getRecordFilePath());
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.prepare();
                    Toast.makeText(chat_screen.this, "Recording Started!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seconds = 0;
                        runTimer();
                    }
                });
            }
        });
    }

    private void stopRecordingAudio() {
        audioUpload();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        Toast.makeText(chat_screen.this, "Stopped", Toast.LENGTH_SHORT).show();
    }

    public void runTimer() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
//                Log.d("RT", time);
                recording_timer_text.setText(time);
                if (isRecording) {
                    seconds++;
                }
                if (!isRecording) {
                    seconds = 0;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private String getRecordFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.getExternalStorageState());
        File file = new File(musicDirectory, "/audioRecorder" + "." + "mp3");
        return file.getPath();
    }

    private void audioUpload() {
        Date d = new Date();
        String times = d.getHours() + ":" + d.getMinutes();
        Uri uri = Uri.fromFile(new File(getRecordFilePath()));
        StorageReference fileRef = reference4.child(System.currentTimeMillis() + ".mp3");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri1) {
                        chatModel model = new chatModel(uid, times, chatModel.AUDIO_LAYOUT, String.valueOf(uri1));
                        MainScreenModel model1 = new MainScreenModel(oppositeUri, oppositeUserName, oppositeNum, oppositeAbout, receiverUid, uid, displayName, otherFcm);
                        MainScreenModel model2 = new MainScreenModel(ourUri, username, userNumber, ourAbout, uid, receiverUid, ourDisplay, fcmToken);
                        reference.child("Chats").child(senderRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child("Chats").child(receiverRoom).child("messages").push().setValue(model);
                                db.collection("OnScreenUser").document(uid + receiverUid).set(model1);
                                db.collection("OnScreenUser").document(receiverUid + uid).set(model2);
                                if (!checkOnline) {
                                    sendNotification("Send audio by " + username);
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chat_screen.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendNotification(String message) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(otherFcm, username, message, this, chat_screen.this);
        notificationsSender.SendNotifications();
    }
}