package com.chenjiayao.musicplayer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.AlbumAdapter;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.chenjiayao.musicplayer.model.songInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * Created by chen on 2015/12/16.
 */
public class AlbumFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<AlbumInfo> infos = new ArrayList<>();
    private GridLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_album, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView();
    }

    private void setRecyclerView() {
        infos = DataSupport.findAll(AlbumInfo.class);
        AlbumAdapter adapter = new AlbumAdapter(getActivity(), infos);

        recyclerView.setAdapter(adapter);

        manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.setListener(new AlbumAdapter.onClickListener() {
            @Override
            public void onClick(int pos, View v) {

            }
        });
    }
}
