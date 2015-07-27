package org.cryse.lkong.data.provider.followedthread;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.lkong.data.provider.LKongContentProvider;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

/**
 * Followed thread.
 */
public class FollowedThreadColumns implements BaseColumns {
    public static final String TABLE_NAME = "followed_thread";
    public static final Uri CONTENT_URI = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Owner id.
     */
    public static final String USER_ID = "user_id";

    /**
     * Followed thread id.
     */
    public static final String THREAD_ID = "thread_id";

    /**
     * Thread title.
     */
    public static final String THREAD_TITLE = "thread_title";

    /**
     * Thread author id.
     */
    public static final String THREAD_AUTHOR_ID = "thread_author_id";

    /**
     * Thread author name.
     */
    public static final String THREAD_AUTHOR_NAME = "thread_author_name";

    /**
     * Thread timestamp.
     */
    public static final String THREAD_TIMESTAMP = "thread_timestamp";

    /**
     * Thread timestamp.
     */
    public static final String THREAD_REPLY_COUNT = "thread_reply_count";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_ID,
            THREAD_ID,
            THREAD_TITLE,
            THREAD_AUTHOR_ID,
            THREAD_AUTHOR_NAME,
            THREAD_TIMESTAMP,
            THREAD_REPLY_COUNT
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(THREAD_ID) || c.contains("." + THREAD_ID)) return true;
            if (c.equals(THREAD_TITLE) || c.contains("." + THREAD_TITLE)) return true;
            if (c.equals(THREAD_AUTHOR_ID) || c.contains("." + THREAD_AUTHOR_ID)) return true;
            if (c.equals(THREAD_AUTHOR_NAME) || c.contains("." + THREAD_AUTHOR_NAME)) return true;
            if (c.equals(THREAD_TIMESTAMP) || c.contains("." + THREAD_TIMESTAMP)) return true;
            if (c.equals(THREAD_REPLY_COUNT) || c.contains("." + THREAD_REPLY_COUNT)) return true;
        }
        return false;
    }

}
