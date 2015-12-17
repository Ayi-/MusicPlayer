package com.chenjiayao.musicplayer.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.songInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SongFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    List<songInfo> infos;
    private SongAdapter adapter;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    //先与onCreateView调用
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_song, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        infos = new ArrayList<>();
        infos = DataSupport.findAll(songInfo.class);
        adapter = new SongAdapter(getActivity(), infos);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
