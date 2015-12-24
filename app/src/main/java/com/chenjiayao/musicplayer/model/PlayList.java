package com.chenjiayao.musicplayer.model;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by chen on 2015/12/24.
 */
public class PlayList {

    public List<SongInfo> playLists;
    int total;
    int current;
    Context context;
    public static PlayList lists;

    SharePreferenceUtils utils;

    boolean isPlaying;

    public PlayList(Context context) {
        this.context = context;
        utils = SharePreferenceUtils.getInstance(context);
    }


    public static PlayList getInstance(Context context) {
        if (lists == null) {
            synchronized (PlayList.class) {
                lists = new PlayList(context);
            }
        }
        return lists;
    }


    public void addToList(List<SongInfo> infos, int pos) {
        clearList();
        playLists = infos;
        total = infos.size();
        current = pos;

        for (SongInfo info :
                infos) {
            Log.i("TAG", info.getSongName());
        }
    }

    private void clearList() {
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
        Log.i("TAG", "" + current);
        return playLists.get(current);
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
        return playLists.get(current);
    }

    public SongInfo getcurrentSong() {
        return playLists.get(current);
    }


}
