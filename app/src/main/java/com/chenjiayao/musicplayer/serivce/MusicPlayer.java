package com.chenjiayao.musicplayer.serivce;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.constant;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.ui.PlayActivity;
import com.chenjiayao.musicplayer.utils.ToastUtils;

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
    private NotificationManager nm;
    private WindowManager wm;
    private TextView view;
    private MusicPlayer.earPhoneReceiver earPhoneReceiver;


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

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.layout_remote);
        remoteViews.setTextViewText(R.id.song_name, info.getSongName());

        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_album_black_24dp)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setContent(remoteViews)
                .setTicker("正在播放")
                .setContentIntent(pi);

        nm.notify(0, builder.build());


    }


    @Override
    public void onCreate() {
        handler = new Handler();
        player = new MediaPlayer();
        binder = new PlayBinder();
        list = PlayList.getInstance(getApplicationContext());

        player.setOnCompletionListener(this);
        player.reset();

        //接收广播
        progressReceiver = new ProgressReceiver();
        IntentFilter filter = new IntentFilter(constant.FILTER + ".progress.activity");
        registerReceiver(progressReceiver, filter);

        killReceiver = new KillReceiver();
        IntentFilter killFilter = new IntentFilter("com.chenjiayao.musicplayer.kill");
        registerReceiver(killReceiver, killFilter);


        earPhoneReceiver earPhoneReceiver = new earPhoneReceiver();
        IntentFilter earPhoneFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        registerReceiver(earPhoneReceiver, earPhoneFilter);

    }


    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
            handler.removeCallbacks(runnable);
            unregisterReceiver(progressReceiver);
            unregisterReceiver(killReceiver);
        }
        nm.cancel(0);
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


    class earPhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        if (list.isPlaying()) {
                            MusicPlayer.this.binder.pause();
                        }
                        break;
                    case 1:
                        break;
                    default:
                }
            }
        }
    }


}
