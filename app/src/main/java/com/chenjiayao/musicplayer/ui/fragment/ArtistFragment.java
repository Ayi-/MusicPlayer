package com.chenjiayao.musicplayer.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;

/**
 * Created by chen on 2015/12/16.
 */
public class ArtistFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_artist, null, true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }
}
