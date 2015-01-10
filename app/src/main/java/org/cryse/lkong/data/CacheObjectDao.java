package org.cryse.lkong.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class CacheObjectDao {
    private LKongDatabaseHelper mSQLiteOpenHelper;
    private SQLiteDatabase mDatabase;
    public static final String TABLE_NAME = "CACHE_OBJECT";
    public static final String COLUMN_KEY = "CACHE_OBJECT_KEY";
    public static final String COLUMN_VALUE = "CACHE_OBJECT_VALUE";
    public static final String COLUMN_CREATE_TIME = "CACHE_OBJECT_CREATE_TIME";
    public static final String COLUMN_EXPIRE_TIME = "CACHE_OBJECT_EXPIRE_TIME";


    @Inject
    public CacheObjectDao(LKongDatabaseHelper helper) {
        this.mSQLiteOpenHelper = helper;
        this.mDatabase = mSQLiteOpenHelper.getWritableDatabase();
    }

    public static void createTableStatement(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLE_NAME + "' (" + //
                "'" + COLUMN_KEY + "' LONG," + // 0: key
                "'" + COLUMN_VALUE + "' TEXT," + // 1: value
                "'" + COLUMN_CREATE_TIME + "' TEXT," + // 2: createTime
                "'" + COLUMN_EXPIRE_TIME + "' TEXT);"); // 3: expireTime
    }

    public long insert(CacheObjectModel cacheObject) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY, cacheObject.getKey());
        values.put(COLUMN_VALUE, cacheObject.getValue());
        values.put(COLUMN_CREATE_TIME, cacheObject.getCreateTime().getTime());
        values.put(COLUMN_EXPIRE_TIME, cacheObject.getExpireTime().getTime());
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    public int delete(String key) {
        return mDatabase.delete(
                TABLE_NAME,
                COLUMN_KEY + " = " + key,
                null);
    }

    public void load(CacheObjectModel cacheObject) {

    }

    public int update(CacheObjectModel cacheObject) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY, cacheObject.getKey());
        values.put(COLUMN_VALUE, cacheObject.getValue());
        values.put(COLUMN_CREATE_TIME, cacheObject.getCreateTime().getTime());
        values.put(COLUMN_EXPIRE_TIME, cacheObject.getExpireTime().getTime());
        return mDatabase.update(
                TABLE_NAME,
                values,
                COLUMN_KEY + " = " + cacheObject.getKey(),
                null
        );
    }

    private static final String QUERY_ALL = "SELECT * FROM " + TABLE_NAME +";";

    public List<CacheObjectModel> loadAll() {
        List<CacheObjectModel> cacheObjects = new ArrayList<CacheObjectModel>();

        Cursor cursor = mDatabase.rawQuery(QUERY_ALL, new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CacheObjectModel cacheObject = readEntity(cursor, 0);
            cacheObjects.add(cacheObject);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return cacheObjects;
    }

    public CacheObjectModel readEntity(Cursor cursor, int offset) {
        CacheObjectModel entity = new CacheObjectModel( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // key
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // value
                cursor.isNull(offset + 2) ? null : new Date(cursor.getLong(offset + 2)), // createTime
                cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)) // expireTime
        );
        return entity;
    }

    public void readEntity(Cursor cursor, CacheObjectModel entity, int offset) {
        entity.setKey(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setValue(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreateTime(cursor.isNull(offset + 2) ? null : new Date(cursor.getLong(offset + 2)));
        entity.setExpireTime(cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)));
    }
}
