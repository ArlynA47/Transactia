package com.egls.transactia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "User";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "userId";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Insert or Update User ID
    public void saveUserId(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER); // Clear the table for a single-user setup

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        db.insert(TABLE_USER, null, values);

        db.close();
    }

    // Retrieve User ID
    public String getUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String userId = null;
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_USER_ID}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            userId = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    // Delete User ID
    public void deleteUserId() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }
}

