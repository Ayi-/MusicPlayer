package com.chenjiayao.musicplayer.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/18.
 */
public class artistInfo extends DataSupport {

    String name;

    int id;

    int number;

    List<songInfo> songInfoList = new ArrayList<>();


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<songInfo> getSongInfoList() {
        return songInfoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSongInfoList(List<songInfo> songInfoList) {
        this.songInfoList = songInfoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
