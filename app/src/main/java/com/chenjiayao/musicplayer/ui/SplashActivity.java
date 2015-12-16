package com.chenjiayao.musicplayer.ui;

import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.songInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SplashActivity extends AppCompatActivity {

    private List<songInfo> infos;
    RelativeLayout layout;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        layout = (RelativeLayout) findViewById(R.id.start_layout);
        textView = (TextView) findViewById(R.id.start_text);

        layout.animate()
                .scaleY(1.4f).scaleX(1.4f).
                setDuration(3000)
                .setListener(new MyAnimateListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).start();

        searchSongs();
    }

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

                    if (duration > 60) {
                        songInfo info = new songInfo();
                        info.setAlbumName(album);
                        info.setArtist(artist);
                        info.setPlayTime(duration);
                        info.setFilePath(songPath);
                        info.setSongName(songName);
                        info.save();
                    }
                }
            }
        }).start();
    }
}
