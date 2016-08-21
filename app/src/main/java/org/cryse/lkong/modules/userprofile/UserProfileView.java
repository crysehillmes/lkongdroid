package org.cryse.lkong.modules.userprofile;

import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.modules.base.ContentView;

public interface UserProfileView extends ContentView {
    void onLoadUserProfileComplete(UserInfoModel userInfoModel);
    void onCheckFollowStatusComplete(boolean isFollowed);
    void onCheckBlockStatusComplete(boolean isBlocked);
}
