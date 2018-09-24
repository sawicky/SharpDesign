package com.mad.sharpdesign.loadmenu;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.sharpdesign.R;
import com.mad.sharpdesign.model.Image;
import com.mad.sharpdesign.model.ImageDao;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getSystemService;

public class LoadPresenter implements LoadContract.Presenter{
    private LoadContract mLoadContract;
    private final ImageDao mImageDao;

    LoadPresenter(LoadContract loadContract, ImageDao imageDao) {
        this.mLoadContract = loadContract;
        this.mImageDao = imageDao;
    }

    public void onDestroy() {
        mLoadContract = null;
    }


    @Override
    public void saveImage(String imagePath) {
        if (imagePath==null) {
            throw new RuntimeException("Tried to save an image with null URI");
        }
        String date = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(new Date());
        createImage(imagePath, date);

    }

    private void createImage(String imagePath, String date) {
        Image newImage = new Image(imagePath, date);
        mImageDao.insert(newImage);
    }

    @Override
    public void start() {

    }
}
