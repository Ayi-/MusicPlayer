package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.artistInfo;
import com.chenjiayao.musicplayer.model.songInfo;
import com.chenjiayao.musicplayer.utils.ImageLoader;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<songInfo> list;
    private ImageLoader imageLoader;

    public interface onItemClickListener {
        void onItemClick(int pos, View view);
    }

    public onItemClickListener listener;

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public SongAdapter(Context context, List<songInfo> list) {
        this.context = context;
        imageLoader = ImageLoader.build(context);
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
        songInfo info = list.get(position);

        holder.songName.setText(info.getSongName());
        holder.singer.setText(info.getArtistInfo().getName());
        holder.itemView.setTag(position);

        imageLoader.bindBitmap(info.getSongId(), info.getAlbumId(),
                holder.songIcon, holder.songIcon.getMeasuredWidth(),
                holder.songIcon.getMeasuredHeight());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView songName;
        TextView singer;
        ImageView songIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.song_name);
            singer = (TextView) itemView.findViewById(R.id.song_artist);
            songIcon = (ImageView) itemView.findViewById(R.id.song_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = (int) v.getTag();
                listener.onItemClick(pos, v);
            }
        }
    }
}
