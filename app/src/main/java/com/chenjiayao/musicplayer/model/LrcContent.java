package com.chenjiayao.musicplayer.model;

/**
 * Created by chen on 2015/12/26.
 */
public class LrcContent {

    /**
     * 一行歌词
     */
    String lrcStr;

    /**
     * 当前歌词的时间
     */
    int lrcTime;

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
