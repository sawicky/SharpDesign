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

    public static Bitmap BitmapGreyscale(Bitmap bitmap) {
        final float RED = 0.3f;
        final float GREEN = 0.59f;
        final float BLUE = 0.11f;
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
//        ColorMatrix matrix = new ColorMatrix();
//        matrix.setSaturation(0);
//
//        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//        int R,G,B,A,pixel;
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        for(int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                pixel = bitmap.getPixel(x, y);
//                R = Color.red(pixel);
//                G = Color.green(pixel);
//                B = Color.blue(pixel);
//                A = Color.alpha(pixel);
//                R = (int) (RED * R + GREEN * G + BLUE * B);
//                G = B = R;
//                out.setPixel(x, y, Color.argb(A,R,G,B));
//            }
//        }
        return mNewBitmap;
    }

    private static class imageManipulation extends AsyncTask<Bitmap, Void, Bitmap> {
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Paint mPaint;
        public imageManipulation(Bitmap bitmap, Canvas canvas, Paint paint){
            this.mBitmap = bitmap;
            this.mCanvas = canvas;
            this.mPaint = paint;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            setGreyscale(bitmap);
        }
    }
    private static void setGreyscale(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
