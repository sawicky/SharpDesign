package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Rotate {
    public static Bitmap rotate(Context context, Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return newBitmap;
    }
}
