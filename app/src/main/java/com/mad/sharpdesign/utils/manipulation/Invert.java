package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptC;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;


import com.mad.sharpdesign.ScriptC_invert;

/**
 * Renderscript implementation of an invert manipulation, that uses a custom RS kernel. Original raw implementation is included below for study/reflection purposes, but it was too slow.
 */
public class Invert {


    public static Bitmap invert(Context context, Bitmap bitmap) {
        // Raw implementation - Slow
//        Bitmap out = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//        int R,G,B,A,pixel;
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                pixel = bitmap.getPixel(x,y);
//                R = 255 - Color.red(pixel);
//                G = 255 - Color.green(pixel);
//                B = 255 - Color.blue(pixel);
//                out.setPixel(x,y,Color.argb(Color.alpha(pixel), R,G,B));
//            }
//        }
//        return out;

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);

        //Apply inversion using our own custom RenderScript kernel.
        ScriptC_invert invert = new ScriptC_invert(script);

        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        invert.forEach_invert(input, output);
        output.copyTo(newBitmap);
        return newBitmap;

    }
}
