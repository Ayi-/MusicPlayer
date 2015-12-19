package com.chenjiayao.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.model.artistInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/18.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<artistInfo> artistInfoList = new ArrayList<>();


    public ArtistAdapter(Context context, List<artistInfo> artistInfoList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.artistInfoList = artistInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_artist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        artistInfo info = artistInfoList.get(position);

        holder.artist.setText(info.getName());
        holder.firstName.setText(String.valueOf(info.getName().charAt(0)));
    }

    @Override
    public int getItemCount() {
        return artistInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView firstName;

        public MyViewHolder(View itemView) {

            super(itemView);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            firstName = (TextView) itemView.findViewById(R.id.artist_icon);
        }
    }
}
