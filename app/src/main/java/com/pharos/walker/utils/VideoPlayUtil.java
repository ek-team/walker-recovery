package com.pharos.walker.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pharos.walker.R;

/**
 * Created by zhanglun on 2021/4/15
 * Describe:
 */
public class VideoPlayUtil {
    /**
     * 单例模式
     */
    private static volatile VideoPlayUtil instance = null;
    private SimpleExoPlayer player;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private float currentVolume = 10;

    public VideoPlayUtil() {
    }


    public static VideoPlayUtil getInstance(){
        if (instance == null){
            synchronized (VideoPlayUtil.class){
                if (instance == null){
                    instance = new VideoPlayUtil();
                }
            }
        }
        return instance;
    }
    public void setVideoPlayer(Context context, String filePath, PlayerView playerView){
        Log.e("VideoPlayerUtil", "destroyPlayer: 视频被创建 " );
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        player.setRepeatMode(Player.REPEAT_MODE_ALL);//循环播放
        playerView.setPlayer(player);
        //  userAgent -> audio/mpeg  不能为空
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
        //创建一个媒体连接源
        concatenatingMediaSource = new ConcatenatingMediaSource();
        //创建一个播放数据源
        ExtractorMediaSource mediaSource1 = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse(filePath));
        concatenatingMediaSource.addMediaSource(mediaSource1);
        player.setPlayWhenReady(true);
        currentVolume = player.getVolume();

    }
    public void startPlayer(){
        if (player != null && concatenatingMediaSource != null){
            player.prepare(concatenatingMediaSource);
        }
    }
    public void setMute(boolean isMute){//设置视频是否静音
        if (player != null && isMute){
            player.setVolume(0);
        }else if (player != null){
            player.setVolume(currentVolume);
        }
    }
    public void stopPlayer(){
        if (player != null){
            player.stop();
        }
    }
    public void destroyPlayer(){
        if (player != null){
            setMute(false);
            player.release();
            Log.e("VideoPlayerUtil", "destroyPlayer: 视频被销毁 " );
        }
    }
}
