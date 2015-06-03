package org.cryse.lkong.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.lkong.data.LKongDatabaseHelper;
import org.cryse.lkong.data.model.PinnedForumEntity;

import javax.inject.Inject;

public class PinnedForumDao extends AbstractDao<PinnedForumEntity, Long> {
    public static final String TABLE_NAME = "PINNED_FORUM_MODEL";
    public static final String COLUMN_FORUM_ID = "FORUM_ID";
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
                "'" + COLUMN_FORUM_ID + "' LONG PRIMARY KEY," + // 0: forumId
                "'" + COLUMN_FORUM_NAME + "' TEXT," + // 1: forumName
                "'" + COLUMN_FORUM_ICON + "' TEXT," + // 2: forumIcon
                "'" + COLUMN_SORT_VALUE + "' LONG);");   // 3: sortValue
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }

    @Override
    public ContentValues entityToContentValues(PinnedForumEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FORUM_ID, entity.getForumId());
        values.put(COLUMN_FORUM_NAME, entity.getForumName());
        values.put(COLUMN_FORUM_ICON, entity.getForumIcon());
        values.put(COLUMN_SORT_VALUE, entity.getForumIcon());
        return values;
    }

    @Override
    public PinnedForumEntity readEntity(Cursor cursor, int offset) {
        PinnedForumEntity entity = new PinnedForumEntity( //
                cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0), // forumId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // forumName
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // forumIcon
                cursor.isNull(offset + 3) ? 0 : cursor.getLong(offset + 3) // sortValue
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, PinnedForumEntity entity, int offset) {
        entity.setForumId(cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0));
        entity.setForumName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setForumIcon(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSortValue(cursor.isNull(offset + 3) ? 0 : cursor.getLong(offset + 3));
    }
}
