package com.chenjiayao.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by chen on 2015/12/19.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    List<AlbumInfo> albumInfos;
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    Uri artistUri = Uri.parse("content://media/external/audio/albumart");


    public interface onItemClickListener {
        void onItemClick(int pos, View view);
    }

    public onItemClickListener listener;

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }


    public AlbumAdapter(List<AlbumInfo> albumInfos, Context context) {
        this.albumInfos = albumInfos;
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_album, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AlbumInfo albumInfo = albumInfos.get(position);
        holder.albumName.setText(albumInfo.getName());
        Uri uri = ContentUris.withAppendedId(artistUri, albumInfo.getAlbumId());
        imageLoader.displayImage(String.valueOf(uri), holder.album);

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return albumInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView albumName;
        ImageView album;

        public MyViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            album = (ImageView) itemView.findViewById(R.id.album_picture);
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
