package org.cryse.lkong.data.provider.browsehistory;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.lkong.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code browse_history} table.
 */
public class BrowseHistorySelection extends AbstractSelection<BrowseHistorySelection> {
    @Override
    protected Uri baseUri() {
        return BrowseHistoryColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BrowseHistoryCursor} object, which is positioned before the first entry, or null.
     */
    public BrowseHistoryCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BrowseHistoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public BrowseHistoryCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code BrowseHistoryCursor} object, which is positioned before the first entry, or null.
     */
    public BrowseHistoryCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new BrowseHistoryCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public BrowseHistoryCursor query(Context context) {
        return query(context, null);
    }


    public BrowseHistorySelection id(long... value) {
        addEquals("browse_history." + BrowseHistoryColumns._ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection idNot(long... value) {
        addNotEquals("browse_history." + BrowseHistoryColumns._ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection orderById(boolean desc) {
        orderBy("browse_history." + BrowseHistoryColumns._ID, desc);
        return this;
    }

    public BrowseHistorySelection orderById() {
        return orderById(false);
    }

    public BrowseHistorySelection userId(long... value) {
        addEquals(BrowseHistoryColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection userIdNot(long... value) {
        addNotEquals(BrowseHistoryColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection userIdGt(long value) {
        addGreaterThan(BrowseHistoryColumns.USER_ID, value);
        return this;
    }

    public BrowseHistorySelection userIdGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.USER_ID, value);
        return this;
    }

    public BrowseHistorySelection userIdLt(long value) {
        addLessThan(BrowseHistoryColumns.USER_ID, value);
        return this;
    }

    public BrowseHistorySelection userIdLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.USER_ID, value);
        return this;
    }

    public BrowseHistorySelection orderByUserId(boolean desc) {
        orderBy(BrowseHistoryColumns.USER_ID, desc);
        return this;
    }

    public BrowseHistorySelection orderByUserId() {
        orderBy(BrowseHistoryColumns.USER_ID, false);
        return this;
    }

    public BrowseHistorySelection forumId(Long... value) {
        addEquals(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection forumIdNot(Long... value) {
        addNotEquals(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection forumIdGt(long value) {
        addGreaterThan(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection forumIdGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection forumIdLt(long value) {
        addLessThan(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection forumIdLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.FORUM_ID, value);
        return this;
    }

    public BrowseHistorySelection orderByForumId(boolean desc) {
        orderBy(BrowseHistoryColumns.FORUM_ID, desc);
        return this;
    }

    public BrowseHistorySelection orderByForumId() {
        orderBy(BrowseHistoryColumns.FORUM_ID, false);
        return this;
    }

    public BrowseHistorySelection forumTitle(String... value) {
        addEquals(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection forumTitleNot(String... value) {
        addNotEquals(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection forumTitleLike(String... value) {
        addLike(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection forumTitleContains(String... value) {
        addContains(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection forumTitleStartsWith(String... value) {
        addStartsWith(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection forumTitleEndsWith(String... value) {
        addEndsWith(BrowseHistoryColumns.FORUM_TITLE, value);
        return this;
    }

    public BrowseHistorySelection orderByForumTitle(boolean desc) {
        orderBy(BrowseHistoryColumns.FORUM_TITLE, desc);
        return this;
    }

    public BrowseHistorySelection orderByForumTitle() {
        orderBy(BrowseHistoryColumns.FORUM_TITLE, false);
        return this;
    }

    public BrowseHistorySelection threadId(long... value) {
        addEquals(BrowseHistoryColumns.THREAD_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection threadIdNot(long... value) {
        addNotEquals(BrowseHistoryColumns.THREAD_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection threadIdGt(long value) {
        addGreaterThan(BrowseHistoryColumns.THREAD_ID, value);
        return this;
    }

    public BrowseHistorySelection threadIdGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.THREAD_ID, value);
        return this;
    }

    public BrowseHistorySelection threadIdLt(long value) {
        addLessThan(BrowseHistoryColumns.THREAD_ID, value);
        return this;
    }

    public BrowseHistorySelection threadIdLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.THREAD_ID, value);
        return this;
    }

    public BrowseHistorySelection orderByThreadId(boolean desc) {
        orderBy(BrowseHistoryColumns.THREAD_ID, desc);
        return this;
    }

    public BrowseHistorySelection orderByThreadId() {
        orderBy(BrowseHistoryColumns.THREAD_ID, false);
        return this;
    }

    public BrowseHistorySelection postId(Long... value) {
        addEquals(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection postIdNot(Long... value) {
        addNotEquals(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection postIdGt(long value) {
        addGreaterThan(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection postIdGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection postIdLt(long value) {
        addLessThan(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection postIdLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.POST_ID, value);
        return this;
    }

    public BrowseHistorySelection orderByPostId(boolean desc) {
        orderBy(BrowseHistoryColumns.POST_ID, desc);
        return this;
    }

    public BrowseHistorySelection orderByPostId() {
        orderBy(BrowseHistoryColumns.POST_ID, false);
        return this;
    }

    public BrowseHistorySelection threadTitle(String... value) {
        addEquals(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection threadTitleNot(String... value) {
        addNotEquals(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection threadTitleLike(String... value) {
        addLike(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection threadTitleContains(String... value) {
        addContains(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection threadTitleStartsWith(String... value) {
        addStartsWith(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection threadTitleEndsWith(String... value) {
        addEndsWith(BrowseHistoryColumns.THREAD_TITLE, value);
        return this;
    }

    public BrowseHistorySelection orderByThreadTitle(boolean desc) {
        orderBy(BrowseHistoryColumns.THREAD_TITLE, desc);
        return this;
    }

    public BrowseHistorySelection orderByThreadTitle() {
        orderBy(BrowseHistoryColumns.THREAD_TITLE, false);
        return this;
    }

    public BrowseHistorySelection threadAuthorId(long... value) {
        addEquals(BrowseHistoryColumns.THREAD_AUTHOR_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection threadAuthorIdNot(long... value) {
        addNotEquals(BrowseHistoryColumns.THREAD_AUTHOR_ID, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection threadAuthorIdGt(long value) {
        addGreaterThan(BrowseHistoryColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorIdGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorIdLt(long value) {
        addLessThan(BrowseHistoryColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorIdLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.THREAD_AUTHOR_ID, value);
        return this;
    }

    public BrowseHistorySelection orderByThreadAuthorId(boolean desc) {
        orderBy(BrowseHistoryColumns.THREAD_AUTHOR_ID, desc);
        return this;
    }

    public BrowseHistorySelection orderByThreadAuthorId() {
        orderBy(BrowseHistoryColumns.THREAD_AUTHOR_ID, false);
        return this;
    }

    public BrowseHistorySelection threadAuthorName(String... value) {
        addEquals(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorNameNot(String... value) {
        addNotEquals(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorNameLike(String... value) {
        addLike(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorNameContains(String... value) {
        addContains(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorNameStartsWith(String... value) {
        addStartsWith(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection threadAuthorNameEndsWith(String... value) {
        addEndsWith(BrowseHistoryColumns.THREAD_AUTHOR_NAME, value);
        return this;
    }

    public BrowseHistorySelection orderByThreadAuthorName(boolean desc) {
        orderBy(BrowseHistoryColumns.THREAD_AUTHOR_NAME, desc);
        return this;
    }

    public BrowseHistorySelection orderByThreadAuthorName() {
        orderBy(BrowseHistoryColumns.THREAD_AUTHOR_NAME, false);
        return this;
    }

    public BrowseHistorySelection lastReadTime(long... value) {
        addEquals(BrowseHistoryColumns.LAST_READ_TIME, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection lastReadTimeNot(long... value) {
        addNotEquals(BrowseHistoryColumns.LAST_READ_TIME, toObjectArray(value));
        return this;
    }

    public BrowseHistorySelection lastReadTimeGt(long value) {
        addGreaterThan(BrowseHistoryColumns.LAST_READ_TIME, value);
        return this;
    }

    public BrowseHistorySelection lastReadTimeGtEq(long value) {
        addGreaterThanOrEquals(BrowseHistoryColumns.LAST_READ_TIME, value);
        return this;
    }

    public BrowseHistorySelection lastReadTimeLt(long value) {
        addLessThan(BrowseHistoryColumns.LAST_READ_TIME, value);
        return this;
    }

    public BrowseHistorySelection lastReadTimeLtEq(long value) {
        addLessThanOrEquals(BrowseHistoryColumns.LAST_READ_TIME, value);
        return this;
    }

    public BrowseHistorySelection orderByLastReadTime(boolean desc) {
        orderBy(BrowseHistoryColumns.LAST_READ_TIME, desc);
        return this;
    }

    public BrowseHistorySelection orderByLastReadTime() {
        orderBy(BrowseHistoryColumns.LAST_READ_TIME, false);
        return this;
    }
}
