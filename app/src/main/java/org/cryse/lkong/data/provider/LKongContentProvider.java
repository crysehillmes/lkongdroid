package org.cryse.lkong.data.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.data.provider.base.BaseContentProvider;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

public class LKongContentProvider extends BaseContentProvider {
    private static final String TAG = LKongContentProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "org.cryse.lkong.data.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_CACHE_OBJECT = 0;
    private static final int URI_TYPE_CACHE_OBJECT_ID = 1;

    private static final int URI_TYPE_FOLLOWED_FORUM = 2;
    private static final int URI_TYPE_FOLLOWED_FORUM_ID = 3;

    private static final int URI_TYPE_FOLLOWED_THREAD = 4;
    private static final int URI_TYPE_FOLLOWED_THREAD_ID = 5;

    private static final int URI_TYPE_FOLLOWED_USER = 6;
    private static final int URI_TYPE_FOLLOWED_USER_ID = 7;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, CacheObjectColumns.TABLE_NAME, URI_TYPE_CACHE_OBJECT);
        URI_MATCHER.addURI(AUTHORITY, CacheObjectColumns.TABLE_NAME + "/#", URI_TYPE_CACHE_OBJECT_ID);
        URI_MATCHER.addURI(AUTHORITY, FollowedForumColumns.TABLE_NAME, URI_TYPE_FOLLOWED_FORUM);
        URI_MATCHER.addURI(AUTHORITY, FollowedForumColumns.TABLE_NAME + "/#", URI_TYPE_FOLLOWED_FORUM_ID);
        URI_MATCHER.addURI(AUTHORITY, FollowedThreadColumns.TABLE_NAME, URI_TYPE_FOLLOWED_THREAD);
        URI_MATCHER.addURI(AUTHORITY, FollowedThreadColumns.TABLE_NAME + "/#", URI_TYPE_FOLLOWED_THREAD_ID);
        URI_MATCHER.addURI(AUTHORITY, FollowedUserColumns.TABLE_NAME, URI_TYPE_FOLLOWED_USER);
        URI_MATCHER.addURI(AUTHORITY, FollowedUserColumns.TABLE_NAME + "/#", URI_TYPE_FOLLOWED_USER_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return LKongSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_CACHE_OBJECT:
                return TYPE_CURSOR_DIR + CacheObjectColumns.TABLE_NAME;
            case URI_TYPE_CACHE_OBJECT_ID:
                return TYPE_CURSOR_ITEM + CacheObjectColumns.TABLE_NAME;

            case URI_TYPE_FOLLOWED_FORUM:
                return TYPE_CURSOR_DIR + FollowedForumColumns.TABLE_NAME;
            case URI_TYPE_FOLLOWED_FORUM_ID:
                return TYPE_CURSOR_ITEM + FollowedForumColumns.TABLE_NAME;

            case URI_TYPE_FOLLOWED_THREAD:
                return TYPE_CURSOR_DIR + FollowedThreadColumns.TABLE_NAME;
            case URI_TYPE_FOLLOWED_THREAD_ID:
                return TYPE_CURSOR_ITEM + FollowedThreadColumns.TABLE_NAME;

            case URI_TYPE_FOLLOWED_USER:
                return TYPE_CURSOR_DIR + FollowedUserColumns.TABLE_NAME;
            case URI_TYPE_FOLLOWED_USER_ID:
                return TYPE_CURSOR_ITEM + FollowedUserColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_CACHE_OBJECT:
            case URI_TYPE_CACHE_OBJECT_ID:
                res.table = CacheObjectColumns.TABLE_NAME;
                res.idColumn = CacheObjectColumns._ID;
                res.tablesWithJoins = CacheObjectColumns.TABLE_NAME;
                res.orderBy = CacheObjectColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_FOLLOWED_FORUM:
            case URI_TYPE_FOLLOWED_FORUM_ID:
                res.table = FollowedForumColumns.TABLE_NAME;
                res.idColumn = FollowedForumColumns._ID;
                res.tablesWithJoins = FollowedForumColumns.TABLE_NAME;
                res.orderBy = FollowedForumColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_FOLLOWED_THREAD:
            case URI_TYPE_FOLLOWED_THREAD_ID:
                res.table = FollowedThreadColumns.TABLE_NAME;
                res.idColumn = FollowedThreadColumns._ID;
                res.tablesWithJoins = FollowedThreadColumns.TABLE_NAME;
                res.orderBy = FollowedThreadColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_FOLLOWED_USER:
            case URI_TYPE_FOLLOWED_USER_ID:
                res.table = FollowedUserColumns.TABLE_NAME;
                res.idColumn = FollowedUserColumns._ID;
                res.tablesWithJoins = FollowedUserColumns.TABLE_NAME;
                res.orderBy = FollowedUserColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_CACHE_OBJECT_ID:
            case URI_TYPE_FOLLOWED_FORUM_ID:
            case URI_TYPE_FOLLOWED_THREAD_ID:
            case URI_TYPE_FOLLOWED_USER_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
