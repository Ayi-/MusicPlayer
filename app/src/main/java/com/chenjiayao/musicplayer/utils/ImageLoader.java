package com.chenjiayao.musicplayer.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.chenjiayao.musicplayer.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by chen on 2015/12/14.
 * 异步图片加载类,用到DiskLruCache和ImageUtils
 */
public class ImageLoader {

    private static final int MESSAGE_POST_RESULT = 1;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    private static final long KEEP_ALIVE = 10L;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int TAG_URL = R.id.imageloader_url;


    private boolean isDiskCacheCreated = false;


    //用来创建线程的工厂类
    private static final ThreadFactory factory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "imageloader#" + count);
        }
    };

    //创建线程池
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), factory);

    private Context context;
    private ImageUtils imageUtils = new ImageUtils();

    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache diskLruCache;

    private static Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;

            ImageView imageView = result.imageView;
//            imageView.setImageBitmap(result.bitmap);

            String url = (String) imageView.getTag(TAG_URL);
            if (url.equals(result.url)) {
                imageView.setImageBitmap(result.bitmap);
            }
        }
    };


    private ImageLoader(Context context) {
        this.context = context.getApplicationContext();

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            //计算每一个缓存对象的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };


        //硬盘缓存
        File diskCacheDir = getDiskCacheDir(context, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir();
        }

        if (getUsableSpace(diskCacheDir) >= DISK_CACHE_SIZE) {
            try {
                diskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                isDiskCacheCreated = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return memoryCache.get(key);
    }


    public void bindBitmap(final String uri, final ImageView imageView, final int reqWidth,
                           final int reqHeight) {
        imageView.setTag(TAG_URL, uri);

        //在内存缓存中寻找
        Bitmap bitmap = loadBitmapFromCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //没有找到那么就去硬盘缓存,或者网络下载
        //即使硬盘缓存中也要放入线程中
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //加载图片
                Bitmap bt = loadBitmap(uri, reqWidth, reqHeight);
                if (bt != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bt);
                    mainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        //放到线程池中
        THREAD_POOL_EXECUTOR.execute(runnable);
    }


    /**
     * 获取可用缓存空间
     *
     * @param diskCacheDir
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File diskCacheDir) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return diskCacheDir.getUsableSpace();
        }
        StatFs fs = new StatFs(diskCacheDir.getPath());
        return (long) fs.getBlockSize() * (long) fs.getAvailableBlocks();
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @param name
     * @return
     */
    private File getDiskCacheDir(Context context, String name) {
        boolean externalStorageAvailable = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + name);
    }

    /**
     * @param bytes
     * @return
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (1 == hex.length()) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //url ---> MD5
    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (Exception e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    /**
     * 依次从内存,硬盘获取缓存,没有就去网上下载
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromCache(uri);
        if (bitmap != null) {
            return bitmap;
        }

        //去硬盘获取缓存
        bitmap = loadBitmapFromDisk(uri, reqWidth, reqHeight);
        if (bitmap != null) {
            return bitmap;
        }

        bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);

        if (null == bitmap && !isDiskCacheCreated) {
            bitmap = downloadBitmapFromUrl(uri);
        }
        return bitmap;
    }

    private Bitmap downloadBitmapFromUrl(String uriString) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(uriString);
            conn = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    /**
     * 从网络下面下载图片,然后放到硬盘缓存中
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromHttp(String uri, int reqWidth, int reqHeight) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new RuntimeException("can not visit network from UI Thread");
        }
        if (diskLruCache == null) {
            return null;
        }

        String key = hashKeyFormUrl(uri);
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (downloadToStream(uri, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                diskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //最后下载下来之后,我们从硬盘缓存中拿
        return loadBitmapFromDisk(uri, reqWidth, reqHeight);
    }

    /**
     * 从uri里面下载到outputStream里面
     *
     * @param uriString
     * @param outputStream
     * @return
     */
    private boolean downloadToStream(String uriString, OutputStream outputStream) {
        HttpURLConnection conn = null;
        BufferedOutputStream os = null;
        BufferedInputStream is = null;

        try {
            URL url = new URL(uriString);
            conn = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
            os = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从硬盘获取缓存,顺便把这个图片放入内存缓存中
     *
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromDisk(String uri, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e("TAG", "don't do it in UI Thread");
        }

        if (null == diskLruCache) {
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFormUrl(uri);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                FileInputStream fs = (FileInputStream) snapshot.getInputStream(0);
                FileDescriptor fd = fs.getFD();
                bitmap = imageUtils.decodeSampleBitmapFromFd(fd, reqWidth, reqHeight);
                if (bitmap != null) {
                    addBitmapToCache(key, bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 从内存缓存获取
     *
     * @param uri
     * @return
     */
    private Bitmap loadBitmapFromCache(String uri) {
        String key = hashKeyFormUrl(uri);
        Bitmap bitmap = getBitmapFromCache(key);
        return bitmap;
    }

    private static class LoaderResult {
        public ImageView imageView;
        public String url;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String url, Bitmap bitmap) {
            this.imageView = imageView;
            this.url = url;
            this.bitmap = bitmap;
        }
    }
}

