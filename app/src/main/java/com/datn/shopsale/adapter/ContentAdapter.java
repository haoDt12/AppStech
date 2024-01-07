package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.VideoDetailActivity;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private final ArrayList<Product> items;
    private final Context context;
    private final SimpleExoPlayer exoPlayer;
    private boolean isPlaying = false;

    public ContentAdapter(ArrayList<Product> items, Context context) {
        this.items = items;
        this.context = context;

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true);

        // Sử dụng DataSource.Factory này để tạo ExoPlayer
        exoPlayer = new SimpleExoPlayer.Builder(context)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory))
                .build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product contentItem = items.get(position);

        if (contentItem.getVideo() != null) {
            holder.playerView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);

            exoPlayer.setMediaItem(MediaItem.fromUri(contentItem.getVideo()));
            exoPlayer.prepare();
            holder.playerView.setPlayer(exoPlayer);
            holder.playerView.requestFocus();

            // Sử dụng nút tùy chỉnh để điều khiển phát/dừng
            holder.lnlVideo.setOnClickListener(v -> {
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
            Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(contentItem.getList_img().get(0))).into(holder.imageView);
        }
        holder.imgFull.setOnClickListener(v -> {
            // Gọi phương thức để xử lý sự kiện bấm vào imgFull
            onImgFullClick(position);
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imagePlay,imgFull;
        PlayerView playerView;
        LinearLayout lnlVideo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playerView = itemView.findViewById(R.id.videoView);
            imagePlay = (ImageView) itemView.findViewById(R.id.img_play);
            lnlVideo = (LinearLayout) itemView.findViewById(R.id.lnl_video);
            imgFull = (ImageView) itemView.findViewById(R.id.img_full);
        }
    }
    private void onImgFullClick(int position) {
        // Lấy ra sản phẩm tại vị trí được bấm
        Product clickedProduct = items.get(position);

        // Kiểm tra xem sản phẩm có video không
        if (clickedProduct.getVideo() != null) {
            // Nếu có video, chuyển sang VideoDetailActivity và truyền video URL
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra("video_url", clickedProduct.getVideo());
            context.startActivity(intent);
        }
    }
}
