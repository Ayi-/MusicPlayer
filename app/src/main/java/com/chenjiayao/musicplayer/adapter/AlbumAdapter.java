package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenjiayao.musicplayer.model.songInfo;

import java.util.List;

/**
 * Created by chen on 2015/12/18.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<songInfo> infos;


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
