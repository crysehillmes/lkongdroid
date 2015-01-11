package org.cryse.lkong.view;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.UserInfoModel;

public interface UserAccountView extends ContentView {
    public void showUserAccount(UserAccountEntity userAccount);
}
