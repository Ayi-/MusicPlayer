package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.AlbumInfo;
import com.chenjiayao.musicplayer.utils.ImageLoader;

import java.util.List;

/**
 * Created by chen on 2015/12/18.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private static final String TAG = "AlbumAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<AlbumInfo> infos;
    private final ImageLoader imageLoader;

    public interface onClickListener {
        void onClick(int pos, View v);
    }

    public onClickListener listener;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public AlbumAdapter(Context context, List<AlbumInfo> infos) {
        this.context = context;
        this.infos = infos;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.build(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_album, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //这个函数里面不要太复杂!!!
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AlbumInfo info = infos.get(position);
        holder.albumName.setText(info.getAlbumName());
        imageLoader.bindBitmap(info.getSongId(), info.getId(), holder.albumPicture,
                holder.albumPicture.getMeasuredWidth(),
                holder.albumPicture.getMeasuredHeight());
        holder.albumArtist.setText(info.getArtist());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return infos.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView albumPicture;
        TextView albumName;
        TextView albumArtist;

        public MyViewHolder(View itemView) {
            super(itemView);
            albumArtist = (TextView) itemView.findViewById(R.id.album_artist);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            albumPicture = (ImageView) itemView.findViewById(R.id.album_picture);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = (int) v.getTag();
                listener.onClick(pos, v);
            }
        }
    }
}
