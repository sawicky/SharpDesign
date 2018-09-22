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
    private Button mButtonURL, mButtonURLCancel, mButtonURLPaste;
    private File mImageFile;
    private ImageLoader mImageLoader;
    private ImageView mImageView;
    private LinearLayout mURLLinearLayout;
    private RelativeLayout mURLRelativeLayout;
    private EditText mEditTextURL;
    private LoadPresenter mPresenter;
    private ViewGroup mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        mPresenter = new LoadPresenter(this);
        mContent = (ViewGroup) findViewById(android.R.id.content);
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.fragment_loadurl, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonURLCancel = (Button)viewInflated.findViewById(R.id.btn_cancel);
        mButtonURLPaste = (Button)viewInflated.findViewById(R.id.btn_paste);
        mEditTextURL = (EditText)viewInflated.findViewById(R.id.input_url);
        //mURLLinearLayout = (LinearLayout)viewInflated.findViewById(R.id.layout_URL);
        mURLRelativeLayout = (RelativeLayout) viewInflated.findViewById(R.id.layout_URL);
        mImageView = (ImageView) viewInflated.findViewById(R.id.imageView_main);
        builder.setView(viewInflated);
        AlertDialog loadURL = builder.create();
        mButtonURL.setOnClickListener((View v) -> {
            loadURL.show();
        });
        mButtonURLPaste.setOnClickListener((View v) -> {
            pasteClipboard();

        });

        mButtonURLCancel.setOnClickListener((View v) -> {
        });
    verifyPermissions();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
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
        String path = new String(textToPaste.toString());
        ImageView img = new ImageView(this);

        Picasso.get().load(path).transform(new HeaderImageBlur(this, 25)).into(img, new Callback() {
            @Override
            public void onSuccess() {
                mURLRelativeLayout.setBackground(img.getDrawable());
                Picasso.get().load(path).into(mImageView);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        //mImageLoader.displayImage(textToPaste.toString(), mImageView);

        mPresenter.saveImage(textToPaste.toString());
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
