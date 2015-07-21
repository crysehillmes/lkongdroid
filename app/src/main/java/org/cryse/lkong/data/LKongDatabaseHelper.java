package org.cryse.lkong.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.cryse.lkong.data.dao.CacheObjectDao;
import org.cryse.lkong.data.dao.PinnedForumDao;

public class LKongDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "lkong.db";
    public static final int DATABASE_VERSION = 3;

    public LKongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CacheObjectDao.createTableStatement(db, false);
        PinnedForumDao.createTableStatement(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2) {
            CacheObjectDao.dropTable(db);
            PinnedForumDao.dropTable(db);
            CacheObjectDao.createTableStatement(db, false);
            PinnedForumDao.createTableStatement(db, false);
        } else if(oldVersion == 2 && newVersion == 3) {
            db.execSQL("DROP TABLE IF EXISTS USER_ACCOUNT_MODEL;");
        } else if(oldVersion == 1 && newVersion == 3) {
            CacheObjectDao.dropTable(db);
            PinnedForumDao.dropTable(db);
            CacheObjectDao.createTableStatement(db, false);
            PinnedForumDao.createTableStatement(db, false);
            db.execSQL("DROP TABLE IF EXISTS USER_ACCOUNT_MODEL;");
        }
    }
}
