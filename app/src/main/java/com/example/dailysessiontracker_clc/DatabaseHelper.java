package com.example.dailysessiontracker_clc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDates.db";
    private static final String TABLE_NAME = "user_table";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_DATE = "DATE_ONLY";
    public static final String COL_PAID = "PAID";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + 
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
            COL_NAME + " TEXT, " + 
            COL_DATE + " TEXT, " + 
            COL_PAID + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_PAID + " INTEGER DEFAULT 0");
        }
    }

    public boolean insertData(String name, String date) {
        return insertData(name, date, 0);
    }

    public boolean insertData(String name, String date, int paid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_PAID, paid);
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public boolean updateData(String id, String name, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_DATE, newDate);
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id}) > 0;
    }

    public boolean updatePaidStatus(String id, boolean isPaid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PAID, isPaid ? 1 : 0);
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id}) > 0;
    }

    public long getRecordCount() {
        return DatabaseUtils.queryNumEntries(this.getReadableDatabase(), TABLE_NAME);
    }

    public List<Map<String, String>> getAllData() {
        List<Map<String, String>> dataList = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                map.put("date", cursor.getString(2));
                map.put("paid", cursor.getString(3)); // This might be null if column doesn't exist yet? No, default 0.
                dataList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataList;
    }
}