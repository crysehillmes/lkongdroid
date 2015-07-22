package org.cryse.lkong.data.provider.followeduser;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code followed_user} table.
 */
public class FollowedUserContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return FollowedUserColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable FollowedUserSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable FollowedUserSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Self id.
     */
    public FollowedUserContentValues putUserId(long value) {
        mContentValues.put(FollowedUserColumns.USER_ID, value);
        return this;
    }


    /**
     * Follow target user id.
     */
    public FollowedUserContentValues putTargetUserId(long value) {
        mContentValues.put(FollowedUserColumns.TARGET_USER_ID, value);
        return this;
    }

}
