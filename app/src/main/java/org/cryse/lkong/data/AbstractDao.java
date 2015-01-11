package org.cryse.lkong.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    }

    public abstract ContentValues entityToContentValues(T entity);

    public long insert(T entity) {
        ContentValues values = entityToContentValues(entity);
        return mDatabase.insert(mTableName, null, values);
    }

    public int delete(K key) {
        return mDatabase.delete(
                mTableName,
                mPrimaryKeyColumn + " = ?",
                new String[] { key.toString() });
    }

    public T load(K key) {
        String LOAD_QUERY = "SELECT * FROM " + mTableName + " WHERE " + mPrimaryKeyColumn + " = ?";
        Cursor c = mDatabase.rawQuery(LOAD_QUERY, new String[]{ key.toString() });

        if (c != null)
            c.moveToFirst();

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

    public  List<T> loadAll() {
        String QUERY_ALL = "SELECT * FROM " + mTableName +";";
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
