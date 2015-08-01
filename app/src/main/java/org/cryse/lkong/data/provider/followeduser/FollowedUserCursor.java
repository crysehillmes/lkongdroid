package org.cryse.lkong.data.provider.followeduser;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code followed_user} table.
 */
public class FollowedUserCursor extends AbstractCursor implements FollowedUserModel {
    public FollowedUserCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(FollowedUserColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Self id.
     */
    public long getUserId() {
        Long res = getLongOrNull(FollowedUserColumns.USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Follow target user id.
     */
    public long getTargetUserId() {
        Long res = getLongOrNull(FollowedUserColumns.TARGET_USER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'target_user_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
