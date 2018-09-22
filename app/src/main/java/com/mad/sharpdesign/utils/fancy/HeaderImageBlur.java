package com.mad.sharpdesign.utils.fancy;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


public class HeaderImageBlur implements com.squareup.picasso.Transformation{
    private static final int UP_LIMIT = 25;
    private static final int LOW_LIMIT = 1;
    protected final Context context;
    private final int blurRadius;

    public HeaderImageBlur(Context context, int radius) {
        this.context = context;

        if (radius < LOW_LIMIT) {
            this.blurRadius = LOW_LIMIT;
        } else if (radius > UP_LIMIT) {
            this.blurRadius = UP_LIMIT;
        } else {
            this.blurRadius = radius;
        }
    }

    @Override public Bitmap transform(Bitmap source) {

        Bitmap blurredBitmap;
        blurredBitmap = Bitmap.createBitmap(source);

        RenderScript renderScript = RenderScript.create(context);

        Allocation input =
                Allocation.createFromBitmap(renderScript, source, Allocation.MipmapControl.MIPMAP_FULL,
                        Allocation.USAGE_SCRIPT);

        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script =
                ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        script.setInput(input);
        script.setRadius(blurRadius);

        script.forEach(output);
        output.copyTo(blurredBitmap);

        return blurredBitmap;
    }

    @Override public String key() {
        return "blurred";
    }
}