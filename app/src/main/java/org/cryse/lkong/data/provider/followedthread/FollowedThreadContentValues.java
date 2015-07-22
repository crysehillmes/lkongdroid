package org.cryse.lkong.data.provider.followedthread;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code followed_thread} table.
 */
public class FollowedThreadContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FollowedThreadColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FollowedThreadSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable FollowedThreadSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Owner id.
     */
    public FollowedThreadContentValues putUserId(long value) {
        mContentValues.put(FollowedThreadColumns.USER_ID, value);
        return this;
    }


    /**
     * Followed thread id.
     */
    public FollowedThreadContentValues putThreadId(long value) {
        mContentValues.put(FollowedThreadColumns.THREAD_ID, value);
        return this;
    }


    /**
     * Thread title.
     */
    public FollowedThreadContentValues putThreadTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("threadTitle must not be null");
        mContentValues.put(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }


    /**
     * Thread author id.
     */
    public FollowedThreadContentValues putThreadAuthorId(long value) {
        mContentValues.put(FollowedThreadColumns.THREAD_AUTHOR_ID, value);
        return this;
    }


    /**
     * Thread author name.
     */
    public FollowedThreadContentValues putThreadAuthorName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("threadAuthorName must not be null");
        mContentValues.put(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }


    /**
     * Thread timestamp.
     */
    public FollowedThreadContentValues putThreadTimestamp(long value) {
        mContentValues.put(FollowedThreadColumns.THREAD_TIMESTAMP, value);
        return this;
    }


    /**
     * Thread timestamp.
     */
    public FollowedThreadContentValues putThreadReplyCount(int value) {
        mContentValues.put(FollowedThreadColumns.THREAD_REPLY_COUNT, value);
        return this;
    }

}
