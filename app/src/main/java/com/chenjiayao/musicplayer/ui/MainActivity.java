package com.chenjiayao.musicplayer.ui;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenjiayao.musicplayer.R;
import com.chenjiayao.musicplayer.adapter.ViewPagerAdapter;
import com.chenjiayao.musicplayer.model.PlayList;
import com.chenjiayao.musicplayer.model.SongInfo;
import com.chenjiayao.musicplayer.utils.SharePreferenceUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private UIReceiver uiReceiver;

    private ImageView conver;
    private TextView songName;
    TextView artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setViewPager();
        setDrawLayout();

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        songName = (TextView) headerView.findViewById(R.id.song);
        artist = (TextView) headerView.findViewById(R.id.artist);
        conver = (ImageView) headerView.findViewById(R.id.conver);

        SharePreferenceUtils utils = SharePreferenceUtils.getInstance(MainActivity.this);

        if (utils.isFirstTimeUse()) {
            utils.setNotFirst();
        }

        uiReceiver = new UIReceiver();
        IntentFilter filter = new IntentFilter("com.chenjiayao.musicplayer.ui");
        registerReceiver(uiReceiver, filter);
    }


    private void setViewPager() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setDrawLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_playing:
                break;
            case R.id.nav_song_list:
                break;
            case R.id.nav_about:
                break;
            case R.id.action_settings:
                break;
            case R.id.nav_share:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    long[] mHits = new long[2];

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();

            if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                Intent intent = new Intent();
                intent.setAction("com.chenjiayao.musicplayer.kill");
                sendBroadcast(intent);
                finish();
            } else {

                Snackbar.make(viewPager, "再次点击关闭音乐退出", Snackbar.LENGTH_SHORT)
                        .setDuration(1000).show();
            }
        }
        return true;
    }


    class UIReceiver extends BroadcastReceiver {

        ImageLoader imageLoader = ImageLoader.getInstance();
        Uri artistUri = Uri.parse("content://media/external/audio/albumart");

        @Override
        public void onReceive(Context context, Intent intent) {
            PlayList list = PlayList.getInstance(MainActivity.this);
            SongInfo songInfo = list.getCurrentSong();
            artist.setText(songInfo.getArtistName());
            songName.setText(songInfo.getSongName());
            Uri uri = ContentUris.withAppendedId(artistUri, songInfo.getAlbumId());
            imageLoader.displayImage(String.valueOf(uri), conver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uiReceiver);
    }
}
