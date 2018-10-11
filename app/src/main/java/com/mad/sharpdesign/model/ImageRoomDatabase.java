package com.mad.sharpdesign.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Room Database. Currently unimplemented.
 */
@Database(entities = {Image.class}, version = 1)
public abstract class ImageRoomDatabase extends RoomDatabase{
    public abstract ImageDao imageDao();
    private static volatile ImageRoomDatabase sImageRoomDatabase;

    public static ImageRoomDatabase getDatabase(final Context context) {
        if (sImageRoomDatabase == null) {
            synchronized (ImageRoomDatabase.class) {
                if (sImageRoomDatabase == null) {
                    //create database
                    sImageRoomDatabase = Room.databaseBuilder(context.getApplicationContext(), ImageRoomDatabase.class, "image_database").build();
                }
            }
        }
        return sImageRoomDatabase;
    }


}
