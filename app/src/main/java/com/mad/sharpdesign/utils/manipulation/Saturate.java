package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.mad.sharpdesign.ScriptC_invert;
import com.mad.sharpdesign.ScriptC_saturation;

/**
 * RenderScript implementation of saturation, using a custom RS kernel. Applies a strength modifier to all pixels at the same time.
 */
public class Saturate {


    public static Bitmap saturate(Context context, Bitmap bitmap, int strength) {
        float max = 2.0f;
        float min = 0.0f;
        float floatStrength = (float)((max-min) * (strength / 100.0) + min);

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);
        ScriptC_saturation saturate = new ScriptC_saturation(script);
        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        saturate.set_saturationFloat(floatStrength);
        saturate.forEach_saturation(input, output);
        output.copyTo(newBitmap);
        return newBitmap;

    }
}
