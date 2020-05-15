package com.antonitor.gotchat.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;

import androidx.core.app.ActivityCompat;

public class Utilities {

    private static final int THUMBSIZE = 400;

    public static byte[] bitmapByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static Bitmap getThumbnail(Bitmap image){
        return ThumbnailUtils.extractThumbnail(image, THUMBSIZE, THUMBSIZE);
    }

}

