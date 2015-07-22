package org.cryse.lkong.data.provider.followeduser;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.lkong.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code followed_user} table.
 */
public class FollowedUserSelection extends AbstractSelection<FollowedUserSelection> {
    @Override
    protected Uri baseUri() {
        return FollowedUserColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedUserCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedUserCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedUserCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public FollowedUserCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code FollowedUserCursor} object, which is positioned before the first entry, or null.
     */
    public FollowedUserCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new FollowedUserCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public FollowedUserCursor query(Context context) {
        return query(context, null);
    }


    public FollowedUserSelection id(long... value) {
        addEquals("followed_user." + FollowedUserColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection idNot(long... value) {
        addNotEquals("followed_user." + FollowedUserColumns._ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection orderById(boolean desc) {
        orderBy("followed_user." + FollowedUserColumns._ID, desc);
        return this;
    }

    public FollowedUserSelection orderById() {
        return orderById(false);
    }

    public FollowedUserSelection userId(long... value) {
        addEquals(FollowedUserColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection userIdNot(long... value) {
        addNotEquals(FollowedUserColumns.USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection userIdGt(long value) {
        addGreaterThan(FollowedUserColumns.USER_ID, value);
        return this;
    }

    public FollowedUserSelection userIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedUserColumns.USER_ID, value);
        return this;
    }

    public FollowedUserSelection userIdLt(long value) {
        addLessThan(FollowedUserColumns.USER_ID, value);
        return this;
    }

    public FollowedUserSelection userIdLtEq(long value) {
        addLessThanOrEquals(FollowedUserColumns.USER_ID, value);
        return this;
    }

    public FollowedUserSelection orderByUserId(boolean desc) {
        orderBy(FollowedUserColumns.USER_ID, desc);
        return this;
    }

    public FollowedUserSelection orderByUserId() {
        orderBy(FollowedUserColumns.USER_ID, false);
        return this;
    }

    public FollowedUserSelection targetUserId(long... value) {
        addEquals(FollowedUserColumns.TARGET_USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection targetUserIdNot(long... value) {
        addNotEquals(FollowedUserColumns.TARGET_USER_ID, toObjectArray(value));
        return this;
    }

    public FollowedUserSelection targetUserIdGt(long value) {
        addGreaterThan(FollowedUserColumns.TARGET_USER_ID, value);
        return this;
    }

    public FollowedUserSelection targetUserIdGtEq(long value) {
        addGreaterThanOrEquals(FollowedUserColumns.TARGET_USER_ID, value);
        return this;
    }

    public FollowedUserSelection targetUserIdLt(long value) {
        addLessThan(FollowedUserColumns.TARGET_USER_ID, value);
        return this;
    }

    public FollowedUserSelection targetUserIdLtEq(long value) {
        addLessThanOrEquals(FollowedUserColumns.TARGET_USER_ID, value);
        return this;
    }

    public FollowedUserSelection orderByTargetUserId(boolean desc) {
        orderBy(FollowedUserColumns.TARGET_USER_ID, desc);
        return this;
    }

    public FollowedUserSelection orderByTargetUserId() {
        orderBy(FollowedUserColumns.TARGET_USER_ID, false);
        return this;
    }
}
