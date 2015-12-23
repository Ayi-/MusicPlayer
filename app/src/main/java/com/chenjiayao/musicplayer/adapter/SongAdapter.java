package com.chenjiayao.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by chen on 2015/12/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<SongInfo> list;
    Uri artistUri = Uri.parse("content://media/external/audio/albumart");
    ImageLoader imageLoader = ImageLoader.getInstance();


    public interface onItemClickListener {
        void onItemClick(int pos, View view);
    }

    public onItemClickListener listener;

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public SongAdapter(Context context, List<SongInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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

        SongInfo songInfo = list.get(position);
        Uri uri = ContentUris.withAppendedId(artistUri, songInfo.getAlbumId());
        imageLoader.displayImage(String.valueOf(uri), holder.songIcon);
        holder.itemView.setTag(position);
        holder.songName.setText(songInfo.getSongName());
        holder.singer.setText(songInfo.getArtistName());

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
