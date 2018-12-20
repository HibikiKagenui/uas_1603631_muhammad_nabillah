package com.resonatestudios.uas_1603631_muhammad_nabillah.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class OpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dbBrakeLog.db";
    public static final String TABLE_CREATE =
            "CREATE TABLE BRAKE_LOG (ID INTEGER PRIMARY KEY AUTOINCREMENT, WAKTU TEXT, LATITUDE REAL, LONGITUDE REAL);";

    public OpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS BRAKE_LOG");
    }
}
