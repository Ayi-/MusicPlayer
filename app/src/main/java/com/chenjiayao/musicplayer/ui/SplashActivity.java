package com.chenjiayao.musicplayer.ui;

import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.chenjiayao.musicplayer.model.songInfo;
import com.chenjiayao.musicplayer.utils.HanZi2PinYinUtils;
import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;
import com.loopj.android.http.LogHandler;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SplashActivity extends AppCompatActivity {

    private List<songInfo> infos;
    TextView startText;
    private ImageView startImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        startText = (TextView) findViewById(R.id.start_text);
        startImage = (ImageView) findViewById(R.id.start_image);
        startImage.animate()
                .scaleY(1.2f).scaleX(1.2f).
                setDuration(3000)
                .setListener(new MyAnimateListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).start();

        SharePreferenceUtils utils = SharePreferenceUtils.getInstance(SplashActivity.this);
        if (utils.isFirstTimeUse()) {
            searchSongs();
        }
    }

    /**
     * 开启一个线程扫描歌曲
     */
    private void searchSongs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                infos = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                while (cursor.moveToNext()) {

                    //专辑
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    //歌曲名称
                    String songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    //歌曲路径
                    String songPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    //演唱者
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    //播放时长
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000;
                    //歌曲id
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    //专辑id
                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    if (duration > 120) {
                        songInfo info = new songInfo();

                        //查找这个专辑编号
                        List<AlbumInfo> list = DataSupport.where("albumName = ?", album).find(AlbumInfo.class);

                        if (list.size() == 0) {
                            AlbumInfo albumInfo = new AlbumInfo();
                            albumInfo.setId(albumId);
                            albumInfo.setAlbumName(album);
                            albumInfo.setArtist(artist);
                            albumInfo.setSongId(id);
                            info.setAlbumInfo(albumInfo);
                            albumInfo.save();
                        }

                        info.setSongId(id);
                        info.setArtist(artist);
                        info.setPlayTime(duration);
                        info.setAlbumId(albumId);

                        int minute = duration / 60;
                        int second = duration % 60;

                        String time = minute + ":" + second;
                        info.setPlayTimeStr(time);

                        info.setFilePath(songPath);
                        info.setSongName(songName);
                        info.setPinYin(HanZi2PinYinUtils.HanZi2PinYin(songName));
                        info.save();
                    }
                }
            }
        }).start();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
