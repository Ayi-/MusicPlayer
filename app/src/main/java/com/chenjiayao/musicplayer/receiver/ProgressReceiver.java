package com.chenjiayao.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chen on 2015/12/25.
 */
public class ProgressReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int progress = intent.getIntExtra("progress", 0);
    }

}
