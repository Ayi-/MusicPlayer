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
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chenjiayao.musicplayer.constant;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;

/**
 * Created by chen on 2015/12/24.
 */
public class MusicPlayer extends Service implements MediaPlayer.OnCompletionListener {


    private MyBindler binder;
    private Handler handler;
    private MediaPlayer player;
    private ProgressReceiver progressReceiver;
    private PlayList list;
    private SongInfo currentInfo;
    private KillReceiver killReceiver;
    private MusicPlayer.phoneReceiver phoneReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        startPlayService(list.getCurrentSong());
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    //发送广播,更新进度条
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setAction(constant.FILTER + ".service.progress");
            intent.putExtra("progress", player.getCurrentPosition());
            list.setCurrentPos(player.getCurrentPosition());
            sendBroadcast(intent);
            handler.postDelayed(this, 1000);
        }
    };

    void startPlayService(SongInfo info) {

        if (currentInfo != null) {
            if (list.isPlaying() && !currentInfo.getSongName().equals(info.getSongName())) {
                player.reset();
            }
        }
        try {
            handler.postDelayed(runnable, 1000);
            currentInfo = info;
            player.setDataSource(info.getPath());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
        player.reset();
        binder = new PlayBinder();
        list = PlayList.getInstance(getApplicationContext());

        player.setOnCompletionListener(this);

        //接收广播
        progressReceiver = new ProgressReceiver();
        IntentFilter filter = new IntentFilter(constant.FILTER + ".progress.activity");
        registerReceiver(progressReceiver, filter);

        killReceiver = new KillReceiver();
        IntentFilter killFilter = new IntentFilter("com.chenjiayao.musicplayer.kill");
        registerReceiver(killReceiver, killFilter);

        phoneReceiver = new phoneReceiver();
        IntentFilter phoneFilter = new IntentFilter();
        phoneFilter.addAction("android.intent.action.PHONE_STATE");
        phoneFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(phoneReceiver, phoneFilter);

    }


    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
            handler.removeCallbacks(runnable);
            unregisterReceiver(progressReceiver);
            unregisterReceiver(killReceiver);
            unregisterReceiver(phoneReceiver);
        }
        stopSelf();
        super.onDestroy();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        binder.next();
        Intent intent = new Intent();
        intent.setAction("com.chenjiayao.musicplayer.next");
        sendBroadcast(intent);
    }


    ///////////////////////////////////
    class PlayBinder extends MyBindler {

        @Override
        public void next() {
            player.reset();
            SongInfo info = list.getNext();
            currentInfo = info;
            startPlayService(currentInfo);
        }

        @Override
        public void previous() {
            player.reset();
            SongInfo info = list.getPrevious();
            currentInfo = info;
            startPlayService(currentInfo);
        }


        @Override
        public void pause() {
            player.pause();
            list.setCurrentPos(player.getCurrentPosition());
            list.setIsPlaying(false);
        }

        @Override
        public void contiune() {
            player.seekTo(list.getCurrentPos());
            player.start();
            list.setIsPlaying(true);

            handler.postDelayed(runnable, 0);
        }

        @Override
        public void startPlay() {
            startPlayService(list.getCurrentSong());
        }
    }


    //////////////////////////////////////////////////
    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int progress = intent.getIntExtra("progress", 0);
            player.seekTo(progress);
            list.setCurrentPos(progress);
        }
    }

    class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MusicPlayer.this.onDestroy();
        }
    }

    class phoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
