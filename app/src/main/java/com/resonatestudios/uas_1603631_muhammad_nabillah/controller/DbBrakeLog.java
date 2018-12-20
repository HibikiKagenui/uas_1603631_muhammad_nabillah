package com.resonatestudios.uas_1603631_muhammad_nabillah.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.resonatestudios.uas_1603631_muhammad_nabillah.adapter.BrakeLogAdapter;
import com.resonatestudios.uas_1603631_muhammad_nabillah.model.BrakeLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;

public class DbBrakeLog {
    public static final String TABLE_NAME = "BRAKE_LOG";
    private final OpenHelper dbHelper;
    // kolom yang diambil
    String[] collumns = {"ID", "WAKTU", "LATITUDE", "LONGITUDE"};
    private SQLiteDatabase db;

    BrakeLogAdapter brakeLogAdapter;

    public DbBrakeLog(Context context, BrakeLogAdapter brakeLogAdapter) {
        dbHelper = new OpenHelper(context);
        this.brakeLogAdapter = brakeLogAdapter;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public ArrayList<BrakeLog> getAll() {
        Cursor cursor;
        ArrayList<BrakeLog> mahasiswaList = new ArrayList<>();

        cursor = db.query(TABLE_NAME, collumns, null, null, null, null, "ID DESC");

        if (cursor.moveToFirst()) {
            brakeLogAdapter.addToList(new BrakeLog(
                    new Date(cursor.getString(1)),
                    cursor.getDouble(2),
                    cursor.getDouble(3)
            ));
            while (cursor.moveToNext()) {
                brakeLogAdapter.addToList(new BrakeLog(
                        new Date(cursor.getString(1)),
                        cursor.getDouble(2),
                        cursor.getDouble(3)
                ));
            }
        }

        cursor.close();

        return mahasiswaList;
    }

    public boolean insert(Date date, double latitude, double longitude) {
        ContentValues newValue = new ContentValues();
        newValue.put("WAKTU", String.valueOf(date));
        newValue.put("LATITUDE", latitude);
        newValue.put("LONGITUDE", longitude);
        return db.insert(TABLE_NAME, null, newValue) > 0;
    }


    public boolean deleteAll() {
        return db.delete(TABLE_NAME, "ID>-1", null) > 0;
    }

}
