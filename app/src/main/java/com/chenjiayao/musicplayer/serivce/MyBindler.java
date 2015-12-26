package com.chenjiayao.musicplayer.serivce;

import android.os.Binder;

/**
 * Created by chen on 2015/12/25.
 */
public abstract class MyBindler extends Binder {


    public abstract void next();
    public abstract void previous();

    public abstract void pause();

    public abstract void contiune();

    public abstract void startPlay();
}
