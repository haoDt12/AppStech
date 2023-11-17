package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.datn.shopsale.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoDetailActivity extends AppCompatActivity {
    private PlayerView playerView;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        String videoUrl = getIntent().getStringExtra("video_url");

        playerView = (PlayerView) findViewById(R.id.playerView);
        imgBack = (ImageView) findViewById(R.id.img_back);

        SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();

        // Phát video
        exoPlayer.play();
        playerView.setControllerAutoShow(false);
        playerView.setControllerShowTimeoutMs(3000);

        // Bắt sự kiện khi người dùng bấm nút back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}