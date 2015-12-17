package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.songInfo;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    private LayoutInflater inflater;
    private List<songInfo> list;


    interface onItemClickListener {
        void onItemClick(int pos, View view);
    }

    public onItemClickListener listener;

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public SongAdapter(Context context, List<songInfo> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_song, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.songName.setText(list.get(position).getSongName());
        holder.singer.setText(list.get(position).getArtist());
        holder.playTime.setText(list.get(position).getPlayTimeStr());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int pos = (int) v.getTag();
            listener.onItemClick(pos, v);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songName;
        TextView singer;
        ImageView songIcon;
        TextView playTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.song_name);
            singer = (TextView) itemView.findViewById(R.id.song_artist);
            songIcon = (ImageView) itemView.findViewById(R.id.song_icon);
            playTime = (TextView) itemView.findViewById(R.id.play_time);
        }
    }
}
