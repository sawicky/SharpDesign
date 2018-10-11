package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.mad.sharpdesign.ScriptC_gamma;

/**
 * Gamma manipulation using a custom RS kernel. Doesn't work very well as Gamma is complicated.
 */
public class Gamma {
    public static Bitmap gamma(Context context, Bitmap bitmap, int strength) {
        float max = 1.0f;
        float min = 0.0f;
        float floatStrength = (float)((max-min) * (strength / 100.0) + min);
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);
        ScriptC_gamma gamma = new ScriptC_gamma(script);
        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        gamma.set_gammaValue(floatStrength);
        gamma.forEach_gamma(input, output);
        output.copyTo(newBitmap);
        return newBitmap;
    }
}
