package com.chenjiayao.musicplayer.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.SongInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by chen on 2015/12/19.
 */
public class ArtistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        LinearLayoutManager manager = new LinearLayoutManager(ArtistActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        List<SongInfo> songInfos;
        if (name != null) {
            songInfos = DataSupport.where("artistname = ?", name).find(SongInfo.class);

        } else {
            String album = intent.getStringExtra("album");
            songInfos = DataSupport.where("albumname = ?", album).find(SongInfo.class);
        }
        SongAdapter adapter = new SongAdapter(ArtistActivity.this, songInfos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }
}
