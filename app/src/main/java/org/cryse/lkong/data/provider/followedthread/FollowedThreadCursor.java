package org.cryse.lkong.data.provider.followedthread;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code followed_thread} table.
 */
public class FollowedThreadCursor extends AbstractCursor implements FollowedThreadModel {
    public FollowedThreadCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(FollowedThreadColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Owner id.
     */
    public long getUserId() {
        Long res = getLongOrNull(FollowedThreadColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Followed thread id.
     */
    public long getThreadId() {
        Long res = getLongOrNull(FollowedThreadColumns.THREAD_ID);
        if (res == null)
            throw new NullPointerException("The value of 'thread_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Thread title.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getThreadTitle() {
        String res = getStringOrNull(FollowedThreadColumns.THREAD_TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'thread_title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Thread author id.
     */
    public long getThreadAuthorId() {
        Long res = getLongOrNull(FollowedThreadColumns.THREAD_AUTHOR_ID);
        if (res == null)
            throw new NullPointerException("The value of 'thread_author_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Thread author name.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getThreadAuthorName() {
        String res = getStringOrNull(FollowedThreadColumns.THREAD_AUTHOR_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'thread_author_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Thread timestamp.
     */
    public long getThreadTimestamp() {
        Long res = getLongOrNull(FollowedThreadColumns.THREAD_TIMESTAMP);
        if (res == null)
            throw new NullPointerException("The value of 'thread_timestamp' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Thread timestamp.
     */
    public int getThreadReplyCount() {
        Integer res = getIntegerOrNull(FollowedThreadColumns.THREAD_REPLY_COUNT);
        if (res == null)
            throw new NullPointerException("The value of 'thread_reply_count' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
