package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.mad.sharpdesign.ScriptC_colour_intensity;
import com.mad.sharpdesign.ScriptC_saturation;

public class ColourIntensity {


    public static Bitmap intensify(Context context, Bitmap bitmap, int r, int g, int b) {
        float max = 1.0f;
        float min = 0.0f;
        float floatR = (float)((max-min) * (r / 100.0) + min);
        float floatG = (float)((max-min) * (g / 100.0) + min);
        float floatB = (float)((max-min) * (b / 100.0) + min);

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);
        ScriptC_colour_intensity colourIntensity = new ScriptC_colour_intensity(script);
        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        colourIntensity.set_r(floatR);
        colourIntensity.set_g(floatG);
        colourIntensity.set_b(floatB);
        colourIntensity.forEach_colour_intensity(input, output);
        output.copyTo(newBitmap);
        return newBitmap;

    }
}
