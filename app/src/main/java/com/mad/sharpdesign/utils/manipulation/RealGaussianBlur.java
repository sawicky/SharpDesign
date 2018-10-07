package com.mad.sharpdesign.utils.manipulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RealGaussianBlur {

    public static Bitmap gaussianBlur(Bitmap bitmap, Context context) {
        Bitmap newBitmap;
        RenderScript mScript = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(mScript, bitmap);
        final Allocation output = Allocation.createTyped(mScript, input.getType());
        final ScriptIntrinsicBlur mBlur = ScriptIntrinsicBlur.create(mScript, Element.U8_4(mScript));
        mBlur.setRadius(25f);
        mBlur.setInput(input);
        mBlur.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }
}
