package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
/**
 * A NDK implementation of using a ColorMatrix filter to apply a commonly found matrix onto a bitmap to produce a tinted effect.
 */
public class Sepia {

    public static Bitmap sepia(Bitmap bitmap) {
        final float[] inputMatrix = new float[] {
                0.393f, 0.769f, 0.189f, 0, 0,
                0.349f, 0.686f, 0.168f, 0, 0,
                0.272f, 0.534f, 0.131f, 0, 0,
                0, 0, 0, 1, 0,};
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(inputMatrix);
        Paint paint = new Paint();
        Bitmap mNewBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(mNewBitmap);
        paint.setColorFilter(filter);
        c.drawBitmap(mNewBitmap, 0, 0, paint);
        return mNewBitmap;
    }
}
