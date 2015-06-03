package org.cryse.lkong.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.lkong.data.LKongDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T, K> {

    protected LKongDatabaseHelper mSQLiteOpenHelper;
    protected SQLiteDatabase mDatabase;
    protected String mTableName;
    protected String mPrimaryKeyColumn;
    public AbstractDao(LKongDatabaseHelper helper, boolean writable, String tableName, String primaryKeyColumn) {
        this.mSQLiteOpenHelper = helper;
        this.mDatabase = writable ? mSQLiteOpenHelper.getWritableDatabase() : mSQLiteOpenHelper.getReadableDatabase();
        this.mTableName = tableName;
        this.mPrimaryKeyColumn = primaryKeyColumn;
    }

    public boolean isOpen() {
        if(mDatabase != null && mDatabase.isOpen())
            return true;
        return false;
    }

    public abstract ContentValues entityToContentValues(T entity);

    public long insert(T entity) {
        ContentValues values = entityToContentValues(entity);
        return mDatabase.insertWithOnConflict(mTableName, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
    }

    public long insertOrReplace(T entity) {
        ContentValues values = entityToContentValues(entity);
        return mDatabase.insertWithOnConflict(mTableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int delete(K key) {
        return mDatabase.delete(
                mTableName,
                mPrimaryKeyColumn + " = ?",
                new String[] { key.toString() });
    }

    public T load(K key) {
        String QUERY_LOAD = String.format("SELECT * FROM %s WHERE %s = ?", mTableName, mPrimaryKeyColumn);
        Cursor c = mDatabase.rawQuery(QUERY_LOAD, new String[]{ key.toString() });

        if (c != null) {
            if(!c.moveToFirst())
                return null;
        } else {
            return null;
        }

        return readEntity(c, 0);
    }

    public int update(K id, T entity) {
        ContentValues values = entityToContentValues(entity);
        return mDatabase.update(
                mTableName,
                values,
                mPrimaryKeyColumn + " = ?",
                new String[] { id.toString() }
        );
    }

    public boolean exist(K id) {
        String QUERY_EXIST = String.format("SELECT 1 FROM %s WHERE %s = ?", mTableName, mPrimaryKeyColumn);
        Cursor cursor = mDatabase.rawQuery(QUERY_EXIST,
                new String[]{id.toString()});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public  List<T> loadAll() {
        String QUERY_ALL = String.format("SELECT * FROM %s;", mTableName);
        List<T> entities = new ArrayList<T>();

        Cursor cursor = mDatabase.rawQuery(QUERY_ALL, new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T entity = readEntity(cursor, 0);
            entities.add(entity);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return entities;
    }

    public abstract T readEntity(Cursor cursor, int offset);

    public abstract void readEntity(Cursor cursor, T entity, int offset);
}
