//package com.chenjiayao.musicplayer.utils;
//
//import android.os.Environment;
//import android.util.Log;
//
//import com.chenjiayao.musicplayer.model.gecimi.GeCiMiLyric;
//import com.google.gson.Gson;
//import com.loopj.android.http.BinaryHttpResponseHandler;
//import com.loopj.android.http.TextHttpResponseHandler;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
//import cz.msebera.android.httpclient.Header;
//
///**
// * Created by chen on 2015/12/24.
// */
//public class LyricUtils {
//    private void mkdir() {
//        String path;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            path = PlayActivity.this.getExternalCacheDir().getPath();
//        } else {
//            path = PlayActivity.this.getCacheDir().getPath();
//        }
//        //存在在包目录下的cache/lyric目录下
//        lyricDir = new File(path + File.separator + "lyric");
//        if (!lyricDir.exists()) {
//            lyricDir.mkdir();
//        }
//    }
//
//
//    private void loadLyric(String songName, String artistName) {
//        //先在本地找,找不到再到网络下载.
//        if (loadFromDisk(songName, artistName)) {
//
//        }
//        loadFromNet(songName, artistName);
//    }
//
//    /**
//     * 从本地加载歌词
//     *
//     * @param songName
//     * @param artistName
//     * @return
//     */
//    private boolean loadFromDisk(String songName, String artistName) {
//        return false;
//    }
//
//    //网络下载歌词
//    private void loadFromNet(final String songName, String artistName) {
//        String url = "http://geci.me/api/lyric/" + songName + artistName;
//        HttpUtils.getJson(url,
//                new TextHttpResponseHandler() {
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        loadFromNet(songName, "");
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        parseJson(responseString);
//                    }
//                });
//    }
//
//    /**
//     * 解析json数据
//     *
//     * @param responseString
//     */
//    private void parseJson(String responseString) {
//        Gson gson = new Gson();
//        GeCiMiLyric lyric = gson.fromJson(responseString, GeCiMiLyric.class);
//
//        //说明这样子的查询并没有获取到歌词,那么试试通过歌名再次搜索
//        if (lyric.getCount() == 0 && !again) {
//            loadFromNet(currentSong.getSongName(), "");
//            again = true;
//        } else {
//            again = false;
//
//            if (lyric.getCount() == 0) {
//                //说明并没有下载到歌词
//            } else {
//                //有歌词,下载
//                lyric = gson.fromJson(responseString, GeCiMiLyric.class);
//                String url = lyric.getResult().get(0).getLrc();
//
//                downloadLyric(url);
//
//            }
//        }
//    }
//
//    /**
//     * 根据url下载文件
//     *
//     * @param url
//     */
//    private void downloadLyric(String url) {
//        HttpUtils.getImage(url, new BinaryHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
//                Log.i("TAG", "" + statusCode);
//                try {
//                    FileOutputStream fos = new FileOutputStream(lyricDir + File.separator + currentSong.getSongName() + "+" + currentSong.getArtistName());
//                    fos.write(binaryData);
//                    fos.flush();
//                    fos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
//
//            }
//        });
//    }
//}
