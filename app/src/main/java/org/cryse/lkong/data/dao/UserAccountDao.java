package org.cryse.lkong.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.lkong.data.LKongDatabaseHelper;
import org.cryse.lkong.data.model.UserAccountEntity;

import javax.inject.Inject;

public class UserAccountDao extends AbstractDao<UserAccountEntity, Long> {
    public static final String TABLE_NAME = "USER_ACCOUNT_MODEL";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_AVATAR = "USER_AVATAR";
    public static final String COLUMN_AUTH_COOKIE = "AUTH_COOKIE";
    public static final String COLUMN_DZSBHEY_COOKIE = "DZSBHEY_COOKIE";
    public static final String COLUMN_IDENTITY_COOKIE = "IDENTITY_COOKIE";

    public static final String TABLE_PRIMARY_KEY = COLUMN_USER_ID;

    @Inject
    public UserAccountDao(LKongDatabaseHelper helper) {
        super(helper, true, TABLE_NAME, TABLE_PRIMARY_KEY);
    }

    public static void createTableStatement(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLE_NAME + "' (" + //
                "'" + COLUMN_USER_ID + "' LONG PRIMARY KEY," + // 0: userId
                "'" + COLUMN_EMAIL + "' TEXT," + // 1: email
                "'" + COLUMN_USER_NAME + "' TEXT," + // 2: userName
                "'" + COLUMN_USER_AVATAR + "' TEXT," + // 3: userAvatar
                "'" + COLUMN_AUTH_COOKIE + "' TEXT," + // 4: authCookie
                "'" + COLUMN_DZSBHEY_COOKIE + "' TEXT," + // 5: dzsbheyCookie
                "'" + COLUMN_IDENTITY_COOKIE + "' TEXT);"); // 6: identityCookie
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
    }

    @Override
    public ContentValues entityToContentValues(UserAccountEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, entity.getUserId());
        values.put(COLUMN_EMAIL, entity.getEmail());
        values.put(COLUMN_USER_NAME, entity.getUserName());
        values.put(COLUMN_USER_AVATAR, entity.getUserAvatar());
        values.put(COLUMN_AUTH_COOKIE, entity.getAuthCookie());
        values.put(COLUMN_DZSBHEY_COOKIE, entity.getDzsbheyCookie());
        values.put(COLUMN_IDENTITY_COOKIE, entity.getIdentityCookie());
        return values;
    }

    public UserAccountEntity readEntity(Cursor cursor, int offset) {
        UserAccountEntity entity = new UserAccountEntity( //
                cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0), // userId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // email
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userName
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // userAvatar
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // authCookie
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // dzsbheyCookie
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // identityCookie
        );
        return entity;
    }

    public void readEntity(Cursor cursor, UserAccountEntity entity, int offset) {
        entity.setUserId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEmail(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserAvatar(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAuthCookie(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDzsbheyCookie(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIdentityCookie(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
    }

    public int update(UserAccountEntity userAccount) {
        return super.update(userAccount.getUserId(), userAccount);
    }
}
