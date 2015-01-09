package org.cryse.lkong.view;

import org.cryse.lkong.logic.restservice.model.UserInfo;

public interface UserInfoView extends ContentView {
    public void showUserInfo(UserInfo userConfigInfo);
}
