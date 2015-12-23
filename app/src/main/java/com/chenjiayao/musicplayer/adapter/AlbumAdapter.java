package com.chenjiayao.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AlbumInfo albumInfo = albumInfos.get(position);
        holder.albumName.setText(albumInfo.getName());
        Uri uri = ContentUris.withAppendedId(artistUri, albumInfo.getAlbumId());
        imageLoader.displayImage(String.valueOf(uri), holder.album,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int color = palette.getVibrantColor(Color.parseColor("#66000000"));
                                holder.layout.setBackgroundColor(color);
//                                albumInfo.setColor(color);
//                                albumInfo.save();
                            }
                        });
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return albumInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView albumName;
        ImageView album;
        LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            album = (ImageView) itemView.findViewById(R.id.album_picture);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
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
