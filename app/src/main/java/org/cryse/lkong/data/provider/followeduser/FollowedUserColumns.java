package org.cryse.lkong.data.provider.followeduser;

import android.net.Uri;
import android.provider.BaseColumns;

import org.cryse.lkong.data.provider.LKongContentProvider;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;

/**
 * Followed user.
 */
public class FollowedUserColumns implements BaseColumns {
    public static final String TABLE_NAME = "followed_user";
    public static final Uri CONTENT_URI = Uri.parse(LKongContentProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Self id.
     */
    public static final String USER_ID = "user_id";

    /**
     * Follow target user id.
     */
    public static final String TARGET_USER_ID = "target_user_id";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            USER_ID,
            TARGET_USER_ID
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(USER_ID) || c.contains("." + USER_ID)) return true;
            if (c.equals(TARGET_USER_ID) || c.contains("." + TARGET_USER_ID)) return true;
        }
        return false;
    }

}
