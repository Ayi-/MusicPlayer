package com.chenjiayao.musicplayer.serivce;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.chenjiayao.musicplayer.adapter.AlbumAdapter;
import com.chenjiayao.musicplayer.constant;
import com.chenjiayao.musicplayer.model.PlayList;

import java.io.IOException;

/**
 * Created by chen on 2015/12/24.
 */
public class MusicPlayer extends Service {


    private MyBindler binder;
    private Handler handler;
    private MediaPlayer player;
    private ProgressReceiver progressReceiver;
    private PlayList list;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startPlayService();
        return super.onStartCommand(intent, flags, startId);
    }

    void startPlayService() {
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(list.getCurrentSong().getPath());
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        handler = new Handler();
        player = new MediaPlayer();

        binder = new PlayBinder();

        list = PlayList.getInstance(getApplicationContext());

        //接收广播
        progressReceiver = new ProgressReceiver();
        IntentFilter filter = new IntentFilter(constant.FILTER + ".progress.activity");
        registerReceiver(progressReceiver, filter);

        //发送广播,更新进度条
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent();
                intent.setAction(constant.FILTER + ".service.progress");
                intent.putExtra("progress", player.getCurrentPosition());
                sendBroadcast(intent);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(progressReceiver);
    }

    ///////////////////////////////////
    class PlayBinder extends MyBindler {

        @Override
        public void startPlay() {
            startPlayService();
        }

        @Override
        public void next() {

        }

        @Override
        public void previous() {

        }
    }


    //////////////////////////////////////////////////
    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            int progress = intent.getIntExtra("progress", 0);
//            player.seekTo(progress);
        }
    }

}
