package com.mad.sharpdesign.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * A helper class to correct an image rotation. When taking images with a Camera, images may be rotated due to the camera taking landscape pictures nativel
 */
public class ImageRotate {
    private String mPath;
    private Bitmap mBitmap;
    private ExifInterface mEi;
    private int mOrientation;

    public ImageRotate(String path, Bitmap bitmap) {
        this.mPath = path;
        this.mBitmap = bitmap;
        try {
            mEi = new ExifInterface(mPath);
            mOrientation = mEi.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap = null;
            switch (mOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(mBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(mBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(mBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = mBitmap;
            }
            mBitmap = rotatedBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    public Bitmap getRotatedBitmap() {
        return mBitmap;
    }
}
