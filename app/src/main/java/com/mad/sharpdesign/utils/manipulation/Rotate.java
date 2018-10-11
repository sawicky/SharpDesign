package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * A quick little hack to very quickly rotate an image without doing much image processing. Rather than rotating pixels and moving them around, simply rotate the Bitmap object using a matrix with nothing but an angle.
 */
public class Rotate {
    public static Bitmap rotate(Context context, Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return newBitmap;
    }
}
