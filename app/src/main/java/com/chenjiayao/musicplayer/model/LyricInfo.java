package com.chenjiayao.musicplayer.model;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class LyricInfo extends DataSupport {

    /**
     * 主键
     */
    int id;
    /**
     * 返回歌词链接数量
     */
    int count;
    /**
     * 歌词URL
     */
    List<String> LyricURLs;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getLyricURLs() {
        return LyricURLs;
    }

    public void setLyricURLs(List<String> lyricURLs) {
        LyricURLs = lyricURLs;
    }
}
