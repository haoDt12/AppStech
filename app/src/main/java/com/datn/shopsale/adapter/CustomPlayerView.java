package com.datn.shopsale.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class CustomPlayerView extends PlayerView {
    private SimpleExoPlayer exoPlayer;

    public CustomPlayerView(Context context) {
        super(context);
        initialize();
    }

    public CustomPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setUseController(false);
    }
}
