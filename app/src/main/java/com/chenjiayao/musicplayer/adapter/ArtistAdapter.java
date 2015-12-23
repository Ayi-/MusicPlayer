package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.ArtistInfo;
import com.chenjiayao.musicplayer.model.SongInfo;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ArtistInfo> list;


    public interface onItemClickListener {
        void onItemClick(int pos, View view);
    }

    public onItemClickListener listener;

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public ArtistAdapter(Context context, List<ArtistInfo> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_artist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ArtistInfo artistInfo = list.get(position);
        holder.itemView.setTag(position);
        holder.artistName.setText(artistInfo.getName());
        holder.artistIcon.setText(String.valueOf(artistInfo.getName().charAt(0)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView artistName;
        TextView artistIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
            artistIcon = (TextView) itemView.findViewById(R.id.artist_icon);
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
