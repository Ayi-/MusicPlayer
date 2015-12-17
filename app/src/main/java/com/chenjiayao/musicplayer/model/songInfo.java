package com.chenjiayao.musicplayer.model;

import org.litepal.crud.DataSupport;

/**
 * Created by chen on 2015/12/16.
 * 根据歌名和演唱者可以获得GeCiMiLyric的内容
 * 其中count,List<ResultEntity>中的aid 和 lrc有用
 */
public class songInfo extends DataSupport {

    /**
     * 主键
     */
    int id;

    /**
     * 歌曲名称
     */
    String songName;
    /**
     * 演唱者
     */
    String artist;
    /**
     * 文件路径,播放时候使用
     */
    String filePath;
    /**
     * 演唱时长
     */
    int playTime;

    String playTimeStr;
    /**
     * 专辑名称
     */
    String albumName;

    /**
     * 为了从歌词迷API获取封面专辑所用id
     * 对应文档中的aid
     */
    int albumId;

    /**
     * 每首歌的拼音(中文),
     */
    String PinYin;

    private LyricInfo lyricInfo;
    private AlbumInfo albumInfo;


    public String getPlayTimeStr() {
        return playTimeStr;
    }

    public void setPlayTimeStr(String playTimeStr) {
        this.playTimeStr = playTimeStr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPinYin() {
        return PinYin;
    }

    public void setPinYin(String pinYin) {
        PinYin = pinYin;
    }

    public LyricInfo getLyricInfo() {
        return lyricInfo;
    }

    public void setLyricInfo(LyricInfo lyricInfo) {
        this.lyricInfo = lyricInfo;
    }

    public AlbumInfo getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(AlbumInfo albumInfo) {
        this.albumInfo = albumInfo;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    //下面暂时没用
    //int songId;

//    public int getSongId() {
//        return songId;
//    }
//
//    public void setSongId(int songId) {
//        this.songId = songId;
//    }
}
