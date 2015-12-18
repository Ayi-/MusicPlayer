package com.chenjiayao.musicplayer.model;

import org.litepal.crud.DataSupport;

/**
 * Created by chen on 2015/12/18.
 */
public class AlbumInfo extends DataSupport {

    private String albumName;
    private String artist;

    private int songId;

    private int albumId;

    private int rbg;

    public int getRbg() {
        return rbg;
    }

    public void setRbg(int rbg) {
        this.rbg = rbg;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
