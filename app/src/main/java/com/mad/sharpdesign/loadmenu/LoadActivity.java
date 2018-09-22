package com.mad.sharpdesign.loadmenu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.mad.sharpdesign.R;
import com.mad.sharpdesign.editmenu.EditActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoadActivity extends AppCompatActivity implements LoadContract {
    private Button mButtonURL, mButtonURLCancel, mButtonURLPaste;
    private ImageLoader mImageLoader;
    private ImageView mImageView;
    private EditText mEditTextURL;
    private LoadPresenter mPresenter;
    private ViewGroup mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        mPresenter = new LoadPresenter(this, new LoadInteractor());
        mContent = (ViewGroup) findViewById(android.R.id.content);
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.fragment_loadurl, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonURLCancel = (Button)viewInflated.findViewById(R.id.btn_cancel);
        mButtonURLPaste = (Button)viewInflated.findViewById(R.id.btn_paste);
        mEditTextURL = (EditText)viewInflated.findViewById(R.id.input_url);
        mImageView = (ImageView) viewInflated.findViewById(R.id.imageView2);
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

        try {
            URL url = new URL(textToPaste.toString());
            mImageLoader.displayImage(textToPaste.toString(), mImageView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
