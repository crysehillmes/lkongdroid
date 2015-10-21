package org.cryse.lkong.data.provider.browsehistory;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code browse_history} table.
 */
public class BrowseHistoryContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return BrowseHistoryColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable BrowseHistorySelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable BrowseHistorySelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The history record user id, nullable.
     */
    public BrowseHistoryContentValues putUserId(long value) {
        mContentValues.put(BrowseHistoryColumns.USER_ID, value);
        return this;
    }


    /**
     * The history record forum id, nullable.
     */
    public BrowseHistoryContentValues putForumId(@Nullable Long value) {
        mContentValues.put(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistoryContentValues putForumIdNull() {
        mContentValues.putNull(BrowseHistoryColumns.FORUM_ID);
        return this;
    }

    /**
     * The history record forum title, nullable.
     */
    public BrowseHistoryContentValues putForumTitle(@Nullable String value) {
        mContentValues.put(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistoryContentValues putForumTitleNull() {
        mContentValues.putNull(BrowseHistoryColumns.FORUM_TITLE);
        return this;
    }

    /**
     * The history record thread id, not null.
     */
    public BrowseHistoryContentValues putThreadId(long value) {
        mContentValues.put(BrowseHistoryColumns.THREAD_ID, value);
        return this;
    }


    /**
     * The history record post id, nullable.
     */
    public BrowseHistoryContentValues putPostId(@Nullable Long value) {
        mContentValues.put(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistoryContentValues putPostIdNull() {
        mContentValues.putNull(BrowseHistoryColumns.POST_ID);
        return this;
    }

    /**
     * The history record thread id, not null.
     */
    public BrowseHistoryContentValues putThreadTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("threadTitle must not be null");
        mContentValues.put(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }


    /**
     * The history record author id, nullable.
     */
    public BrowseHistoryContentValues putThreadAuthorId(long value) {
        mContentValues.put(BrowseHistoryColumns.THREAD_AUTHOR_ID, value);
        return this;
    }


    /**
     * The history record author name, nullable.
     */
    public BrowseHistoryContentValues putThreadAuthorName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("threadAuthorName must not be null");
        mContentValues.put(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }


    /**
     * The history record read timestamp, nullable.
     */
    public BrowseHistoryContentValues putLastReadTime(long value) {
        mContentValues.put(BrowseHistoryColumns.LAST_READ_TIME, value);
        return this;
    }

}
