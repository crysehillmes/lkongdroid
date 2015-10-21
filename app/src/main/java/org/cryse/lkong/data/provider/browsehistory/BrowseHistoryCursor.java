package org.cryse.lkong.data.provider.browsehistory;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code browse_history} table.
 */
public class BrowseHistoryCursor extends AbstractCursor implements BrowseHistoryModel {
    public BrowseHistoryCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(BrowseHistoryColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record user id, nullable.
     */
    public long getUserId() {
        Long res = getLongOrNull(BrowseHistoryColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record forum id, nullable.
     * Can be {@code null}.
     */
    @Nullable
    public Long getForumId() {
        Long res = getLongOrNull(BrowseHistoryColumns.FORUM_ID);
        return res;
    }

    /**
     * The history record forum title, nullable.
     * Can be {@code null}.
     */
    @Nullable
    public String getForumTitle() {
        String res = getStringOrNull(BrowseHistoryColumns.FORUM_TITLE);
        return res;
    }

    /**
     * The history record thread id, not null.
     */
    public long getThreadId() {
        Long res = getLongOrNull(BrowseHistoryColumns.THREAD_ID);
        if (res == null)
            throw new NullPointerException("The value of 'thread_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record post id, nullable.
     * Can be {@code null}.
     */
    @Nullable
    public Long getPostId() {
        Long res = getLongOrNull(BrowseHistoryColumns.POST_ID);
        return res;
    }

    /**
     * The history record thread id, not null.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getThreadTitle() {
        String res = getStringOrNull(BrowseHistoryColumns.THREAD_TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'thread_title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record author id, nullable.
     */
    public long getThreadAuthorId() {
        Long res = getLongOrNull(BrowseHistoryColumns.THREAD_AUTHOR_ID);
        if (res == null)
            throw new NullPointerException("The value of 'thread_author_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record author name, nullable.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getThreadAuthorName() {
        String res = getStringOrNull(BrowseHistoryColumns.THREAD_AUTHOR_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'thread_author_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The history record read timestamp, nullable.
     */
    public long getLastReadTime() {
        Long res = getLongOrNull(BrowseHistoryColumns.LAST_READ_TIME);
        if (res == null)
            throw new NullPointerException("The value of 'last_read_time' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
