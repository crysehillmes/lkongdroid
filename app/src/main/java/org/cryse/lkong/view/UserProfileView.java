package org.cryse.lkong.view;

import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.List;

public interface UserProfileView extends ContentViewEx {
    void onLoadUserProfileComplete(UserInfoModel userInfoModel);
    void onLoadUserProfileError(Throwable throwable, Object... extraInfo);
    void onLoadUserAllData(List<TimelineModel> items, boolean isLoadingMore);
    void onLoadUserThreads(List<ThreadModel> items, boolean isDigest, boolean isLoadingMore);
}
