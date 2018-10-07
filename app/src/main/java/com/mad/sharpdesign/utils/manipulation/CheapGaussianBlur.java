package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class CheapGaussianBlur {
    public static Bitmap gaussianBlur(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createScaledBitmap(bitmap,width/16, height/16, true);

    }
}
