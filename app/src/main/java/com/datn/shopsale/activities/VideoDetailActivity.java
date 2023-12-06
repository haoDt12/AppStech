package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.shopsale.R;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoDetailActivity extends AppCompatActivity {
    private PlayerView playerView;
    private Toolbar toolbarDetailVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        String videoUrl = GetImgIPAddress.convertLocalhostToIpAddress(getIntent().getStringExtra("video_url"));

        playerView = (PlayerView) findViewById(R.id.playerView);
        toolbarDetailVideo = (Toolbar) findViewById(R.id.toolbar_detail_video);

        SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

        // Phát video
        exoPlayer.play();
        playerView.setControllerAutoShow(false);
        playerView.setControllerShowTimeoutMs(3000);

        setSupportActionBar(toolbarDetailVideo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarDetailVideo.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        // Bắt sự kiện khi người dùng bấm nút back
    }
}