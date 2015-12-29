package com.chenjiayao.musicplayer.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.constant;
import com.chenjiayao.musicplayer.model.LrcContent;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.serivce.MusicPlayer;
import com.chenjiayao.musicplayer.serivce.MyBindler;
import com.chenjiayao.musicplayer.utils.FastBlurUtils;
import com.chenjiayao.musicplayer.utils.LrcProcess;
import com.chenjiayao.musicplayer.utils.LyricUtils;
import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;
import com.chenjiayao.musicplayer.utils.ToastUtils;
import com.chenjiayao.musicplayer.widgets.CircleImageView;
import com.chenjiayao.musicplayer.widgets.CircularSeekBar;
import com.chenjiayao.musicplayer.widgets.LrcView;
import com.chenjiayao.musicplayer.widgets.MyAnimateListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Collections;
import java.util.List;


/**
 * Created by chen on 2015/12/19.
 */
public class PlayActivity extends AppCompatActivity implements View.OnClickListener, CircularSeekBar.OnCircularSeekBarChangeListener {

    private static final String TAG = "PlayActivity";
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

    float rotate = 0.0f;

    PlayList list;
    SongInfo currentSong;
    ProgressReceiver progressReceiver;
    private MyBindler binder;
    private NextReceiver nextReceiver;
    private Uri artistUri;
    private ImageLoader imageLoader;

    LrcView lrcView;

    LrcProcess process;

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MyBindler) service;
            binder.startPlay();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private SharePreferenceUtils utils;
    private List<LrcContent> lrcList;
    private PhoneReceiver phoneReceiver;
    private Handler handler;
    private WindowManager wm;
    private TextView textView;
    private PlayActivity.earPhoneReceiver earPhoneReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        init();

        artistUri = Uri.parse("content://media/external/audio/albumart");
        imageLoader = ImageLoader.getInstance();
        list = PlayList.getInstance(PlayActivity.this);

        utils = SharePreferenceUtils.getInstance(PlayActivity.this);

        bindService();
        setImage(list.getCurrentSong());

        loadLyric(currentSong);

        list.setIsPlaying(true);
        fab.setImageResource(R.mipmap.ic_pause_white_36dp);
    }


    private void loadLyric(SongInfo info) {
        new LoadLrc().execute(info);
    }

    private void bindService() {

        //服务如果不存在就开启,不然就直接绑定
        if (!isWorked(MusicPlayer.class.getName())) {
            Intent startIntent = new Intent(this, MusicPlayer.class);
            startIntent.setFlags(Service.START_NOT_STICKY);
            startService(startIntent);
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
        handler = new Handler();

        previous = (ImageView) findViewById(R.id.previous);
        next = (ImageView) findViewById(R.id.next);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        timer = (ImageView) findViewById(R.id.timer);
        lyricLayout = (FrameLayout) findViewById(R.id.lyric_layout);
        artistConver = (CircleImageView) findViewById(R.id.album_art);
        lrcView = (LrcView) findViewById(R.id.lrc_view);


        process = new LrcProcess();

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

        phoneReceiver = new PhoneReceiver();
        IntentFilter phoneFilter = new IntentFilter();
        phoneFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(phoneReceiver, phoneFilter);


        earPhoneReceiver = new earPhoneReceiver();
        IntentFilter earPhoneFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        registerReceiver(earPhoneReceiver, earPhoneFilter);

        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.alpha = 0.0f;

        View view = LayoutInflater.from(PlayActivity.this).inflate(R.layout.layout_launcher, null, false);
        textView = (TextView) view.findViewById(R.id.tv_lrc);
        wm.addView(view, params);
    }

    class LoadLrc extends AsyncTask<SongInfo, Void, List<LrcContent>> {

        @Override
        protected List<LrcContent> doInBackground(SongInfo... params) {
            String path = LyricUtils.getLrcPath(params[0], PlayActivity.this);

            if (TextUtils.isEmpty(path)) {
                return null;
            }

            process.readLrc(path);  //传入歌词文件

            lrcList = process.getContents();
            Collections.sort(lrcList);
            return lrcList;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<LrcContent> lrcContents) {

            if (lrcContents == null) {
                lrcView.setNoLrc(true);
            } else {
                lrcView.setNoLrc(false);
                lrcView.setList(lrcList);
                lrcView.setIndex(0);
                lrcView.invalidate();
            }
        }
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
                seekBar.setVisibility(View.VISIBLE);
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
                seekBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void previousUI() {
        binder.previous();
        SongInfo info = list.getCurrentSong();
        setImage(info);
        fab.setImageResource(R.mipmap.ic_pause_white_36dp);
        list.setIsPlaying(true);
        seekBar.setMax(currentSong.getDuration());
        loadLyric(info);
        lrcView.setIndex(1);
        textView.setText("");
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
        fab.setImageResource(R.mipmap.ic_pause_white_36dp);
        list.setIsPlaying(true);
        seekBar.setMax(currentSong.getDuration());

        loadLyric(info);
        lrcView.setIndex(1);
        textView.setText("");
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
            public void onLoadingComplete(final String imageUri, View view, final Bitmap loadedImage) {
                new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int color = palette.getVibrantColor(Color.parseColor("#66000000"));
                        layout.setBackgroundColor(color);

                        color = palette.getLightMutedColor(Color.parseColor("#66000000"));
                        lyricLayout.setBackgroundColor(color);

//                        RenderScript script = RenderScript.create(PlayActivity.this);
//                        Allocation allocation = Allocation.createFromBitmap(script, loadedImage);
//                        ScriptIntrinsicBlur sb = ScriptIntrinsicBlur.create(script, allocation.getElement());
//                        sb.setInput(allocation);
//                        sb.setRadius(25);
//                        sb.forEach(allocation);
//                        allocation.copyTo(loadedImage);
//
//                        lyricLayout.setBackground(new BitmapDrawable(getResources(), loadedImage));
//                        script.destroy();

//                        float scaleFactor = 1;
//                        float radius = 20;
////                        if (downScale.isChecked()) {
//                        scaleFactor = 8;
//                        radius = 2;
//                        }

//                        Bitmap overlay = Bitmap.createBitmap((int) (layout.getMeasuredWidth() / scaleFactor),
//                                (int) (layout.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(overlay);
//                        canvas.translate(-layout.getLeft() / scaleFactor, -layout.getTop() / scaleFactor);
//                        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//                        Paint paint = new Paint();
//                        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//                        canvas.drawBitmap(loadedImage, 0, 0, paint);
//
//                        overlay = FastBlurUtils.doBlur(overlay, (int) radius, true);
//                        layout.setBackground(new BitmapDrawable(getResources(), overlay));
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        Intent intent = new Intent();
        intent.setAction("com.chenjiayao.musicplayer.ui");
        sendBroadcast(intent);

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
                if (time != -1) {
                    ToastUtils.showToast(PlayActivity.this, time + "分钟之后停止播放");
                    handler.postDelayed(runnable, time * 60 * 1000);
                } else {
                    handler.removeCallbacks(runnable);
                }
                utils.setTimeToLeft(time);
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (list.isPlaying()) {
                PlayActivity.this.control();
            }
            SharePreferenceUtils.getInstance(PlayActivity.this)
                    .setTimeToLeft(-1);
        }
    };

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
        unregisterReceiver(phoneReceiver);
        unregisterReceiver(earPhoneReceiver);
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

            //应该在这里更新歌词
            if (lrcList != null) {
                if (lyricLayout.getVisibility() == View.VISIBLE && !lrcList.isEmpty() && list.isPlaying()) {
                    PlayActivity.this.lrcView.setIndex(getIndex());
                    PlayActivity.this.lrcView.invalidate();

                    textView.setText("  ");
                    Log.i("TAG", textView.getText().toString());
                    textView.setText(lrcList.get(getIndex()).getLrcStr());

                }
            }

            if (list.isPlaying()) {
            }
        }

    }

    class PhoneReceiver extends BroadcastReceiver {

        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //继续播放音乐
//                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        PlayActivity.this.control();
                        break;
                }
            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private int getIndex() {


        int currentTime = seekBar.getProgress();
        int newIndex = 1;
        for (int i = 0; i < lrcList.size(); i++) {

            int temp = lrcList.get(i).getLrcTime() - currentTime;
            if (temp > 0) {

                if (i - 1 > lrcView.getIndex()) {
                    newIndex = i - 1;
                } else {
                    newIndex = lrcView.getIndex();
                }
                break;
            }
        }
        return newIndex;
    }

    /////////////////////////////////////////////////////

    public class NextReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PlayActivity.this.nextUI();
        }
    }


    //////////////////////////////////////////
    class earPhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.i("TAg", "拔出了.....");
                        fab.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                        break;
                    case 1:
                        Log.i("TAg", "插入了 .....");
                        //这里
                        break;
                    default:
                        Log.i("TAG", "fasdfdsfds");
                }
            }
        }
    }

}

