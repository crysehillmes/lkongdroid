package org.cryse.lkong.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import org.cryse.lkong.BuildConfig;

/**
 * Implement your custom database creation or upgrade code here.
 *
 * This file will not be overwritten if you re-run the content provider generator.
 */
public class LKongSQLiteOpenHelperCallbacks {
    private static final String TAG = LKongSQLiteOpenHelperCallbacks.class.getSimpleName();

    public void onOpen(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onOpen");
        // Insert your db open code here.
    }

    public void onPreCreate(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPreCreate");
        // Insert your db creation code here. This is called before your tables are created.
    }

    public void onPostCreate(final Context context, final SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onPostCreate");
        // Insert your db creation code here. This is called after your tables are created.
    }

    public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Insert your upgrading code here.
        if(oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS " + "PINNED_FORUM_MODEL");
            db.execSQL("DROP TABLE IF EXISTS " + "CACHE_OBJECT");
            db.execSQL("DROP TABLE IF EXISTS " + "USER_ACCOUNT_MODEL");
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_TABLE_CACHE_OBJECT);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_CACHE_OBJECT_CACHE_KEY);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_TABLE_FOLLOWED_FORUM);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_FORUM_USER_ID);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_FORUM_FORUM_ID);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_TABLE_FOLLOWED_THREAD);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_THREAD_USER_ID);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_THREAD_THREAD_ID);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_TABLE_FOLLOWED_USER);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_USER_USER_ID);
            db.execSQL(LKongSQLiteOpenHelper.SQL_CREATE_INDEX_FOLLOWED_USER_TARGET_USER_ID);
        }
    }
}
