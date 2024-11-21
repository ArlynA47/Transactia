package com.egls.transactia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // Table for authenticated user
    private static final String TABLE_USER = "User";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Table for unauthenticated user
    private static final String TABLE_UNAUTHENTICATED_USER = "UnauthenticatedUser";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create authenticated user table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        // Create unauthenticated user table
        String CREATE_UNAUTHENTICATED_USER_TABLE = "CREATE TABLE " + TABLE_UNAUTHENTICATED_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_UNAUTHENTICATED_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNAUTHENTICATED_USER);
        onCreate(db);
    }

    // Insert or update authenticated user email and password
    public void saveUserDetails(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER); // Clear the table for single-user setup

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_USER, null, values);

        db.close();
    }

    // Retrieve authenticated user email and password
    public String[] getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] userDetails = null;
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_EMAIL, COLUMN_PASSWORD}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            userDetails = new String[]{cursor.getString(0), cursor.getString(1)};
        }
        cursor.close();
        db.close();
        return userDetails;
    }

    // Delete authenticated user email and password
    public void deleteUserDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }

    // Insert unauthenticated user email and password
    public void saveUnauthenticatedUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_UNAUTHENTICATED_USER); // Clear the table for single-user setup

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_UNAUTHENTICATED_USER, null, values);

        db.close();
    }

    // Retrieve unauthenticated user email and password
    public String[] getUnauthenticatedUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] userDetails = null;
        Cursor cursor = db.query(TABLE_UNAUTHENTICATED_USER, new String[]{COLUMN_EMAIL, COLUMN_PASSWORD}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            userDetails = new String[]{cursor.getString(0), cursor.getString(1)};
        }
        cursor.close();
        db.close();
        return userDetails;
    }

    // Delete unauthenticated user email and password
    public void deleteUnauthenticatedUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_UNAUTHENTICATED_USER);
        db.close();
    }
}
