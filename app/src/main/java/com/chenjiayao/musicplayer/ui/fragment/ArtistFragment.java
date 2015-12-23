package com.chenjiayao.musicplayer.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.ArtistAdapter;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.ArtistInfo;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by chen on 2015/12/16.
 */
public class ArtistFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    List<ArtistInfo> artistInfos;
    private LinearLayoutManager manager;
    private ArtistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistInfos = DataSupport.findAll(ArtistInfo.class);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_artist, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        adapter = new ArtistAdapter(getActivity(), artistInfos);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        adapter.setListener(new ArtistAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                Intent intent = new Intent(getActivity(), ArtistActivity.class);
                intent.putExtra("name", artistInfos.get(pos).getName());
                startActivity(intent);
            }
        });
    }
}
