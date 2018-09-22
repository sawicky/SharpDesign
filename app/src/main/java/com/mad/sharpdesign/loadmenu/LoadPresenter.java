package com.mad.sharpdesign.loadmenu;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.sharpdesign.R;

import static android.support.v4.content.ContextCompat.getSystemService;

public class LoadPresenter{
    private LoadContract mLoadContract;
    private LoadInteractor mLoadInteractor;
    private AlertDialog.Builder builder;

    LoadPresenter(LoadContract loadContract, LoadInteractor loadInteractor) {
        this.mLoadContract = loadContract;
        this.mLoadInteractor = loadInteractor;
    }

    public void onDestroy() {
        mLoadContract = null;
    }


}
