package com.chenjiayao.musicplayer.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.ui.QuickSearchView;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chen on 2015/12/16.
 */
public class SongFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private LinearLayoutManager manager;
    private QuickSearchView searchView;
    List<SongInfo> songInfos;
    int lastPos = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    //先与onCreateView调用
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songInfos = new ArrayList<>();
        songInfos = DataSupport.findAll(SongInfo.class, true);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_song, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchView = (QuickSearchView) view.findViewById(R.id.indicator);

        setRecyclerView();

        searchView.setListener(new QuickSearchView.onTouchListener() {
            @Override
            public void onTouch(String s) {

            }
        });
    }

    private void setRecyclerView() {
        adapter = new SongAdapter(getActivity(), songInfos);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        adapter.setListener(new SongAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                lastPos = pos;
                beginPlay(pos);
            }
        });
    }

    private void beginPlay(int pos) {

    }
}
