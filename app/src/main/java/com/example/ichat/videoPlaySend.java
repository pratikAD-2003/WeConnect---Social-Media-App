package com.example.ichat;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class videoPlaySend extends AppCompatActivity {

    //    VideoView videoView;
    ExoPlayer player;
    StyledPlayerView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_send);
        videoView = findViewById(R.id.play_sent_video);
        String videoUri = getIntent().getStringExtra("uriPlayVideo");
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(videoUri);
//        videoView.setVideoURI(uri);
//        videoView.setMediaController(mediaController);
//        videoView.start();
        player = new ExoPlayer.Builder(videoPlaySend.this).build();
        videoView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(false);


    }

    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
        player.release();
        player = null;
    }
}