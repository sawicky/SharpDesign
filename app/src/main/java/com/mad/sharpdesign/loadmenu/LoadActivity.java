package com.mad.sharpdesign.loadmenu;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.mad.sharpdesign.R;
import com.mad.sharpdesign.editmenu.EditActivity;
import com.mad.sharpdesign.model.ImageRoomDatabase;
import com.mad.sharpdesign.utils.fancy.HeaderImageBlur;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadActivity extends AppCompatActivity implements LoadContract {
    private static final String[] STORAGE_PERMISSIONS = { Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Button mButtonURL, mButtonURLCancel, mButtonURLPaste, mButtonLoad, mButtonFile;
    private File mImageFile;
    private ImageLoader mImageLoader;
    private String mPath;
    private ImageView mImageView;
    private LinearLayout mURLLinearLayout;
    private RelativeLayout mURLRelativeLayout;
    private EditText mEditTextURL;
    private LoadPresenter mPresenter;
    private Target mPasteTarget, mLoadTarget;
    private ViewGroup mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageRoomDatabase db = ImageRoomDatabase.getDatabase(getApplication());
        mPresenter = new LoadPresenter(this, db.imageDao());

        mContent = (ViewGroup) findViewById(android.R.id.content);
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        mButtonFile = (Button)findViewById(R.id.btn_loadFile);
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.fragment_loadurl, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonURLCancel = (Button)viewInflated.findViewById(R.id.btn_cancel);
        mButtonURLPaste = (Button)viewInflated.findViewById(R.id.btn_paste);
        mButtonLoad = (Button)viewInflated.findViewById(R.id.btn_load);
        mEditTextURL = (EditText)viewInflated.findViewById(R.id.input_url);
        //mURLLinearLayout = (LinearLayout)viewInflated.findViewById(R.id.layout_URL);
        mURLRelativeLayout = (RelativeLayout) viewInflated.findViewById(R.id.layout_URL);
        mImageView = (ImageView) viewInflated.findViewById(R.id.imageView_main);
        builder.setView(viewInflated);
        AlertDialog loadURL = builder.create();
        mButtonFile.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
        mButtonURL.setOnClickListener((View v) -> {
            loadURL.show();
        });
        mButtonURLPaste.setOnClickListener((View v) -> {
            pasteClipboard();

        });
        mButtonLoad.setOnClickListener((View v) -> {
            loadImage();
        });
        mButtonURLCancel.setOnClickListener((View v) -> {
            loadURL.dismiss();
        });
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/");
        folder.mkdirs();
    verifyPermissions();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    public void loadImage() {

        mLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = ""+System.currentTimeMillis();
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/" + fileName + ".jpg");
                        Log.d("MAD", "Saving file into: " + file.getAbsolutePath());
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.d("MAD","Failed");
                            e.printStackTrace();
                        }2
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mPath).into(mLoadTarget);
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public void pasteClipboard() {
        mImageLoader = ImageLoader.getInstance();
        ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboardManager.getPrimaryClip();
        if (clip==null) return;
        ClipData.Item item = clip.getItemAt(0);
        if (item==null) return;
        CharSequence textToPaste = item.getText();
        if (textToPaste == null) return;
        mEditTextURL.setText(textToPaste);
        //Add some input validation to check if url is valid later
        mPath = new String(textToPaste.toString());
        ImageView img = new ImageView(this);
        Log.d("MAD","Loading image now");
        mPasteTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MAD","Bitmap was loaded");
                Picasso.get().load(mPath).into(mImageView);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mPath).into(mPasteTarget);


        Picasso.get().load(mPath).transform(new HeaderImageBlur(this, 25)).into(img, new Callback() {
            @Override
            public void onSuccess() {
                mURLRelativeLayout.setBackground(img.getDrawable());
            }

            @Override
            public void onError(Exception e) {

            }
        });
        //mImageLoader.displayImage(textToPaste.toString(), mImageView);

        //mPresenter.saveImage(textToPaste.toString());
    }

    public void verifyPermissions()
    {
        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this,STORAGE_PERMISSIONS,1);
        }
    }
}
