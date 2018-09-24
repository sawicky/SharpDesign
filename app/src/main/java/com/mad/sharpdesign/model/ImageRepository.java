package com.mad.sharpdesign.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ImageRepository {
    private ImageDao mImageDao;
    private LiveData<ArrayList<Image>> mImages;

    ImageRepository(Application application) {
        ImageRoomDatabase db = ImageRoomDatabase.getDatabase(application);
        mImageDao = db.imageDao();
        mImages = mImageDao.getAllImages();
    }
    LiveData<ArrayList<Image>> getImages() {
        return mImages;
    }

    public void insert (Image image) {
        new insertAsyncTask(mImageDao).execute(image);
    }

    private static class insertAsyncTask extends AsyncTask<Image, Void, Void> {

        private ImageDao mAsyncTaskDao;

        insertAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(Image... images) {
            mAsyncTaskDao.insert(images[0]);
            return null;
        }
    }
}
