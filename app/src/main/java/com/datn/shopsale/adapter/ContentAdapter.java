package com.datn.shopsale.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Product;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private ArrayList<Product> items;
    private Context context;
    private SimpleExoPlayer exoPlayer;
    private boolean isPlaying = false;

    public ContentAdapter(ArrayList<Product> items, Context context) {
        this.items = items;
        this.context = context;

        exoPlayer = new SimpleExoPlayer.Builder(context).build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product contentItem = items.get(position);

        if (contentItem.getVideo() != null) {
            holder.playerView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);

            exoPlayer.setMediaItem(MediaItem.fromUri(contentItem.getVideo()));
            exoPlayer.prepare();
            holder.playerView.setPlayer(exoPlayer);
            holder.playerView.requestFocus();

            // Sử dụng nút tùy chỉnh để điều khiển phát/dừng
            holder.lnlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        exoPlayer.setPlayWhenReady(false); // Dừng phát
                        isPlaying = false;
                        holder.imagePlay.setImageResource(R.drawable.ic_exo_play);
                        holder.imagePlay.setVisibility(View.VISIBLE);
                    } else {
                        exoPlayer.setPlayWhenReady(true); // Tiếp tục phát
                        isPlaying = true;
                        holder.imagePlay.setVisibility(View.INVISIBLE);
                    }
                }
            });
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_ENDED) {
                        // Xử lý sự kiện khi video đã hoàn thành
                        // Ở đây, bạn có thể thực hiện các thao tác cần thiết,
                        // chẳng hạn như đặt lại vị trí video về đầu và tái phát.
                        exoPlayer.seekTo(0); // Đặt lại vị trí video về đầu
                        exoPlayer.play(); // Tự động phát lại video
                    }
                }
            });

        } else if (contentItem.getList_img() != null && !contentItem.getList_img().isEmpty()) {
            holder.playerView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);

            Picasso.get().load(contentItem.getList_img().get(0)) // Hiển thị hình ảnh đầu tiên
                    .into(holder.imageView);
        }
    }


    public void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imagePlay;
        CustomPlayerView playerView;
        LinearLayout lnlVideo;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playerView = itemView.findViewById(R.id.videoView);
            imagePlay = (ImageView) itemView.findViewById(R.id.img_play);
            lnlVideo = (LinearLayout) itemView.findViewById(R.id.lnl_video);

        }
    }
}
