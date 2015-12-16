package com.chenjiayao.musicplayer.model;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class AlbumInfo {

    int id;
    int count;
    List<String> AlbumURLs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getAlbumURLs() {
        return AlbumURLs;
    }

    public void setAlbumURLs(List<String> albumURLs) {
        AlbumURLs = albumURLs;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
