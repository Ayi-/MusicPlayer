package com.chenjiayao.musicplayer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by chen on 2015/12/14.
 */
public class ImageUtils {

    public ImageUtils() {

    }


    public Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        Log.i("TAG", "req : " + reqWidth + "   " + reqHeight);
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        int height = options.outHeight;
        int width = options.outWidth;
        Log.i("TAG", "height : " + width + "   " + height);

        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            Log.i("TAG", "req : " + reqWidth + "  " + width);
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeSampleBitmapFromFd(FileDescriptor fd, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }
}
