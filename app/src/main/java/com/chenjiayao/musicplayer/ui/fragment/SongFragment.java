package com.chenjiayao.musicplayer.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.ui.PlayActivity;
import com.chenjiayao.musicplayer.utils.ToastUtils;
import com.chenjiayao.musicplayer.utils.TouchDeleteCallback;
import com.chenjiayao.musicplayer.widgets.QuickSearchView;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    //onCreateView先调用
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long l = System.currentTimeMillis();

        songInfos = new ArrayList<>();
        songInfos = DataSupport.findAll(SongInfo.class, true);
        Collections.sort(songInfos);

        Log.i("TAG", " : " + (System.currentTimeMillis() - l));
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_song, null, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchView = (QuickSearchView) view.findViewById(R.id.indicator);

        setRecyclerView();

        //快速索引
        searchView.setListener(new QuickSearchView.onTouchListener() {
            @Override
            public void onTouch(String s) {
                for (int i = 0; i < songInfos.size(); i++) {
                    String pinyin = songInfos.get(i).getPinyin();
                    if (s.equals(pinyin.substring(0, 1))) {
                        ToastUtils.showToast(getActivity(), s);
                        //开始滚动到相应位置
                        int first = manager.findFirstVisibleItemPosition();
                        int last = manager.findLastVisibleItemPosition();

                        if (i <= first) {
                            manager.scrollToPosition(i);
                        } else if (i >= last) {
                            manager.scrollToPosition(i);
//                            int top = recyclerView.getChildAt(i - first).getTop();
//                            recyclerView.scrollBy(0, top);
                        } else {
                            int top = recyclerView.getChildAt(i - first).getTop();
                            recyclerView.scrollBy(0, top);
                        }
                        break;
                    }
                }
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

        TouchDeleteCallback callback = new TouchDeleteCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        adapter.setListener(new SongAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                startActivity(intent);
                PlayList list = PlayList.getInstance(getActivity());
                list.addToList(songInfos, pos);
            }
        });
    }

}
