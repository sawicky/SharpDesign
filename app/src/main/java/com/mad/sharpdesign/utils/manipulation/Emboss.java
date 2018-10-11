package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * Emboss manipulation, uses inbuilt RenderScript Convolution Matrix (3x3) with a common emboss matrix. Multipliers applied to leading edge and trailing edge of the pixel map (upper left corner and lower right corner of a 3x3 grid)
 */
public class Emboss {
    public static Bitmap emboss(Context context, Bitmap bitmap, int strength) {
        float max = 1.0f;
        float min = 0.0f;
        float floatStrength = (float)((max-min) * (strength / 10.0) + min);
        float[] embossMatrix =  {-2f*floatStrength, -1f, 0f,
                                 -1f, 1f, 1f,
                                 0f, 1f, 2f*floatStrength};

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);
        ScriptIntrinsicConvolve3x3 embossScript = ScriptIntrinsicConvolve3x3.create(script, Element.U8_4(script));
        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        embossScript.setCoefficients(embossMatrix);
        embossScript.setInput(input);
        embossScript.forEach(output);
        output.copyTo(newBitmap);
        return newBitmap;
    }
}
