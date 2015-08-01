package org.cryse.lkong.data.provider.followedforum;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.lkong.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code followed_forum} table.
 */
public class FollowedForumSelection extends AbstractSelection<FollowedForumSelection> {
    @Override
    protected Uri baseUri() {
        return FollowedForumColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedForumCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedForumCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedForumCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public FollowedForumCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedForumCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedForumCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedForumCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public FollowedForumCursor query(Context context) {
        return query(context, null);
    }


    public FollowedForumSelection id(long... value) {
        addEquals("followed_forum." + FollowedForumColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection idNot(long... value) {
        addNotEquals("followed_forum." + FollowedForumColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection orderById(boolean desc) {
        orderBy("followed_forum." + FollowedForumColumns._ID, desc);
        return this;
    }

    public FollowedForumSelection orderById() {
        return orderById(false);
    }

    public FollowedForumSelection userId(long... value) {
        addEquals(FollowedForumColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection userIdNot(long... value) {
        addNotEquals(FollowedForumColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection userIdGt(long value) {
        addGreaterThan(FollowedForumColumns.USER_ID, value);
        return this;
    }

    public FollowedForumSelection userIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedForumColumns.USER_ID, value);
        return this;
    }

    public FollowedForumSelection userIdLt(long value) {
        addLessThan(FollowedForumColumns.USER_ID, value);
        return this;
    }

    public FollowedForumSelection userIdLtEq(long value) {
        addLessThanOrEquals(FollowedForumColumns.USER_ID, value);
        return this;
    }

    public FollowedForumSelection orderByUserId(boolean desc) {
        orderBy(FollowedForumColumns.USER_ID, desc);
        return this;
    }

    public FollowedForumSelection orderByUserId() {
        orderBy(FollowedForumColumns.USER_ID, false);
        return this;
    }

    public FollowedForumSelection forumId(long... value) {
        addEquals(FollowedForumColumns.FORUM_ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection forumIdNot(long... value) {
        addNotEquals(FollowedForumColumns.FORUM_ID, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection forumIdGt(long value) {
        addGreaterThan(FollowedForumColumns.FORUM_ID, value);
        return this;
    }

    public FollowedForumSelection forumIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedForumColumns.FORUM_ID, value);
        return this;
    }

    public FollowedForumSelection forumIdLt(long value) {
        addLessThan(FollowedForumColumns.FORUM_ID, value);
        return this;
    }

    public FollowedForumSelection forumIdLtEq(long value) {
        addLessThanOrEquals(FollowedForumColumns.FORUM_ID, value);
        return this;
    }

    public FollowedForumSelection orderByForumId(boolean desc) {
        orderBy(FollowedForumColumns.FORUM_ID, desc);
        return this;
    }

    public FollowedForumSelection orderByForumId() {
        orderBy(FollowedForumColumns.FORUM_ID, false);
        return this;
    }

    public FollowedForumSelection forumName(String... value) {
        addEquals(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection forumNameNot(String... value) {
        addNotEquals(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection forumNameLike(String... value) {
        addLike(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection forumNameContains(String... value) {
        addContains(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection forumNameStartsWith(String... value) {
        addStartsWith(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection forumNameEndsWith(String... value) {
        addEndsWith(FollowedForumColumns.FORUM_NAME, value);
        return this;
    }

    public FollowedForumSelection orderByForumName(boolean desc) {
        orderBy(FollowedForumColumns.FORUM_NAME, desc);
        return this;
    }

    public FollowedForumSelection orderByForumName() {
        orderBy(FollowedForumColumns.FORUM_NAME, false);
        return this;
    }

    public FollowedForumSelection forumIcon(String... value) {
        addEquals(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection forumIconNot(String... value) {
        addNotEquals(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection forumIconLike(String... value) {
        addLike(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection forumIconContains(String... value) {
        addContains(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection forumIconStartsWith(String... value) {
        addStartsWith(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection forumIconEndsWith(String... value) {
        addEndsWith(FollowedForumColumns.FORUM_ICON, value);
        return this;
    }

    public FollowedForumSelection orderByForumIcon(boolean desc) {
        orderBy(FollowedForumColumns.FORUM_ICON, desc);
        return this;
    }

    public FollowedForumSelection orderByForumIcon() {
        orderBy(FollowedForumColumns.FORUM_ICON, false);
        return this;
    }

    public FollowedForumSelection forumSortValue(long... value) {
        addEquals(FollowedForumColumns.FORUM_SORT_VALUE, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection forumSortValueNot(long... value) {
        addNotEquals(FollowedForumColumns.FORUM_SORT_VALUE, toObjectArray(value));
        return this;
    }

    public FollowedForumSelection forumSortValueGt(long value) {
        addGreaterThan(FollowedForumColumns.FORUM_SORT_VALUE, value);
        return this;
    }

    public FollowedForumSelection forumSortValueGtEq(long value) {
        addGreaterThanOrEquals(FollowedForumColumns.FORUM_SORT_VALUE, value);
        return this;
    }

    public FollowedForumSelection forumSortValueLt(long value) {
        addLessThan(FollowedForumColumns.FORUM_SORT_VALUE, value);
        return this;
    }

    public FollowedForumSelection forumSortValueLtEq(long value) {
        addLessThanOrEquals(FollowedForumColumns.FORUM_SORT_VALUE, value);
        return this;
    }

    public FollowedForumSelection orderByForumSortValue(boolean desc) {
        orderBy(FollowedForumColumns.FORUM_SORT_VALUE, desc);
        return this;
    }

    public FollowedForumSelection orderByForumSortValue() {
        orderBy(FollowedForumColumns.FORUM_SORT_VALUE, false);
        return this;
    }
}
