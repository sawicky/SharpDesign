package com.mad.sharpdesign.utils.manipulation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class ConvolutionMatrix {
    public static final int MATRIX_SIZE = 3;
    public double[][] mMatrix;
    public double mFactor = 1;
    public double mOffset = 1;

    public ConvolutionMatrix(int size) {
        mMatrix = new double[size][size];
    }
    private static int cap(int color) {
        if (color > 255) {
            return 255;
        } else if (color < 0) {
            return 0;
        } else {
            return color;
        }
    }
    public void setAll(double value) {
        for (int x = 0; x < MATRIX_SIZE; ++x) {
            for (int y = 0; y < MATRIX_SIZE; ++y) {
                mMatrix[x][y] = value;
            }
        }
    }
    public void applyConfig(double[][] config) {
        for (int x = 0; x < MATRIX_SIZE; ++x) {
            for (int y = 0; y < MATRIX_SIZE; ++y) {
                mMatrix[x][y] = config[x][y];
            }
        }
    }
    public static Bitmap convolution(Bitmap bitmap, Matrix matrix,float factor, int offset) {
        float[] newMatrix = new float[MATRIX_SIZE * MATRIX_SIZE];
        matrix.getValues(newMatrix);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int [] pixelMap = new int[width * height];
        bitmap.getPixels(pixelMap,0,width,0,0,width,height);
        int [] mutablePixelMap = pixelMap.clone();
        int R,G,B;
        int rVal, gVal, bVal;
        int i, p;
        float matrixValue;
        for (int x = 0, w = width-MATRIX_SIZE+1; x<w; ++x) {
            for (int y = 0, h =height-MATRIX_SIZE+1; y<h; ++x) {
                i = (x+1) + (y+1) * width;
                rVal = gVal = bVal = 0;
                for (int matrixX = 0; matrixX < MATRIX_SIZE; ++matrixX) {
                    for (int matrixY = 0; matrixY < MATRIX_SIZE; ++matrixY) {
                        p = pixelMap[(x + matrixX) + (y+matrixY) * width];
                        matrixValue = newMatrix[matrixX + matrixY * MATRIX_SIZE];

                        rVal += (Color.red(p) * matrixValue);
                        gVal += (Color.green(p) * matrixValue);
                        bVal += (Color.blue(p) * matrixValue);
                    }
                }
                R = cap((int)(rVal / factor + offset));
                G = cap((int)(gVal / factor + offset));
                B = cap((int)(bVal / factor + offset));
                mutablePixelMap[i] = Color.argb(Color.alpha(pixelMap[i]), R, G, B);

            }
        }
        return Bitmap.createBitmap(mutablePixelMap, width, height, bitmap.getConfig());

    }
}
