package com.chenjiayao.musicplayer.model;

import org.litepal.crud.DataSupport;

/**
 * Created by chen on 2015/12/19.
 */
public class AlbumInfo extends DataSupport {

    int albumId;
    String name;


    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
