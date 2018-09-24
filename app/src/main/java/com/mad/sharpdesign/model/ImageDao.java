package com.mad.sharpdesign.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

@Dao
public interface ImageDao {

    @Insert
    void insert(Image image);

    @Query("DELETE from image_table")
    void deleteAll();

    @Query("SELECT * from image_table ORDER BY date DESC")
    LiveData<ArrayList<Image>>getAllImages();

    @Query("SELECT * FROM image_table WHERE id=:id")
    Image getImageById(Integer id);

}
