package com.zhixin.flappybird.server;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhixin.flappybird.R;

import java.io.IOException;

public class MyService extends Service{

    private MediaPlayer mediaPlayer;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        start();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pause();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("音乐", "mediaPlayer.onDestroy();");
        //停止播放音乐
        mediaPlayer.stop();
        //释放占用的资源
        mediaPlayer.release();
        //将player置为空
        mediaPlayer = null;
        super.onDestroy();
    }

    public void start() {
        Log.i("音乐", "mediaPlayer.start();");
        //创建音乐播放器对象
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.game);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setLooping(true);
    }

    //暂停 继续播放
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.i("音乐", "mediaPlayer.pause();");
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.i("音乐", "mediaPlayer.start();");
        }
    }

    //停止播放
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

}
