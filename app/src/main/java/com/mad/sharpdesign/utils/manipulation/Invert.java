package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Invert {
    private Bitmap mBitmap;

    public static Bitmap invert(Bitmap bitmap) {
        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int R,G,B,A,pixel;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmap.getPixel(x,y);
                R = 255 - Color.red(pixel);
                G = 255 - Color.green(pixel);
                B = 255 - Color.blue(pixel);
                out.setPixel(x,y,Color.argb(Color.alpha(pixel), R,G,B));
            }
        }
        return out;
    }
}
