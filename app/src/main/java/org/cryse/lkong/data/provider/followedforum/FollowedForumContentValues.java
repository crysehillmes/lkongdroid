package org.cryse.lkong.data.provider.followedforum;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code followed_forum} table.
 */
public class FollowedForumContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FollowedForumColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FollowedForumSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable FollowedForumSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Owner id.
     */
    public FollowedForumContentValues putUserId(long value) {
        mContentValues.put(FollowedForumColumns.USER_ID, value);
        return this;
    }


    /**
     * Followed forum id.
     */
    public FollowedForumContentValues putForumId(long value) {
        mContentValues.put(FollowedForumColumns.FORUM_ID, value);
        return this;
    }


    /**
     * Forum name.
     */
    public FollowedForumContentValues putForumName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("forumName must not be null");
        mContentValues.put(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }


    /**
     * Forum icon url.
     */
    public FollowedForumContentValues putForumIcon(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("forumIcon must not be null");
        mContentValues.put(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }


    /**
     * Sort value of forum.
     */
    public FollowedForumContentValues putForumSortValue(long value) {
        mContentValues.put(FollowedForumColumns.FORUM_SORT_VALUE, value);
        return this;
    }

}
