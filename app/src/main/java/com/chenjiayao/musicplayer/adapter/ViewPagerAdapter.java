package com.chenjiayao.musicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chenjiayao.musicplayer.ui.fragment.AlbumFragment;
import com.chenjiayao.musicplayer.ui.fragment.ArtistFragment;
import com.chenjiayao.musicplayer.ui.fragment.SongFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> lists = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        lists.add(new SongFragment());
        lists.add(new AlbumFragment());
        lists.add(new ArtistFragment());
        titles.add("歌曲");
        titles.add("专辑");
        titles.add("歌手");
    }

    @Override
    public Fragment getItem(int position) {
        return lists.get(position);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
