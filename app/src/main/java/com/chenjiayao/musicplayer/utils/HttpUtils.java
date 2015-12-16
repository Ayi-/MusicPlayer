package com.chenjiayao.musicplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by chen on 2015/12/16.
 */
public class HttpUtils {
    public static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * 用于获取歌词的url
     *
     * @param url
     * @param handlerInterface
     */
    public static void getJson(String url, ResponseHandlerInterface handlerInterface) {
        client.get(url, handlerInterface);
    }

    public static void getImage(String url, ResponseHandlerInterface handlerInterface) {
        client.get(url, handlerInterface);
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager service = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = service.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }
}
