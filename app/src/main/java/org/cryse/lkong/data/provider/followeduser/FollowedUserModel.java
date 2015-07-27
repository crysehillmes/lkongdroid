package org.cryse.lkong.data.provider.followeduser;

import org.cryse.lkong.data.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Followed user.
 */
public interface FollowedUserModel extends BaseModel {

    /**
     * Self id.
     */
    long getUserId();

    /**
     * Follow target user id.
     */
    long getTargetUserId();
}
