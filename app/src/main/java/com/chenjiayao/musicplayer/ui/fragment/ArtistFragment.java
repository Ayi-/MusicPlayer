package com.chenjiayao.musicplayer.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.ArtistAdapter;
import com.chenjiayao.musicplayer.model.artistInfo;
import com.chenjiayao.musicplayer.model.songInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class ArtistFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<artistInfo> artistInfos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistInfos = DataSupport.findAll(artistInfo.class);


        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_artist, null, true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        ArtistAdapter adapter = new ArtistAdapter(getActivity(), artistInfos);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }
}
