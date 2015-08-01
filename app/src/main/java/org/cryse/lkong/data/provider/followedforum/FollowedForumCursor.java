package org.cryse.lkong.data.provider.followedforum;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code followed_forum} table.
 */
public class FollowedForumCursor extends AbstractCursor implements FollowedForumModel {
    public FollowedForumCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(FollowedForumColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Owner id.
     */
    public long getUserId() {
        Long res = getLongOrNull(FollowedForumColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Followed forum id.
     */
    public long getForumId() {
        Long res = getLongOrNull(FollowedForumColumns.FORUM_ID);
        if (res == null)
            throw new NullPointerException("The value of 'forum_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Forum name.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getForumName() {
        String res = getStringOrNull(FollowedForumColumns.FORUM_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'forum_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Forum icon url.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getForumIcon() {
        String res = getStringOrNull(FollowedForumColumns.FORUM_ICON);
        if (res == null)
            throw new NullPointerException("The value of 'forum_icon' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Sort value of forum.
     */
    public long getForumSortValue() {
        Long res = getLongOrNull(FollowedForumColumns.FORUM_SORT_VALUE);
        if (res == null)
            throw new NullPointerException("The value of 'forum_sort_value' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
