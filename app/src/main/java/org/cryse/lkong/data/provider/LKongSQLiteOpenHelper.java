package org.cryse.lkong.data.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

public class LKongSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = LKongSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "lkong.db";
    private static final int DATABASE_VERSION = 4;
    private static LKongSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final LKongSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_CACHE_OBJECT = "CREATE TABLE IF NOT EXISTS "
            + CacheObjectColumns.TABLE_NAME + " ( "
            + CacheObjectColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CacheObjectColumns.CACHE_KEY + " TEXT NOT NULL, "
            + CacheObjectColumns.CACHE_VALUE + " TEXT NOT NULL, "
            + CacheObjectColumns.CACHE_TIME_CREATE + " INTEGER, "
            + CacheObjectColumns.CACHE_TIME_EXPIRE + " INTEGER "
            + ", CONSTRAINT unique_cache_key UNIQUE (cache_key) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_CACHE_OBJECT_CACHE_KEY = "CREATE INDEX IDX_CACHE_OBJECT_CACHE_KEY "
            + " ON " + CacheObjectColumns.TABLE_NAME + " ( " + CacheObjectColumns.CACHE_KEY + " );";

    public static final String SQL_CREATE_TABLE_FOLLOWED_FORUM = "CREATE TABLE IF NOT EXISTS "
            + FollowedForumColumns.TABLE_NAME + " ( "
            + FollowedForumColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FollowedForumColumns.USER_ID + " INTEGER NOT NULL, "
            + FollowedForumColumns.FORUM_ID + " INTEGER NOT NULL, "
            + FollowedForumColumns.FORUM_NAME + " TEXT NOT NULL, "
            + FollowedForumColumns.FORUM_ICON + " TEXT NOT NULL, "
            + FollowedForumColumns.FORUM_SORT_VALUE + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_followed_forum_id UNIQUE (user_id, forum_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_FORUM_USER_ID = "CREATE INDEX IDX_FOLLOWED_FORUM_USER_ID "
            + " ON " + FollowedForumColumns.TABLE_NAME + " ( " + FollowedForumColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_FORUM_FORUM_ID = "CREATE INDEX IDX_FOLLOWED_FORUM_FORUM_ID "
            + " ON " + FollowedForumColumns.TABLE_NAME + " ( " + FollowedForumColumns.FORUM_ID + " );";

    public static final String SQL_CREATE_TABLE_FOLLOWED_THREAD = "CREATE TABLE IF NOT EXISTS "
            + FollowedThreadColumns.TABLE_NAME + " ( "
            + FollowedThreadColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FollowedThreadColumns.USER_ID + " INTEGER NOT NULL, "
            + FollowedThreadColumns.THREAD_ID + " INTEGER NOT NULL, "
            + FollowedThreadColumns.THREAD_TITLE + " TEXT NOT NULL, "
            + FollowedThreadColumns.THREAD_AUTHOR_ID + " INTEGER NOT NULL, "
            + FollowedThreadColumns.THREAD_AUTHOR_NAME + " TEXT NOT NULL, "
            + FollowedThreadColumns.THREAD_TIMESTAMP + " INTEGER NOT NULL, "
            + FollowedThreadColumns.THREAD_REPLY_COUNT + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_followed_thread_id UNIQUE (user_id, thread_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_THREAD_USER_ID = "CREATE INDEX IDX_FOLLOWED_THREAD_USER_ID "
            + " ON " + FollowedThreadColumns.TABLE_NAME + " ( " + FollowedThreadColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_THREAD_THREAD_ID = "CREATE INDEX IDX_FOLLOWED_THREAD_THREAD_ID "
            + " ON " + FollowedThreadColumns.TABLE_NAME + " ( " + FollowedThreadColumns.THREAD_ID + " );";

    public static final String SQL_CREATE_TABLE_FOLLOWED_USER = "CREATE TABLE IF NOT EXISTS "
            + FollowedUserColumns.TABLE_NAME + " ( "
            + FollowedUserColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FollowedUserColumns.USER_ID + " INTEGER NOT NULL, "
            + FollowedUserColumns.TARGET_USER_ID + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_followed_user_id UNIQUE (user_id, target_user_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_USER_USER_ID = "CREATE INDEX IDX_FOLLOWED_USER_USER_ID "
            + " ON " + FollowedUserColumns.TABLE_NAME + " ( " + FollowedUserColumns.USER_ID + " );";

    public static final String SQL_CREATE_INDEX_FOLLOWED_USER_TARGET_USER_ID = "CREATE INDEX IDX_FOLLOWED_USER_TARGET_USER_ID "
            + " ON " + FollowedUserColumns.TABLE_NAME + " ( " + FollowedUserColumns.TARGET_USER_ID + " );";

    // @formatter:on

    public static LKongSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static LKongSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static LKongSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new LKongSQLiteOpenHelper(context);
    }

    private LKongSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new LKongSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static LKongSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new LKongSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private LKongSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new LKongSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_CACHE_OBJECT);
        db.execSQL(SQL_CREATE_INDEX_CACHE_OBJECT_CACHE_KEY);
        db.execSQL(SQL_CREATE_TABLE_FOLLOWED_FORUM);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_FORUM_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_FORUM_FORUM_ID);
        db.execSQL(SQL_CREATE_TABLE_FOLLOWED_THREAD);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_THREAD_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_THREAD_THREAD_ID);
        db.execSQL(SQL_CREATE_TABLE_FOLLOWED_USER);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_USER_USER_ID);
        db.execSQL(SQL_CREATE_INDEX_FOLLOWED_USER_TARGET_USER_ID);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
