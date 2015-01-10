package org.cryse.lkong.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class UserAccountDao {
    private LKongDatabaseHelper mSQLiteOpenHelper;
    private SQLiteDatabase mDatabase;
    public static final String TABLE_NAME = "USER_ACCOUNT_MODEL";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_AVATAR = "USER_AVATAR";
    public static final String COLUMN_AUTH_COOKIE = "AUTH_COOKIE";
    public static final String COLUMN_DZSBHEY_COOKIE = "DZSBHEY_COOKIE";
    public static final String COLUMN_IDENTITY_COOKIE = "IDENTITY_COOKIE";


    @Inject
    public UserAccountDao(LKongDatabaseHelper helper) {
        this.mSQLiteOpenHelper = helper;
        this.mDatabase = mSQLiteOpenHelper.getWritableDatabase();
    }

    public static void createTableStatement(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'" + TABLE_NAME + "' (" + //
                "'" + COLUMN_USER_ID + "' LONG," + // 0: userId
                "'" + COLUMN_EMAIL + "' TEXT," + // 1: email
                "'" + COLUMN_USER_NAME + "' TEXT," + // 2: userName
                "'" + COLUMN_USER_AVATAR + "' TEXT," + // 3: userAvatar
                "'" + COLUMN_AUTH_COOKIE + "' TEXT," + // 4: authCookie
                "'" + COLUMN_DZSBHEY_COOKIE + "' TEXT," + // 5: dzsbheyCookie
                "'" + COLUMN_IDENTITY_COOKIE + "' TEXT);"); // 6: identityCookie
    }

    public long insert(UserAccountModel userAccount) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userAccount.getUserId());
        values.put(COLUMN_EMAIL, userAccount.getEmail());
        values.put(COLUMN_USER_NAME, userAccount.getUserName());
        values.put(COLUMN_USER_AVATAR, userAccount.getUserAvatar());
        values.put(COLUMN_AUTH_COOKIE, userAccount.getAuthCookie());
        values.put(COLUMN_DZSBHEY_COOKIE, userAccount.getDzsbheyCookie());
        values.put(COLUMN_IDENTITY_COOKIE, userAccount.getIdentityCookie());
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    public int delete(long userId) {
            return mDatabase.delete(
                    TABLE_NAME,
                    COLUMN_USER_ID + " = " + userId,
                    null);
    }

    public void load(UserAccountModel userAccount) {

    }

    public int update(UserAccountModel userAccount) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userAccount.getUserId());
        values.put(COLUMN_EMAIL, userAccount.getEmail());
        values.put(COLUMN_USER_NAME, userAccount.getUserName());
        values.put(COLUMN_USER_AVATAR, userAccount.getUserAvatar());
        values.put(COLUMN_AUTH_COOKIE, userAccount.getAuthCookie());
        values.put(COLUMN_DZSBHEY_COOKIE, userAccount.getDzsbheyCookie());
        values.put(COLUMN_IDENTITY_COOKIE, userAccount.getIdentityCookie());
        return mDatabase.update(
                TABLE_NAME,
                values,
                COLUMN_USER_ID + " = " + userAccount.getUserId(),
                null
        );
    }

    private static final String QUERY_ALL = "SELECT * FROM " + TABLE_NAME +";";

    public List<UserAccountModel> loadAll() {
        List<UserAccountModel> accounts = new ArrayList<UserAccountModel>();

        Cursor cursor = mDatabase.rawQuery(QUERY_ALL, new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserAccountModel userAccount = readEntity(cursor, 0);
            accounts.add(userAccount);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return accounts;
    }

    public UserAccountModel readEntity(Cursor cursor, int offset) {
        UserAccountModel entity = new UserAccountModel( //
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

    public void readEntity(Cursor cursor, UserAccountModel entity, int offset) {
        entity.setUserId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEmail(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUserAvatar(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAuthCookie(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDzsbheyCookie(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIdentityCookie(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
    }
}
