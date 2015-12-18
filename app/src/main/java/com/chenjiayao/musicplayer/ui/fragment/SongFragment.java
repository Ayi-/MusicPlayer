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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.adapter.SongAdapter.onItemClickListener;
import com.chenjiayao.musicplayer.model.songInfo;
import com.chenjiayao.musicplayer.ui.QuickSearchView;
import com.chenjiayao.musicplayer.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterOutputStream;

/**
 * Created by chen on 2015/12/16.
 */
public class SongFragment extends BaseFragment {

    private View view;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private LinearLayoutManager manager;
    private QuickSearchView searchView;

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
        searchView = (QuickSearchView) view.findViewById(R.id.indicator);

        setRecyclerView();

        searchView.setListener(new QuickSearchView.onTouchListener() {
            @Override
            public void onTouch(String s) {
                for (int i = 0; i < infos.size(); i++) {
                    String name = infos.get(i).getPinYin();
                    if (s.equals(name.substring(0, 1))) {
                        ToastUtils.showToast(getActivity(), s);
                        manager.scrollToPosition(i);
                        break;
                    }
                }
            }
        });
    }

    private void setRecyclerView() {
        adapter = new SongAdapter(getActivity(), infos);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.setListener(new onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                Log.i("TAG", infos.get(pos).getSongName());
            }
        });
    }

}
