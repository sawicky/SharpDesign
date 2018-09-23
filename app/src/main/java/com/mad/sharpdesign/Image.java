package com.mad.sharpdesign;

import java.sql.Blob;

public class Image {
    private String mFilePath;
    private String mDate;

    public Image(String filePath, String date) {
        mFilePath = filePath;
        mDate = date;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
