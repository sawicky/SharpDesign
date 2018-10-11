package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.Button;

public class Greyscale {
    private static Bitmap mBitmap;
    public Greyscale(Bitmap bitmap){
        this.mBitmap = bitmap;
    }

    public static Bitmap BitmapGreyscale(Bitmap bitmap, int strength) {
        final float RED = 0.3f * ((float) 0.01 * strength);
        final float GREEN = 0.59f * ((float) 0.01 * strength);
        final float BLUE = 0.11f * ((float) 0.01 * strength);
        final float[] inputMatrix = new float[] {
                RED, GREEN, BLUE, 0, 0,
                RED, GREEN, BLUE, 0, 0,
                RED, GREEN, BLUE, 0, 0,
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
