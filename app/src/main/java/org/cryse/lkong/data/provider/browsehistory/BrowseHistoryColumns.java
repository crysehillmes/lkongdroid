package org.cryse.lkong.data.provider.browsehistory;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.lkong.data.provider.LKongContentProvider;
import org.cryse.lkong.data.provider.browsehistory.BrowseHistoryColumns;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

/**
 * Store User browse history here.
 */
public class BrowseHistoryColumns implements BaseColumns {
    public static final String TABLE_NAME = "browse_history";
    public static final Uri CONTENT_URI = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The history record user id, nullable.
     */
    public static final String USER_ID = "user_id";

    /**
     * The history record forum id, nullable.
     */
    public static final String FORUM_ID = "forum_id";

    /**
     * The history record forum title, nullable.
     */
    public static final String FORUM_TITLE = "forum_title";

    /**
     * The history record thread id, not null.
     */
    public static final String THREAD_ID = "thread_id";

    /**
     * The history record post id, nullable.
     */
    public static final String POST_ID = "post_id";

    /**
     * The history record thread id, not null.
     */
    public static final String THREAD_TITLE = "thread_title";

    /**
     * The history record author id, nullable.
     */
    public static final String THREAD_AUTHOR_ID = "thread_author_id";

    /**
     * The history record author name, nullable.
     */
    public static final String THREAD_AUTHOR_NAME = "thread_author_name";

    /**
     * The history record read timestamp, nullable.
     */
    public static final String LAST_READ_TIME = "last_read_time";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_ID,
            FORUM_ID,
            FORUM_TITLE,
            THREAD_ID,
            POST_ID,
            THREAD_TITLE,
            THREAD_AUTHOR_ID,
            THREAD_AUTHOR_NAME,
            LAST_READ_TIME
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(FORUM_ID) || c.contains("." + FORUM_ID)) return true;
            if (c.equals(FORUM_TITLE) || c.contains("." + FORUM_TITLE)) return true;
            if (c.equals(THREAD_ID) || c.contains("." + THREAD_ID)) return true;
            if (c.equals(POST_ID) || c.contains("." + POST_ID)) return true;
            if (c.equals(THREAD_TITLE) || c.contains("." + THREAD_TITLE)) return true;
            if (c.equals(THREAD_AUTHOR_ID) || c.contains("." + THREAD_AUTHOR_ID)) return true;
            if (c.equals(THREAD_AUTHOR_NAME) || c.contains("." + THREAD_AUTHOR_NAME)) return true;
            if (c.equals(LAST_READ_TIME) || c.contains("." + LAST_READ_TIME)) return true;
        }
        return false;
    }

}
