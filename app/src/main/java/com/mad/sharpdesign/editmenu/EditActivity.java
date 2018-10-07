package com.mad.sharpdesign.editmenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mad.sharpdesign.R;
import com.mad.sharpdesign.utils.manipulation.CheapGaussianBlur;
import com.mad.sharpdesign.utils.manipulation.IntrinsicSharpen;
import com.mad.sharpdesign.utils.manipulation.Invert;
import com.mad.sharpdesign.utils.manipulation.RealGaussianBlur;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private Uri mFilePath;
    private ImageView mMainImageView;
    private Button mGreyScale, mInvert;
    private Bitmap mBitmap;
    private static final String IMAGE_PATH_KEY = "ImagePathKey";
    private static final String IMAGE_KEY = "ImageKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        mFilePath = (Uri) intent.getExtras().get(IMAGE_PATH_KEY);
        Log.d("MAD", "Loading from: "+mFilePath);

        mMainImageView = (ImageView)findViewById(R.id.edit_imageView);
        mGreyScale = (Button)findViewById(R.id.btn_edit_greyscale);
        mInvert = (Button)findViewById(R.id.btn_edit_invert);
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Picasso.get().load(mFilePath).into(mMainImageView);
        mInvert.setOnClickListener((View v) -> {
            Bitmap newBitmap = Invert.invert(mBitmap);
            Picasso.get().load(getImageUri(EditActivity.this.getApplicationContext(), newBitmap)).into(mMainImageView);
            });
        mGreyScale.setOnClickListener((View v) -> {
            Bitmap newBitmap = IntrinsicSharpen.sharpen(getApplicationContext(), mBitmap, 2);
            Picasso.get().load(getImageUri(this, newBitmap)).into(mMainImageView);


        });
    }
        public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "temp", null);
        return Uri.parse(path);
    }

}
