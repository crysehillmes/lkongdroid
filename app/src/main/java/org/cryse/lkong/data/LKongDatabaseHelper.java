package org.cryse.lkong.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LKongDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "lkong.db";
    public static final int DATABASE_VERSION = 1;

    public LKongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        UserAccountDao.createTableStatement(db, false);
        CacheObjectDao.createTableStatement(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
