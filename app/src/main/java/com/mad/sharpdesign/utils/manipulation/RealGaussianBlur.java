package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * RenderScript implementation using inbuilt RenderScript blur function to produce a real, fast gaussian blur
 */

public class RealGaussianBlur {

    public static Bitmap gaussianBlur(Bitmap bitmap, Context context, int strength) {
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript mScript = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(mScript, newBitmap);
        final Allocation output = Allocation.createTyped(mScript, input.getType());
        final ScriptIntrinsicBlur mBlur = ScriptIntrinsicBlur.create(mScript, Element.U8_4(mScript));
        mBlur.setRadius((float) strength);
        mBlur.setInput(input);
        mBlur.forEach(output);
        output.copyTo(newBitmap);
        return newBitmap;
    }
}
