package org.cryse.lkong.data.provider.cacheobject;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.lkong.data.provider.LKongContentProvider;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

/**
 * Cache any kind of object here.
 */
public class CacheObjectColumns implements BaseColumns {
    public static final String TABLE_NAME = "cache_object";
    public static final Uri CONTENT_URI_NOTIFY = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME)
            .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(true)).build();
    public static final Uri CONTENT_URI = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME)
            .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(false)).build();

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The key of cache object, unique and indexed.
     */
    public static final String CACHE_KEY = "cache_key";

    /**
     * The value of cache, could be simple String or Json String.
     */
    public static final String CACHE_VALUE = "cache_value";

    /**
     * The create time of cache, nullable.
     */
    public static final String CACHE_TIME_CREATE = "cache_time_create";

    /**
     * The expire time of cache, nullable.
     */
    public static final String CACHE_TIME_EXPIRE = "cache_time_expire";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            CACHE_KEY,
            CACHE_VALUE,
            CACHE_TIME_CREATE,
            CACHE_TIME_EXPIRE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(CACHE_KEY) || c.contains("." + CACHE_KEY)) return true;
            if (c.equals(CACHE_VALUE) || c.contains("." + CACHE_VALUE)) return true;
            if (c.equals(CACHE_TIME_CREATE) || c.contains("." + CACHE_TIME_CREATE)) return true;
            if (c.equals(CACHE_TIME_EXPIRE) || c.contains("." + CACHE_TIME_EXPIRE)) return true;
        }
        return false;
    }

}
