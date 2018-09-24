package com.mad.sharpdesign.loadmenu;

import com.mad.sharpdesign.BasePresenter;

import java.net.URI;

public interface LoadContract {
    interface Presenter extends BasePresenter{
        void saveImage(String imagePath);

    }

}
