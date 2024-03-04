package com.example.ichat;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioPlayer extends AppCompatActivity {

    SeekBar seekBar;
    LottieAnimationView animationView;
    TextView duration;
    ImageButton play;

    MediaPlayer mediaPlayer;

    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        seekBar = findViewById(R.id.seekBar_per_person);
        animationView = findViewById(R.id.lottieAnimationView2);
        duration = findViewById(R.id.audio_duration_per_person);
        play = findViewById(R.id.play_pause_per_person);

        String uriAudio = getIntent().getStringExtra("AudioUri");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Uri uri = Uri.parse(uriAudio);
        try {
            mediaPlayer.setDataSource(String.valueOf(uri));
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        mediaPlayer.seekTo(i);
                        seekBar.setProgress(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(0);
                    animationView.setVisibility(View.INVISIBLE);
                    play.setImageResource(R.drawable.music_play_btn);
                }
            });
            String duration1 = milliSecondsToTimer(mediaPlayer.getDuration());
            duration.setText("Duration " + duration1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    isPlaying = true;
                    mediaPlayer.start();
                    animationView.setVisibility(View.VISIBLE);
                    play.setImageResource(R.drawable.music_pause_btn);
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    }, 0, 1000);
                } else {
                    mediaPlayer.pause();
                    animationView.setVisibility(View.INVISIBLE);
                    play.setImageResource(R.drawable.music_play_btn);
                    isPlaying = false;
                }
            }
        });
    }

    public String milliSecondsToTimer(long milliSeconds) {
        String finalTimerString = "";
        String secondsString = null;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        finish();
        super.onBackPressed();
    }
}