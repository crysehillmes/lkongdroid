package org.cryse.lkong.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.lkong.data.LKongDatabaseHelper;
import org.cryse.lkong.data.model.CacheObjectEntity;

import java.util.Date;

import javax.inject.Inject;

public class CacheObjectDao extends AbstractDao<CacheObjectEntity, String> {
    public static final String TABLE_NAME = "CACHE_OBJECT";

    public static final String COLUMN_KEY = "CACHE_OBJECT_KEY";
    public static final String COLUMN_VALUE = "CACHE_OBJECT_VALUE";
    public static final String COLUMN_CREATE_TIME = "CACHE_OBJECT_CREATE_TIME";
    public static final String COLUMN_EXPIRE_TIME = "CACHE_OBJECT_EXPIRE_TIME";

    public static final String TABLE_PRIMARY_KEY = COLUMN_KEY;


    @Inject
    public CacheObjectDao(LKongDatabaseHelper helper) {
        super(helper, true, TABLE_NAME, TABLE_PRIMARY_KEY);
    }

    public static void createTableStatement(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLE_NAME + "' (" + //
                "'" + COLUMN_KEY + "' LONG PRIMARY KEY," + // 0: key
                "'" + COLUMN_VALUE + "' TEXT," + // 1: value
                "'" + COLUMN_CREATE_TIME + "' LONG," + // 2: createTime
                "'" + COLUMN_EXPIRE_TIME + "' LONG);"); // 3: expireTime
    }

    @Override
    public ContentValues entityToContentValues(CacheObjectEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY, entity.getKey());
        values.put(COLUMN_VALUE, entity.getValue());
        values.put(COLUMN_CREATE_TIME, (entity.getCreateTime() == null) ? null : entity.getCreateTime().getTime());
        values.put(COLUMN_EXPIRE_TIME, (entity.getExpireTime() == null) ? null : entity.getExpireTime().getTime());
        return values;
    }

    public CacheObjectEntity readEntity(Cursor cursor, int offset) {
        CacheObjectEntity entity = new CacheObjectEntity( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // key
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // value
                cursor.isNull(offset + 2) ? null : new Date(cursor.getLong(offset + 2)), // createTime
                cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)) // expireTime
        );
        return entity;
    }

    public void readEntity(Cursor cursor, CacheObjectEntity entity, int offset) {
        entity.setKey(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setValue(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreateTime(cursor.isNull(offset + 2) ? null : new Date(cursor.getLong(offset + 2)));
        entity.setExpireTime(cursor.isNull(offset + 3) ? null : new Date(cursor.getLong(offset + 3)));
    }

    public int update(CacheObjectEntity entity) {
        return super.update(entity.getKey(), entity);
    }

    public void putCache(String key, String value, Date expireTime) {
        long ret = this.insert(new CacheObjectEntity(key, value, new Date(), expireTime));
        if(ret == -1)
            throw new RuntimeException("Cache object insert error.");
    }

    public String getCache(String key) {
        CacheObjectEntity cacheObject = load(key);
        if(cacheObject == null) return null;
        Date nowTime = new Date();
        if((cacheObject.getExpireTime() != null && nowTime.before(cacheObject.getExpireTime())) || cacheObject.getExpireTime() == null) {
            return cacheObject.getValue();
        } else {
            throw new RuntimeException("Cache expired.");
        }
    }
}
