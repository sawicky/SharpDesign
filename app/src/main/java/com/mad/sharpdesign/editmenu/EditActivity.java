package com.mad.sharpdesign.editmenu;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.mad.sharpdesign.R;
import com.mad.sharpdesign.editmenu.fragments.BlankFragment;
import com.mad.sharpdesign.editmenu.fragments.NegativeStrengthFragment;
import com.mad.sharpdesign.editmenu.fragments.RGBFragment;
import com.mad.sharpdesign.editmenu.fragments.StrengthFragment;
import com.mad.sharpdesign.events.ApplyEvent;
import com.mad.sharpdesign.events.RGBEvent;
import com.mad.sharpdesign.events.StrengthEvent;
import com.mad.sharpdesign.utils.manipulation.Brightness;
import com.mad.sharpdesign.utils.manipulation.CheapGaussianBlur;
import com.mad.sharpdesign.utils.manipulation.ColourIntensity;
import com.mad.sharpdesign.utils.manipulation.Emboss;
import com.mad.sharpdesign.utils.manipulation.Gamma;
import com.mad.sharpdesign.utils.manipulation.Greyscale;
import com.mad.sharpdesign.utils.manipulation.IntrinsicSharpen;
import com.mad.sharpdesign.utils.manipulation.Invert;
import com.mad.sharpdesign.utils.manipulation.Polaroid;
import com.mad.sharpdesign.utils.manipulation.RealGaussianBlur;
import com.mad.sharpdesign.utils.manipulation.Rotate;
import com.mad.sharpdesign.utils.manipulation.Saturate;
import com.mad.sharpdesign.utils.manipulation.Sepia;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity that loads the file you have selected in the loading activity from file, then apply manipulations to it.
 */
public class EditActivity extends AppCompatActivity {
    private Uri mFilePath;
    private ImageView mMainImageView;
    private Button mGreyScale, mInvert;
    private Bitmap mBitmap, mNewBitmap;
    private String mImageName;
    private Spinner mOptionSpinner;
    private int mEffectSelected, mStrengthEffect, mRGBEffect;
    private StrengthFragment mStrengthFragment;
    private BlankFragment mBlankFragment;
    private RGBFragment mRGBFragment;
    private NegativeStrengthFragment mNegativeStrengthFragment;
    private FragmentTransaction mTransaction;
    private static final String IMAGE_PATH_KEY = "ImagePathKey";
    private static final String IMAGE_NAME = "Name";

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
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mFilePath = (Uri) intent.getExtras().get(IMAGE_PATH_KEY);
        mImageName = intent.getStringExtra(IMAGE_NAME);
        Log.d("MAD", "Loading from: "+mFilePath);
        mOptionSpinner = (Spinner)findViewById(R.id.effect_spinner);
        mMainImageView = (ImageView)findViewById(R.id.edit_imageView);

        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFilePath);
            mNewBitmap = mBitmap.copy(mBitmap.getConfig(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Picasso.get().load(mFilePath).into(mMainImageView);
        mOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEffectSelected = i;
                //Change previewed bitmap back to the original.
                mMainImageView.setImageBitmap(mBitmap);
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
                        mStrengthFragment = StrengthFragment.newInstance(100);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 2;
                        break;
                    case 3://Invert
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = Invert.invert(getApplicationContext(), mBitmap);
                        mMainImageView.setImageBitmap(mNewBitmap);
                        break;
                    case 4://Polaroid
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = Polaroid.polaroid(mBitmap,0,0,0);
                        mMainImageView.setImageBitmap(mNewBitmap);
                        break;
                    case 5://Fake Blur
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = CheapGaussianBlur.gaussianBlur(mBitmap);
                        mMainImageView.setImageBitmap(mNewBitmap);
                        break;
                    case 6://Sepia
                        mBlankFragment = BlankFragment.newInstance();
                        createTransaction(mBlankFragment);
                        mNewBitmap = Sepia.sepia(mBitmap);
                        mMainImageView.setImageBitmap(mNewBitmap);
                        break;
                    case 7: //Gamma correction
                        mNegativeStrengthFragment = NegativeStrengthFragment.newInstance(100);
                        createTransaction(mNegativeStrengthFragment);
                        mStrengthEffect = 3;
                        Toast.makeText(getApplicationContext(), R.string.errorMath, Toast.LENGTH_LONG).show();

                        break;
                    case 8://Contrast
                        Toast.makeText(getApplicationContext(), R.string.errorNYI, Toast.LENGTH_LONG).show();
                        break;
                    case 9://Brightness
                        mNegativeStrengthFragment = NegativeStrengthFragment.newInstance(100);
                        createTransaction(mNegativeStrengthFragment);
                        mStrengthEffect =4;
                        break;
                    case 10://Rotate Image
                        mNegativeStrengthFragment = NegativeStrengthFragment.newInstance(180);
                        createTransaction(mNegativeStrengthFragment);
                        mStrengthEffect = 5;
                        break;
                    case 11://Saturate
                        mStrengthFragment = StrengthFragment.newInstance(255);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 6;
                        break;
                    case 12: //Colour Intensity
                        mRGBFragment = RGBFragment.newInstance(255, 255,255,  122);
                        createTransaction(mRGBFragment);
                        mRGBEffect = 0;
                        break;
                    case 13: //Emboss
                        mStrengthFragment = StrengthFragment.newInstance(100);
                        createTransaction(mStrengthFragment);
                        mStrengthEffect = 7;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /**
     * Helper functions to override create transaction so we don't have to repeat too much code.
     * @param fragment
     */
    public void createTransaction(StrengthFragment fragment){
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        mTransaction.replace(R.id.placeholder_fragment, mStrengthFragment);
        mTransaction.commit();
    }
    public void createTransaction(BlankFragment fragment) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        mTransaction.replace(R.id.placeholder_fragment, mBlankFragment);
        mTransaction.commit();
    }
    public void createTransaction(RGBFragment fragment) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        mTransaction.replace(R.id.placeholder_fragment, mRGBFragment);
        mTransaction.commit();
    }
    public void createTransaction(NegativeStrengthFragment fragment) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        mTransaction.replace(R.id.placeholder_fragment, fragment);
        mTransaction.commit();
    }

    /**
     * Whenever we change the value of a fragment's Strength or Negative strength slider, run this method.
     * @param event
     */
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
            case 3: //Gamma
                mNewBitmap = Gamma.gamma(getApplicationContext(), mBitmap, strength);
                break;

            case 4://Brightness
                mNewBitmap = Brightness.brightness(getApplicationContext(), mBitmap, strength);
                break;
            case 5://Rotate
                mNewBitmap = Rotate.rotate(getApplicationContext(), mBitmap, strength);
                break;
            case 6://Saturate
                mNewBitmap = Saturate.saturate(getApplicationContext(), mBitmap, strength);
                break;
            case 7: //Emboss
                mNewBitmap = Emboss.emboss(getApplicationContext(), mBitmap, strength);
            default:
                break;
        }
        mMainImageView.setImageBitmap(mNewBitmap);
        //Picasso.get().load(getImageUri(this, mNewBitmap)).into(mMainImageView);

    }

    /**
     * Runs when an Apply message is sent via EventBus - Whenever we click a fragment's event button
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyEvent(ApplyEvent event) {
        if (event.isApplied()) {
            mBitmap = mNewBitmap;
            Toast.makeText(getApplicationContext(), "Effect applied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Whenever we change a value of a fragment's RGB slider, run this method.
     * @param event
     */
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
        mMainImageView.setImageBitmap(mNewBitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        Drawable save = menu.getItem(0).getIcon();
        save.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.saveImage:
                mBitmap = mNewBitmap;
                Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/" + mImageName + ".jpg");
                Log.d("MAD", "Saving file into: " + file.getAbsolutePath());
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, ostream);
                    ostream.flush();
                    ostream.close();
                } catch (IOException e) {
                    Log.d("MAD","Failed");
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
