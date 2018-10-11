package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.ScientificNumberFormatter;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;

/**
 * Sharpen manipulation using RenderScript's inbuilt 3x3 convolution matrix. Uses a common sharpen matrix and a modifier for the original pixel
 */
public class IntrinsicSharpen {
    public static Bitmap sharpen(Context context, Bitmap bitmap, float weight) {
        weight = weight / 10;
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        RenderScript mScript = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(mScript, newBitmap);
        final Allocation output = Allocation.createTyped(mScript, input.getType());
        final ScriptIntrinsicConvolve3x3 script = ScriptIntrinsicConvolve3x3.create(mScript, Element.U8_4(mScript));
        script.setCoefficients(new float[] {
                -weight,-weight,-weight,
                -weight, 8*weight+1, -weight,
                -weight,-weight,-weight});
        script.setInput(input);
        script.forEach(output);
        output.copyTo(newBitmap);
        mScript.destroy();
        return newBitmap;
    }
}
