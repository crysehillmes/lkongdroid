package org.cryse.lkong.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.lkong.data.LKongDatabaseHelper;
import org.cryse.lkong.data.model.PinnedForumEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PinnedForumDao extends AbstractDao<PinnedForumEntity, Long> {
    public static final String TABLE_NAME = "PINNED_FORUM_MODEL";
    public static final String COLUMN_FORUM_ID = "FORUM_ID";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_FORUM_NAME = "FORUM_NAME";
    public static final String COLUMN_FORUM_ICON = "FORUM_ICON";
    public static final String COLUMN_SORT_VALUE = "FORUM_SORT_VALUE";

    public static final String TABLE_PRIMARY_KEY = COLUMN_FORUM_ID;

    @Inject
    public PinnedForumDao(LKongDatabaseHelper helper) {
        super(helper, true, TABLE_NAME, TABLE_PRIMARY_KEY);
    }

    public static void createTableStatement(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLE_NAME + "' (" + //
                "'" + COLUMN_FORUM_ID + "' LONG," + // 0: forumId
                "'" + COLUMN_USER_ID + "' LONG," + // 1: userId
                "'" + COLUMN_FORUM_NAME + "' TEXT," + // 2: forumName
                "'" + COLUMN_FORUM_ICON + "' TEXT," + // 3: forumIcon
                "'" + COLUMN_SORT_VALUE + "' LONG," +
                "PRIMARY KEY (" + COLUMN_FORUM_ID + "," + COLUMN_USER_ID +"));" +
                "CREATE INDEX 'index_" + COLUMN_USER_ID.toLowerCase() + "' ON '" + TABLE_NAME + "' ('" + COLUMN_USER_ID + "' ASC);");   // 4: sortValue
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }

    @Override
    public ContentValues entityToContentValues(PinnedForumEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FORUM_ID, entity.getForumId());
        values.put(COLUMN_USER_ID, entity.getUserId());
        values.put(COLUMN_FORUM_NAME, entity.getForumName());
        values.put(COLUMN_FORUM_ICON, entity.getForumIcon());
        values.put(COLUMN_SORT_VALUE, entity.getForumIcon());
        return values;
    }

    @Override
    public PinnedForumEntity readEntity(Cursor cursor, int offset) {
        PinnedForumEntity entity = new PinnedForumEntity( //
                cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0), // forumId
                cursor.isNull(offset + 1) ? 0 : cursor.getLong(offset + 1), // userId
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // forumName
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // forumIcon
                cursor.isNull(offset + 4) ? 0 : cursor.getLong(offset + 4) // sortValue
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, PinnedForumEntity entity, int offset) {
        entity.setForumId(cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? 0 : cursor.getLong(offset + 1));
        entity.setForumName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setForumIcon(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSortValue(cursor.isNull(offset + 4) ? 0 : cursor.getLong(offset + 4));
    }

    public List<PinnedForumEntity> loadAllForUser(long userId) {
        String QUERY_ALL_FOR_USER = String.format("SELECT * FROM %s WHERE %s = %d;", mTableName, COLUMN_USER_ID, userId);
        List<PinnedForumEntity> entities = new ArrayList<PinnedForumEntity>();

        Cursor cursor = mDatabase.rawQuery(QUERY_ALL_FOR_USER, new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PinnedForumEntity entity = readEntity(cursor, 0);
            entities.add(entity);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return entities;
    }

    public int unpinForum(long uid, long fid) {
        return mDatabase.delete(
                mTableName,
                COLUMN_USER_ID + " = ? AND " + COLUMN_FORUM_ID + " = ? ",
                new String[] { Long.toString(uid), Long.toString(fid) });
    }

    public boolean isPinned(long uid, long fid) {
        String QUERY_EXIST = String.format("SELECT 1 FROM %s WHERE %s = ? AND %s = ?", mTableName, COLUMN_USER_ID, COLUMN_FORUM_ID);
        Cursor cursor = mDatabase.rawQuery(QUERY_EXIST,
                new String[]{Long.toString(uid), Long.toString(fid) });
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
