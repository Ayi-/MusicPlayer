package com.chenjiayao.musicplayer.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.AlbumAdapter;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.chenjiayao.musicplayer.ui.DetailActivity;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by chen on 2015/12/16.
 */
public class AlbumFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private List<AlbumInfo> albumInfos;
    private AlbumAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumInfos = DataSupport.findAll(AlbumInfo.class);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_album, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView();
    }

    private void setRecyclerView() {
        adapter = new AlbumAdapter(albumInfos, getActivity());
        manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        adapter.setListener(new AlbumAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("album", albumInfos.get(pos).getName());
                startActivity(intent);


            }
        });
    }
}
