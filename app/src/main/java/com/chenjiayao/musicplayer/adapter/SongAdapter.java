package com.chenjiayao.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.utils.ItemTouchHelperAdapter;
import com.chenjiayao.musicplayer.utils.ItemTouchHelperViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<SongInfo> list;
    Uri artistUri = Uri.parse("content://media/external/audio/albumart");
    ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    public void onItemDismiss(final int position) {


        //删除数据库 删除本地音乐


        new AsyncTask<SongInfo, Void, Void>() {
            @Override
            protected Void doInBackground(SongInfo... params) {
                Log.i("TAG", params[0].getPath());
                Log.i("TAG", params[0].getSongName());
                DataSupport.deleteAll(SongInfo.class, "songid = ?", String.valueOf(params[0].getSongId()));

                return null;
            }

        }.execute(list.get(position));
        list.remove(position);
        notifyItemRemoved(position);
    }


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

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {

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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
