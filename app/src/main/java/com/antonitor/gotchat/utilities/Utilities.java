package com.antonitor.gotchat.utilities;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Utilities {

    public static byte[] bitmapByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return baos.toByteArray();
    }

}

