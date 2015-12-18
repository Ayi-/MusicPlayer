package com.chenjiayao.musicplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.chenjiayao.musicplayer.model.songInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/18.
 */
public class BaseFragment extends Fragment {

    List<songInfo> infos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infos = new ArrayList<>();
        infos = DataSupport.findAll(songInfo.class);
    }
}
