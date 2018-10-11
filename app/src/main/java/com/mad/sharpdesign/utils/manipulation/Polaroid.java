package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class Polaroid {

    public static Bitmap polaroid(Bitmap bitmap, int r, int g, int b) {
        final float[] inputMatrix = new float[] {
                1.438f, -0.062f, -0.062f, 0, 0,
                -0.122f, 1.378f, -0.122f, 0, 0,
                -0.016f, -0.016f, 1.438f, 0, 0,
                -0.03f, 0.05f, -0.02f, 1, 0,};
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(inputMatrix);
        Paint paint = new Paint();
        Bitmap mNewBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(mNewBitmap);
        paint.setColorFilter(filter);
        c.drawBitmap(mNewBitmap, 0, 0, paint);
        return mNewBitmap;
    }
}
