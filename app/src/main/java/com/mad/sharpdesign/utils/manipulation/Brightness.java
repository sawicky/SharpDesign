package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.mad.sharpdesign.ScriptC_brightness;


public class Brightness {
    public static Bitmap brightness(Context context,Bitmap bitmap, int strength) {
        float max = 1.0f;
        float min = 0.0f;
        float floatStrength = (float)((max-min) * (strength / 100.0) + min);
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript script = RenderScript.create(context);
        ScriptC_brightness brightness = new ScriptC_brightness(script);
        final Allocation input = Allocation.createFromBitmap(script, newBitmap);
        final Allocation output = Allocation.createTyped(script, input.getType());
        brightness.set_brightnessValue(floatStrength);
        brightness.forEach_brightness(input, output);
        output.copyTo(newBitmap);
        return newBitmap;

    }
}
