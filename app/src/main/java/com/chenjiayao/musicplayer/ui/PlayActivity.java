package com.chenjiayao.musicplayer.ui;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.model.gecimi.GeCiMiLyric;
import com.chenjiayao.musicplayer.utils.HttpUtils;
import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;
import com.chenjiayao.musicplayer.widgets.CircleImageView;
import com.chenjiayao.musicplayer.widgets.CircularSeekBar;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cz.msebera.android.httpclient.Header;


/**
 * Created by chen on 2015/12/19.
 */
public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

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

    SongInfo currentSong;

    boolean again = false;

    SharePreferenceUtils utils = SharePreferenceUtils.getInstance(PlayActivity.this);

    Uri artistUri = Uri.parse("content://media/external/audio/albumart");
    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        init();

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Log.i("TAG", "" + progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        PlayList list = PlayList.getInstance(PlayActivity.this);
        setImage(list.getcurrentSong());

        loadLyric(currentSong.getSongName(), "/" + currentSong.getArtistName());
    }

    private void loadLyric(String songName, String artistName) {
        //先在本地找,找不到再到网络下载.
        loadFromNet(songName, artistName);
    }

    //网络下载歌词
    private void loadFromNet(final String songName, String artistName) {
        String url = "http://geci.me/api/lyric/" + songName + artistName;
        HttpUtils.getJson(url,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        loadFromNet(songName, "");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        parseJson(responseString);
                    }
                });
    }

    /**
     * 解析json数据
     *
     * @param responseString
     */
    private void parseJson(String responseString) {
        Gson gson = new Gson();
        GeCiMiLyric lyric = gson.fromJson(responseString, GeCiMiLyric.class);

        //说明这样子的查询并没有获取到歌词,那么试试通过歌名再次搜索
        if (lyric.getCount() == 0 && !again) {
            loadFromNet(currentSong.getSongName(), "");
            again = true;
        } else {
            again = false;

            if (lyric.getCount() == 0) {
                //说明并没有下载到歌词
            } else {
                //有歌词,下载
                lyric = gson.fromJson(responseString, GeCiMiLyric.class);
                String url = lyric.getResult().get(0).getLrc();

                downloadLyric(url);

            }
        }
    }

    /**
     * 根据url下载文件
     *
     * @param url
     */
    private void downloadLyric(String url) {

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:    //暂停或者播放

                break;
            case R.id.next:   //下一首
                playNext();
                break;
            case R.id.lyric_layout:   //从歌词界面切换到文艺界面
                lyricLayout.setVisibility(View.INVISIBLE);
                artistConver.setVisibility(View.VISIBLE);
                break;
            case R.id.previous:     //上一首
                playPrevious();
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

    /**
     * 发送广播播放下一首,同时更新界面
     */
    private void playNext() {
        PlayList list = PlayList.getInstance(PlayActivity.this);
        SongInfo info = list.getNext();
        setImage(info);

    }

    private void playPrevious() {
        PlayList list = PlayList.getInstance(PlayActivity.this);
        SongInfo info = list.getPrevious();
        setImage(info);
    }

    private void setImage(SongInfo song) {

        toolbar.setTitle(song.getSongName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentSong = song;
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
                shuffle.setImageResource(R.mipmap.ic_repeat_one_white_36dp);
                mode += 1;
                break;
            case 1:
                shuffle.setImageResource(R.mipmap.ic_shuffle_white_36dp);
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
}

