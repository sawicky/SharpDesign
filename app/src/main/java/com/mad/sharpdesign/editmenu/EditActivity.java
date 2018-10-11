package com.mad.sharpdesign.editmenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mad.sharpdesign.R;
import com.mad.sharpdesign.editmenu.fragments.BlankFragment;
import com.mad.sharpdesign.editmenu.fragments.RGBFragment;
import com.mad.sharpdesign.editmenu.fragments.StrengthFragment;
import com.mad.sharpdesign.events.ApplyEvent;
import com.mad.sharpdesign.events.RGBEvent;
import com.mad.sharpdesign.events.StrengthEvent;
import com.mad.sharpdesign.utils.manipulation.ColourIntensity;
import com.mad.sharpdesign.utils.manipulation.Greyscale;
import com.mad.sharpdesign.utils.manipulation.IntrinsicSharpen;
import com.mad.sharpdesign.utils.manipulation.Invert;
import com.mad.sharpdesign.utils.manipulation.Polaroid;
import com.mad.sharpdesign.utils.manipulation.RealGaussianBlur;
import com.mad.sharpdesign.utils.manipulation.Saturate;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private Uri mFilePath;
    private ImageView mMainImageView;
    private Button mGreyScale, mInvert;
    private Bitmap mBitmap, mNewBitmap;
    private Spinner mOptionSpinner;
    private int mEffectSelected, mStrengthEffect, mRGBEffect;
    private StrengthFragment mStrengthFragment;
    private BlankFragment mBlankFragment;
    private RGBFragment mRGBFragment;
    private FragmentTransaction mTransaction;
    private static final String IMAGE_PATH_KEY = "ImagePathKey";
    private static final String IMAGE_KEY = "ImageKey";

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        mFilePath = (Uri) intent.getExtras().get(IMAGE_PATH_KEY);
        Log.d("MAD", "Loading from: "+mFilePath);
        mOptionSpinner = (Spinner)findViewById(R.id.effect_spinner);
        mMainImageView = (ImageView)findViewById(R.id.edit_imageView);

        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Picasso.get().load(mFilePath).into(mMainImageView);
        mOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEffectSelected = i;
                //Change previewed bitmap back to the original.
                //Picasso.get().load(getImageUri(getApplicationContext(), mNewBitmap)).into(mMainImageView);
                switch (i){
                    case 0: //Greyscale
                        mStrengthFragment = StrengthFragment.newInstance(100);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 0;
                        break;
                    case 1: //Real Blur
                        mStrengthFragment = StrengthFragment.newInstance(25);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 1;
                        break;
                    case 2: //Sharpen
                        mStrengthFragment = StrengthFragment.newInstance(25);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 2;
                        break;
                    case 3://Invert
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = Invert.invert(getApplicationContext(), mBitmap);
                        Picasso.get().load(getImageUri(getApplicationContext(), mNewBitmap)).into(mMainImageView);
                        break;
                    case 4://Polaroid
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = Polaroid.polaroid(mBitmap,0,0,0);
                        Picasso.get().load(getImageUri(getApplicationContext(), mNewBitmap)).into(mMainImageView);
                        break;
                    case 5://Fake Blur
                        break;
                    case 6://Sepia
                        break;
                    case 11://Saturate
                        mStrengthFragment = StrengthFragment.newInstance(100);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 3;
                        break;
                    case 12: //Colour Intensity
                        mRGBFragment = RGBFragment.newInstance(255, 255,255,  122);
                        createTransaction(mRGBFragment);
                        mRGBEffect = 0;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void createTransaction(StrengthFragment fragment){
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.placeholder_fragment, mStrengthFragment);
        mTransaction.commit();
    }
    public void createTransaction(BlankFragment fragment) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.placeholder_fragment, mBlankFragment);
        mTransaction.commit();
    }
    public void createTransaction(RGBFragment fragment) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.placeholder_fragment, mRGBFragment);
        mTransaction.commit();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStrengthEvent(StrengthEvent event) {
        int strength = event.getmStrength();
        switch (mStrengthEffect) {
            case 0: //Grayscale
                mNewBitmap = Greyscale.BitmapGreyscale(mBitmap, strength);
                break;
            case 1: //Real Gaussian Blur
                if (strength > 0) {
                    mNewBitmap = RealGaussianBlur.gaussianBlur(mBitmap, getApplicationContext(), strength);
                } else {
                    mNewBitmap = RealGaussianBlur.gaussianBlur(mBitmap, getApplicationContext(), 1);
                }
                break;
            case 2://Sharpen
                mNewBitmap = IntrinsicSharpen.sharpen(getApplicationContext(), mBitmap, strength);
                break;
            case 3://Saturate
                mNewBitmap = Saturate.saturate(getApplicationContext(), mBitmap, strength);
            default:
                break;
        }
        Picasso.get().load(getImageUri(this, mNewBitmap)).into(mMainImageView);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyEvent(ApplyEvent event) {
        if (event.isApplied()) {
            mBitmap = mNewBitmap;
            Toast.makeText(getApplicationContext(), "Effect applied", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRGBEvent(RGBEvent event) {
        int r = event.getRed();
        int g = event.getGreen();
        int b = event.getBlue();
        switch (mRGBEffect) {
            case 0: //Colour intensity
                mNewBitmap = ColourIntensity.intensify(getApplicationContext(), mBitmap, r,g,b);
                break;
            default:
                break;
        }
        Picasso.get().load(getImageUri(this, mNewBitmap)).into(mMainImageView);
    }

        public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "temp", null);
        File imageFile = new File(path);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        return Uri.parse(path);
    }

}
