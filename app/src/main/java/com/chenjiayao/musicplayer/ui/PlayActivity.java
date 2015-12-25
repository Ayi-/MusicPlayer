package com.chenjiayao.musicplayer.ui;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.constant;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.serivce.MusicPlayer;
import com.chenjiayao.musicplayer.serivce.MyBindler;
import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;
import com.chenjiayao.musicplayer.widgets.CircleImageView;
import com.chenjiayao.musicplayer.widgets.CircularSeekBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


/**
 * Created by chen on 2015/12/19.
 */
public class PlayActivity extends AppCompatActivity implements View.OnClickListener, CircularSeekBar.OnCircularSeekBarChangeListener {

    FrameLayout lyricLayout;
    CircleImageView artistConver;
    CircularSeekBar seekBar;
    ImageView timer;
    ImageView shuffle;
    ImageView next;
    ImageView previous;
    FloatingActionButton fab;
    LinearLayout layout;
    Toolbar toolbar;


    PlayList list;
    SongInfo currentSong;

    ProgressReceiver progressReceiver;


    SharePreferenceUtils utils = SharePreferenceUtils.getInstance(PlayActivity.this);

    Uri artistUri = Uri.parse("content://media/external/audio/albumart");
    ImageLoader imageLoader = ImageLoader.getInstance();
    private MyBindler binder;

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MyBindler) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private NextReceiver nextReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        init();


        list = PlayList.getInstance(PlayActivity.this);
        list.setIsPlaying(true);
        fab.setImageResource(R.mipmap.ic_pause_white_36dp);

        PlayList list = PlayList.getInstance(PlayActivity.this);
        setImage(list.getCurrentSong());

        bindService();
        loadLyric();


    }

    //加载歌词
    private void loadLyric() {

    }

    private void bindService() {

        //服务如果不存在就开启,不然就直接绑定
        if (!isWorked(MusicPlayer.class.getName())) {
            Intent startIntent = new Intent(this, MusicPlayer.class);
            startIntent.setFlags(Service.START_NOT_STICKY);
            startService(startIntent);
            Log.i("TAG", "start..............");
        }

        Intent bindIntent = new Intent(this, MusicPlayer.class);
        bindService(bindIntent, con, 0);
    }

    boolean isWorked(String className) {
        ActivityManager manager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        boolean res = false;
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(50);
        if (services.size() <= 0) {
            res = false;
        }

        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).service.getClassName().equals(className)) {
                res = true;

                break;
            }
        }
        return res;
    }


    private void init() {
        layout = (LinearLayout) findViewById(R.id.layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        previous = (ImageView) findViewById(R.id.previous);
        next = (ImageView) findViewById(R.id.next);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        timer = (ImageView) findViewById(R.id.timer);
        lyricLayout = (FrameLayout) findViewById(R.id.lyric_layout);
        artistConver = (CircleImageView) findViewById(R.id.album_art);

        layout.setOnClickListener(this);
        fab.setOnClickListener(this);
        previous.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        next.setOnClickListener(this);
        timer.setOnClickListener(this);
        lyricLayout.setOnClickListener(this);
        artistConver.setOnClickListener(this);
        seekBar = (CircularSeekBar) findViewById(R.id.song_progress_circular);
        seekBar.setOnSeekBarChangeListener(this);

        progressReceiver = new ProgressReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(constant.FILTER + ".service.progress");
        registerReceiver(progressReceiver, filter);

        nextReceiver = new NextReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.chenjiayao.musicplayer.next");
        registerReceiver(nextReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:    //暂停或者播放
                control();
                break;
            case R.id.next:   //下一首
                nextUI();
                break;
            case R.id.lyric_layout:   //从歌词界面切换到文艺界面
                lyricLayout.setVisibility(View.INVISIBLE);
                artistConver.setVisibility(View.VISIBLE);
                break;
            case R.id.previous:     //上一首
                previousUI();
                break;
            case R.id.timer:      //定时退出
                showDialog();
                break;
            case R.id.shuffle:    //单曲循环, 随机播放, 顺序播放
                showShuffle();
                break;
            case R.id.album_art:  //从文艺界面切换到歌词界面
                lyricLayout.setVisibility(View.VISIBLE);
                artistConver.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void previousUI() {
        binder.previous();
        SongInfo info = list.getCurrentSong();
        setImage(info);
        seekBar.setMax(currentSong.getDuration());
    }


    private void control() {
        if (list.isPlaying()) {
            binder.pause();
            fab.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
        } else {
            fab.setImageResource(R.mipmap.ic_pause_white_36dp);
            binder.contiune();
        }
    }


    private void nextUI() {
        binder.next();
        SongInfo info = list.getCurrentSong();
        setImage(info);
        seekBar.setMax(currentSong.getDuration());
    }


    private void setImage(SongInfo song) {

        toolbar.setTitle(song.getSongName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentSong = song;
        seekBar.setMax(currentSong.getDuration());

        Uri uri = ContentUris.withAppendedId(artistUri, song.getAlbumId());
        imageLoader.displayImage(String.valueOf(uri), artistConver, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int color = palette.getVibrantColor(Color.parseColor("#66000000"));
                        layout.setBackgroundColor(color);

                        color = palette.getLightMutedColor(Color.parseColor("#66000000"));
                        lyricLayout.setBackgroundColor(color);
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


    /**
     * 0 为重复播放
     * 1 为随机播放
     * 2 单曲循环
     */
    private void showShuffle() {
        int mode = utils.getPlayMode();
        switch (mode) {
            case 0:
                shuffle.setImageResource(R.mipmap.ic_shuffle_white_36dp);
                mode += 1;
                break;
            case 1:
                shuffle.setImageResource(R.mipmap.ic_repeat_one_white_36dp);
                mode += 1;
                break;
            case 2:
                shuffle.setImageResource(R.mipmap.ic_repeat_white_36dp);
                mode = 0;
                break;
        }
        utils.setPlayMode(mode);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("多久之后退出");
        final String[] items = {"10分钟", "20分钟", "40分钟", "60分钟", "算了随便听"};

        utils = SharePreferenceUtils.getInstance(PlayActivity.this);
        int item = -1;
        switch (utils.getLeaveTime()) {
            case 10:
                item = 0;
                break;
            case 20:
                item = 1;
                break;
            case 40:
                item = 2;
                break;
            case 60:
                item = 3;
                break;
            case -1:
                item = 4;
                break;
            default:
                item = 4;
                break;
        }

        builder.setSingleChoiceItems(items, item
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int time = 0;
                switch (which) {
                    case 0:
                        time = 10;
                        break;
                    case 1:
                        time = 20;
                        break;
                    case 2:
                        time = 40;
                        break;
                    case 3:
                        time = 60;
                        break;
                    case 4:
                        time = -1;
                        break;

                }
                utils.setTimeToLeft(time);
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(progressReceiver);
        unregisterReceiver(nextReceiver);
        unbindService(con);
        super.onDestroy();
    }


    //////////////////////////////////////////////////////////
    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        //发送广播通知改变进度
        Intent intent = new Intent();
        intent.setAction(constant.FILTER + ".progress.activity");
        intent.putExtra("progress", seekBar.getProgress());
        sendBroadcast(intent);
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {
    }

    ///////////////////////////////////////////////////////
    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            seekBar.setProgress(progress);

        }

    }


    /////////////////////////////////////////////////////

    public class NextReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PlayActivity.this.nextUI();
        }
    }
}

