package org.cryse.lkong.data.provider.followedthread;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.lkong.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code followed_thread} table.
 */
public class FollowedThreadSelection extends AbstractSelection<FollowedThreadSelection> {
    @Override
    protected Uri baseUri() {
        return FollowedThreadColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedThreadCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedThreadCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedThreadCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public FollowedThreadCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedThreadCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedThreadCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedThreadCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public FollowedThreadCursor query(Context context) {
        return query(context, null);
    }


    public FollowedThreadSelection id(long... value) {
        addEquals("followed_thread." + FollowedThreadColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection idNot(long... value) {
        addNotEquals("followed_thread." + FollowedThreadColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection orderById(boolean desc) {
        orderBy("followed_thread." + FollowedThreadColumns._ID, desc);
        return this;
    }

    public FollowedThreadSelection orderById() {
        return orderById(false);
    }

    public FollowedThreadSelection userId(long... value) {
        addEquals(FollowedThreadColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection userIdNot(long... value) {
        addNotEquals(FollowedThreadColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection userIdGt(long value) {
        addGreaterThan(FollowedThreadColumns.USER_ID, value);
        return this;
    }

    public FollowedThreadSelection userIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedThreadColumns.USER_ID, value);
        return this;
    }

    public FollowedThreadSelection userIdLt(long value) {
        addLessThan(FollowedThreadColumns.USER_ID, value);
        return this;
    }

    public FollowedThreadSelection userIdLtEq(long value) {
        addLessThanOrEquals(FollowedThreadColumns.USER_ID, value);
        return this;
    }

    public FollowedThreadSelection orderByUserId(boolean desc) {
        orderBy(FollowedThreadColumns.USER_ID, desc);
        return this;
    }

    public FollowedThreadSelection orderByUserId() {
        orderBy(FollowedThreadColumns.USER_ID, false);
        return this;
    }

    public FollowedThreadSelection threadId(long... value) {
        addEquals(FollowedThreadColumns.THREAD_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadIdNot(long... value) {
        addNotEquals(FollowedThreadColumns.THREAD_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadIdGt(long value) {
        addGreaterThan(FollowedThreadColumns.THREAD_ID, value);
        return this;
    }

    public FollowedThreadSelection threadIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedThreadColumns.THREAD_ID, value);
        return this;
    }

    public FollowedThreadSelection threadIdLt(long value) {
        addLessThan(FollowedThreadColumns.THREAD_ID, value);
        return this;
    }

    public FollowedThreadSelection threadIdLtEq(long value) {
        addLessThanOrEquals(FollowedThreadColumns.THREAD_ID, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadId(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_ID, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadId() {
        orderBy(FollowedThreadColumns.THREAD_ID, false);
        return this;
    }

    public FollowedThreadSelection threadTitle(String... value) {
        addEquals(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection threadTitleNot(String... value) {
        addNotEquals(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection threadTitleLike(String... value) {
        addLike(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection threadTitleContains(String... value) {
        addContains(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection threadTitleStartsWith(String... value) {
        addStartsWith(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection threadTitleEndsWith(String... value) {
        addEndsWith(FollowedThreadColumns.THREAD_TITLE, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadTitle(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_TITLE, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadTitle() {
        orderBy(FollowedThreadColumns.THREAD_TITLE, false);
        return this;
    }

    public FollowedThreadSelection threadAuthorId(long... value) {
        addEquals(FollowedThreadColumns.THREAD_AUTHOR_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadAuthorIdNot(long... value) {
        addNotEquals(FollowedThreadColumns.THREAD_AUTHOR_ID, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadAuthorIdGt(long value) {
        addGreaterThan(FollowedThreadColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedThreadColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorIdLt(long value) {
        addLessThan(FollowedThreadColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorIdLtEq(long value) {
        addLessThanOrEquals(FollowedThreadColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadAuthorId(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_AUTHOR_ID, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadAuthorId() {
        orderBy(FollowedThreadColumns.THREAD_AUTHOR_ID, false);
        return this;
    }

    public FollowedThreadSelection threadAuthorName(String... value) {
        addEquals(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorNameNot(String... value) {
        addNotEquals(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorNameLike(String... value) {
        addLike(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorNameContains(String... value) {
        addContains(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorNameStartsWith(String... value) {
        addStartsWith(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection threadAuthorNameEndsWith(String... value) {
        addEndsWith(FollowedThreadColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadAuthorName(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_AUTHOR_NAME, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadAuthorName() {
        orderBy(FollowedThreadColumns.THREAD_AUTHOR_NAME, false);
        return this;
    }

    public FollowedThreadSelection threadTimestamp(long... value) {
        addEquals(FollowedThreadColumns.THREAD_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadTimestampNot(long... value) {
        addNotEquals(FollowedThreadColumns.THREAD_TIMESTAMP, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadTimestampGt(long value) {
        addGreaterThan(FollowedThreadColumns.THREAD_TIMESTAMP, value);
        return this;
    }

    public FollowedThreadSelection threadTimestampGtEq(long value) {
        addGreaterThanOrEquals(FollowedThreadColumns.THREAD_TIMESTAMP, value);
        return this;
    }

    public FollowedThreadSelection threadTimestampLt(long value) {
        addLessThan(FollowedThreadColumns.THREAD_TIMESTAMP, value);
        return this;
    }

    public FollowedThreadSelection threadTimestampLtEq(long value) {
        addLessThanOrEquals(FollowedThreadColumns.THREAD_TIMESTAMP, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadTimestamp(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_TIMESTAMP, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadTimestamp() {
        orderBy(FollowedThreadColumns.THREAD_TIMESTAMP, false);
        return this;
    }

    public FollowedThreadSelection threadReplyCount(int... value) {
        addEquals(FollowedThreadColumns.THREAD_REPLY_COUNT, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadReplyCountNot(int... value) {
        addNotEquals(FollowedThreadColumns.THREAD_REPLY_COUNT, toObjectArray(value));
        return this;
    }

    public FollowedThreadSelection threadReplyCountGt(int value) {
        addGreaterThan(FollowedThreadColumns.THREAD_REPLY_COUNT, value);
        return this;
    }

    public FollowedThreadSelection threadReplyCountGtEq(int value) {
        addGreaterThanOrEquals(FollowedThreadColumns.THREAD_REPLY_COUNT, value);
        return this;
    }

    public FollowedThreadSelection threadReplyCountLt(int value) {
        addLessThan(FollowedThreadColumns.THREAD_REPLY_COUNT, value);
        return this;
    }

    public FollowedThreadSelection threadReplyCountLtEq(int value) {
        addLessThanOrEquals(FollowedThreadColumns.THREAD_REPLY_COUNT, value);
        return this;
    }

    public FollowedThreadSelection orderByThreadReplyCount(boolean desc) {
        orderBy(FollowedThreadColumns.THREAD_REPLY_COUNT, desc);
        return this;
    }

    public FollowedThreadSelection orderByThreadReplyCount() {
        orderBy(FollowedThreadColumns.THREAD_REPLY_COUNT, false);
        return this;
    }
}
