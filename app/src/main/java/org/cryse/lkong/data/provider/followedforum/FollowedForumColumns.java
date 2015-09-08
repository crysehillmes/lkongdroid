package org.cryse.lkong.data.provider.followedforum;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.lkong.data.provider.LKongContentProvider;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

/**
 * Followed forum.
 */
public class FollowedForumColumns implements BaseColumns {
    public static final String TABLE_NAME = "followed_forum";
    public static final Uri CONTENT_URI_NOTIFY = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME)
            .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(true)).build();
    public static final Uri CONTENT_URI = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME)
            .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(false)).build();

    public static Uri contentUri(String authority) {
        return Uri.parse("content://" + authority + "/" + TABLE_NAME)
                .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(false)).build();
    }

    public static Uri contentUriNotify(String authority) {
        return Uri.parse("content://" + authority + "/" + TABLE_NAME)
                .buildUpon().appendQueryParameter("QUERY_NOTIFY", Boolean.toString(true)).build();
    }

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Owner id.
     */
    public static final String USER_ID = "user_id";

    /**
     * Followed forum id.
     */
    public static final String FORUM_ID = "forum_id";

    /**
     * Forum name.
     */
    public static final String FORUM_NAME = "forum_name";

    /**
     * Forum icon url.
     */
    public static final String FORUM_ICON = "forum_icon";

    /**
     * Sort value of forum.
     */
    public static final String FORUM_SORT_VALUE = "forum_sort_value";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_ID,
            FORUM_ID,
            FORUM_NAME,
            FORUM_ICON,
            FORUM_SORT_VALUE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(FORUM_ID) || c.contains("." + FORUM_ID)) return true;
            if (c.equals(FORUM_NAME) || c.contains("." + FORUM_NAME)) return true;
            if (c.equals(FORUM_ICON) || c.contains("." + FORUM_ICON)) return true;
            if (c.equals(FORUM_SORT_VALUE) || c.contains("." + FORUM_SORT_VALUE)) return true;
        }
        return false;
    }

}
