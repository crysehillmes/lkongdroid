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
import org.cryse.lkong.data.provider.browsehistory.BrowseHistoryColumns;
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

    protected static final int URI_TYPE_CACHE_OBJECT = 0;
    protected static final int URI_TYPE_CACHE_OBJECT_ID = 1;

    protected static final int URI_TYPE_FOLLOWED_FORUM = 2;
    protected static final int URI_TYPE_FOLLOWED_FORUM_ID = 3;

    protected static final int URI_TYPE_FOLLOWED_THREAD = 4;
    protected static final int URI_TYPE_FOLLOWED_THREAD_ID = 5;

    protected static final int URI_TYPE_FOLLOWED_USER = 6;
    protected static final int URI_TYPE_FOLLOWED_USER_ID = 7;

    protected static final int URI_TYPE_BROWSE_HISTORY = 8;
    protected static final int URI_TYPE_BROWSE_HISTORY_ID = 9;


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
        URI_MATCHER.addURI(AUTHORITY, BrowseHistoryColumns.TABLE_NAME, URI_TYPE_BROWSE_HISTORY);
        URI_MATCHER.addURI(AUTHORITY, BrowseHistoryColumns.TABLE_NAME + "/#", URI_TYPE_BROWSE_HISTORY_ID);
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
        int match = getUriMatcher().match(uri);
        switch (match) {
            case URI_TYPE_BROWSE_HISTORY:
                return TYPE_CURSOR_DIR + BrowseHistoryColumns.TABLE_NAME;
            case URI_TYPE_BROWSE_HISTORY_ID:
                return TYPE_CURSOR_ITEM + BrowseHistoryColumns.TABLE_NAME;

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
        if (DEBUG) Log.d(getLogTag(), "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(getLogTag(), "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(getLogTag(), "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(getLogTag(), "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(getLogTag(), "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = getUriMatcher().match(uri);
        switch (matchedId) {
            case URI_TYPE_BROWSE_HISTORY:
            case URI_TYPE_BROWSE_HISTORY_ID:
                res.table = BrowseHistoryColumns.TABLE_NAME;
                res.idColumn = BrowseHistoryColumns._ID;
                res.tablesWithJoins = BrowseHistoryColumns.TABLE_NAME;
                res.orderBy = BrowseHistoryColumns.DEFAULT_ORDER;
                break;

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
            case URI_TYPE_BROWSE_HISTORY_ID:
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

    protected String getLogTag() {
        return TAG;
    }

    protected UriMatcher getUriMatcher() {
        return URI_MATCHER;
    }
}
