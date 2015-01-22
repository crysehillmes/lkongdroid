package org.cryse.lkong.application;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserAccountManager {
    private List<UserAccountEntity> mUserAccounts = new ArrayList<UserAccountEntity>();
    private UserAccountEntity mCurrentUserAccount;
    private LKAuthObject mAuthObject;

    public UserAccountManager() {
    }

    public void setUserAccounts(Collection<UserAccountEntity> userAccounts, long currentUid) {
        mUserAccounts.addAll(userAccounts);
        setCurrentUserAccount(currentUid);
        if(this.mCurrentUserAccount == null) {
            throw new IllegalArgumentException();
        }
    }

    public UserAccountEntity getCurrentUserAccount() {
        return mCurrentUserAccount;
    }

    public LKAuthObject getAuthObject() {
        return mAuthObject;
    }

    public List<UserAccountEntity> getUserAccounts() {
        return mUserAccounts;
    }

    public void setCurrentUserAccount(long userId) {
        for(UserAccountEntity entity : mUserAccounts) {
            if(entity.getUserId() == userId) {
                this.mCurrentUserAccount = entity;
                this.mAuthObject = mCurrentUserAccount.getAuthObject();
            }
        }
    }
}
