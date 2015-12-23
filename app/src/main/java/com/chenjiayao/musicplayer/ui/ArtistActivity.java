package com.chenjiayao.musicplayer.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.SongAdapter;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by chen on 2015/12/19.
 */
public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView imageView;
    ImageLoader imageLoader;
    Uri artistUri = Uri.parse("content://media/external/audio/albumart");
    private Uri uri;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        imageLoader = ImageLoader.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.iv);
        fab = (FloatingActionButton) findViewById(R.id.play_button);
        fab.setOnClickListener(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        LinearLayoutManager manager = new LinearLayoutManager(ArtistActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        List<SongInfo> songInfos;
        if (name != null) {
            songInfos = DataSupport.where("artistname = ?", name).find(SongInfo.class);
            collapsingToolbarLayout.setTitle(songInfos.get(0).getArtistName());
        } else {
            String album = intent.getStringExtra("album");
            songInfos = DataSupport.where("albumname = ?", album).find(SongInfo.class);
            collapsingToolbarLayout.setTitle(songInfos.get(0).getAlbumName());
        }
        uri = ContentUris.withAppendedId(artistUri, songInfos.get(0).getAlbumId());
        imageLoader.displayImage(String.valueOf(uri), imageView);

        SongAdapter adapter = new SongAdapter(ArtistActivity.this, songInfos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        adapter.setListener(new SongAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View view) {
                Intent playIntent = new Intent(ArtistActivity.this, PlayActivity.class);
                startActivity(playIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ArtistActivity.this, PlayActivity.class);
        startActivity(intent);
    }
}
