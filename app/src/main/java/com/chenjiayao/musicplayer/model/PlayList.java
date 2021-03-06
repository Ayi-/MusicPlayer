package com.chenjiayao.musicplayer.model;


import android.content.Context;
import android.util.Log;

import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by chen on 2015/12/24.
 */
public class PlayList {

    public List<SongInfo> songInfos;
    int total;
    int current;
    Context context;
    public static PlayList lists;
    SharePreferenceUtils utils;
    boolean isPlaying = true;
    int currentPos;


    public PlayList(Context context) {
        this.context = context;
        utils = SharePreferenceUtils.getInstance(context);
        songInfos = new ArrayList<>();
    }


    public static PlayList getInstance(Context context) {
        if (lists == null) {
            synchronized (PlayList.class) {
                lists = new PlayList(context);
            }
        }
        return lists;
    }


    public List<SongInfo> getSongInfos() {
        return songInfos;
    }

    public void setSongInfos(List<SongInfo> songInfos) {
        this.songInfos = songInfos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void addToList(List<SongInfo> infos, int pos) {
        clearList();
        songInfos.addAll(infos);
        total = infos.size();
        current = pos;
    }

    private void clearList() {
        songInfos.clear();
        total = 0;
        current = -1;
    }


    public SongInfo getNext() {
        int mode = utils.getPlayMode();
        switch (mode) {
            case 0:   //顺序播放
                if (current == total - 1) {
                    current = 0;
                } else {
                    current += 1;
                }
                break;
            case 1:  //随机播放
                Random random = new Random(System.currentTimeMillis());
                current = random.nextInt(total);
                break;
            case 2:
                //重新播放
                break;
        }
        return songInfos.get(current);
    }

    public SongInfo getPrevious() {
        int mode = utils.getPlayMode();
        switch (mode) {
            case 0:   //顺序播放
                if (current == 0) {
                    current = total - 1;
                } else {
                    current -= 1;
                }
                break;
            case 1:  //随机播放
                Random random = new Random(System.currentTimeMillis());
                current = random.nextInt(total);
                break;
            case 2:
                //重新播放
                break;
        }
        return songInfos.get(current);
    }

    public SongInfo getCurrentSong() {
        return songInfos.get(current);
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

}
