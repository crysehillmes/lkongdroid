package org.cryse.lkong.application;

import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.utils.preference.LongPreference;

import java.util.ArrayList;
import java.util.Collections;
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
                int index = -1;
                for (int i = 0; i < mUserAccounts.size(); i++) {
                    if(mUserAccounts.get(i).getUserId() == uid) {
                        mCurrentUserAccount = mUserAccounts.get(i);
                        index = i;
                    }
                }
                if(index != -1) {
                    Collections.swap(mUserAccounts, 0, index);
                }
            }
        } catch (Exception ex) {
            Timber.e(ex, "getAllUserAccounts() failed.", LOG_TAG);
        }
    }

    public UserAccountEntity getCurrentUserAccount() {
        if(mCurrentUserAccount == null && mUserAccounts.size() > 0) {
            mCurrentUserAccount = mUserAccounts.get(0);
            mDefaultAccountUid.set(mCurrentUserAccount.getUserId());
            mAuthObject = mCurrentUserAccount.getAuthObject();
            return mCurrentUserAccount;
        }
        if(mCurrentUserAccount != null) return mCurrentUserAccount;
        throw new NeedSignInException("You should sign in before you do this.");
    }

    public LKAuthObject getAuthObject() {
        if(mAuthObject == null && mCurrentUserAccount != null) {
            mAuthObject = mCurrentUserAccount.getAuthObject();
        }
        return mAuthObject;
    }

    public boolean isSignedIn() {
        if(mCurrentUserAccount != null || mUserAccounts.size() > 0)
            return true;
        return false;
    }

    public List<UserAccountEntity> getUserAccounts() {
        return mUserAccounts;
    }

    public long getCurrentUserId() {
        return this.mDefaultAccountUid.get();
    }

    public void setCurrentUserAccount(long userId) {
        int index = -1;
        for (int i = 0; i < mUserAccounts.size(); i++) {
            UserAccountEntity entity = mUserAccounts.get(i);
            if (entity.getUserId() == userId) {
                index = i;
                this.mCurrentUserAccount = entity;
                this.mAuthObject = mCurrentUserAccount.getAuthObject();
                this.mDefaultAccountUid.set(userId);
            }
        }
        if(index != -1) {
            Collections.swap(mUserAccounts, 0, index);
        }
    }
}
