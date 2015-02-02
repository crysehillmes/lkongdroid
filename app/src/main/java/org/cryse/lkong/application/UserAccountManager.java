package org.cryse.lkong.application;

import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.utils.preference.LongPreference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class UserAccountManager {
    private static final String LOG_TAG = UserAccountManager.class.getName();
    private List<UserAccountEntity> mUserAccounts = new ArrayList<UserAccountEntity>();
    private UserAccountEntity mCurrentUserAccount;
    private LKAuthObject mAuthObject;
    @Inject
    LKongDatabase mLKongDatabase;

    @Inject
    @PrefsDefaultAccountUid
    LongPreference mDefaultAccountUid;

    public UserAccountManager() {
    }

    public void init() {
        refresh();
    }

    public void refresh() {
        long uid = mDefaultAccountUid.get();
        try {
            mUserAccounts.clear();
            mUserAccounts.addAll(this.mLKongDatabase.getAllUserAccounts());
            if(uid >= 0) {
                mCurrentUserAccount = mLKongDatabase.getUserAccount(uid);
            }
        } catch (Exception ex) {
            Timber.e(ex, "getAllUserAccounts() failed.", LOG_TAG);
        }
    }

    public UserAccountEntity getCurrentUserAccount() {
        if(mCurrentUserAccount != null) return mCurrentUserAccount;
        if(mUserAccounts.size() > 0) {
            mCurrentUserAccount = mUserAccounts.get(0);
            mDefaultAccountUid.set(mCurrentUserAccount.getUserId());
            mAuthObject = mCurrentUserAccount.getAuthObject();
            return mCurrentUserAccount;
        }
        throw new IllegalStateException("You should sign in before you do this.");
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
                this.mDefaultAccountUid.set(userId);
            }
        }
    }
}
