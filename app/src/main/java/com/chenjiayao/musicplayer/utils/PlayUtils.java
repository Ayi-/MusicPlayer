package com.chenjiayao.musicplayer.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;


/**
 * Created by chen on 2015/12/19.
 */
public class PlayUtils {

    public static PlayUtils playUtils;
    public static boolean isPlaying = false;

    public static PlayUtils getInstance() {
        if (playUtils == null) {
            playUtils = new PlayUtils();
        }
        return playUtils;
    }


    public static MediaPlayer player;
    int playTime;

    public static final int PLAY_ORDER = 1;
    public static final int PLAY_RANDOM = 2;
    public static final int PLAY_CIRCULATE = 3;

    public static int PLAY_WAY = PLAY_ORDER;

    public PlayUtils() {
        player = new MediaPlayer();
        this.playTime = 0;
    }

    public static void startPlay(String path) {
        if (isPlaying) {
            stopPlay();
        }
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(path);
            player.prepare();
            player.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void stopPlay() {
        isPlaying = false;
        player.reset();
        player.stop();
    }

    public static void freeResource() {
        player.release();
        player = null;
    }
}
