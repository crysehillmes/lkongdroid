package org.cryse.lkong.data.model;

import org.cryse.lkong.data.provider.followeduser.FollowedUserModel;

public class FollowedUser implements FollowedUserModel {
    private long userId;
    private long targetUserId;

    public FollowedUser(long userId, long targetUserId) {
        this.userId = userId;
        this.targetUserId = targetUserId;
    }

    public FollowedUser(FollowedUserModel model) {
        this.userId = model.getUserId();
        this.targetUserId = model.getTargetUserId();
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getTargetUserId() {
        return targetUserId;
    }
}
