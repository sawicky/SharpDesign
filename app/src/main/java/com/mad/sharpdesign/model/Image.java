package com.mad.sharpdesign.model;


public class Image {
    private String filePath;
    private String date;

    public Image(String filePath, String date) {
        this.filePath = filePath;
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
