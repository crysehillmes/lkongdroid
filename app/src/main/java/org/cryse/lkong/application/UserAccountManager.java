package org.cryse.lkong.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.cryse.lkong.account.AccountConst;
import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NewAccountEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.utils.preference.LongPreference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class UserAccountManager {
    private static final String LOG_TAG = UserAccountManager.class.getName();
    private LinkedHashMap<Long, UserAccount> mUserAccounts = new LinkedHashMap<Long, UserAccount>();
    private UserAccount mCurrentUserAccount;
    private LKAuthObject mAuthObject;

    @Inject
    @ApplicationContext
    Context mContext;

    @Inject
    LKongDatabase mLKongDatabase;

    @Inject
    @PrefsDefaultAccountUid
    LongPreference mDefaultAccountUid;

    @Inject
    AccountManager mAccountManager;

    @Inject
    RxEventBus mEventBus;

    private static final String mLKongAccountType = "org.cryse.lkong";

    public UserAccountManager() {
    }

    public void init() {
        refresh();
        mAccountManager.addOnAccountsUpdatedListener(new OnAccountsUpdateListener() {
            @Override
            public void onAccountsUpdated(Account[] accounts) {
                if(accounts !=null ) {
                    List<Account> lkongAccount = new ArrayList<Account>();
                    for (Account account : accounts) {
                        if(account.type.equals(mLKongAccountType)) {
                            lkongAccount.add(account);
                        }
                    }
                    if(mUserAccounts.size() != lkongAccount.size()) {
                        Log.d(LOG_TAG, "ACCOUNT COUNT CHANGED!");
                    }
                    if(mUserAccounts.size() == 0 && lkongAccount.size() != 0) {
                        refresh();
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mEventBus.sendEvent(new CurrentAccountChangedEvent());
                    } else if(lkongAccount.size() > mUserAccounts.size()){
                        refresh();
                        mEventBus.sendEvent(new NewAccountEvent());
                    } else if(lkongAccount.size() < mUserAccounts.size() && mUserAccounts.size() > 0) {
                        refresh();
                        mEventBus.sendEvent(new AccountRemovedEvent());
                    }
                }
            }
        }, null, true);
    }

    public void refresh() {
        long uid = mDefaultAccountUid.get();
        try {
            mUserAccounts.clear();
            Account[] accounts = mAccountManager.getAccountsByType(AccountConst.ACCOUNT_TYPE);
            for(Account account : accounts) {
                UserAccount userAccount = getUserAccountFromAccountManager(account, mAccountManager);
                mUserAccounts.put(userAccount.getUserId(), userAccount);
                // Log.d(LOG_TAG, String.format("USER[ \"name\": \"%s\", \"id\": \"%d\"]", userName, userId));
                // Log.d(LOG_TAG, String.format("Auth: %s, Dzsbhey: %s", userAuth, userDZSBHEY));
            }
            UserAccount currentAccount = mUserAccounts.get(uid);
            if(currentAccount == null && getFirst() != null) {
                setCurrentUserAccount(getFirst().getUserId());
            } else {
                mDefaultAccountUid.set(0l);
            }
            /*if(uid >= 0) {
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
            }*/
        } catch (Exception ex) {
            Timber.e(ex, "getAllUserAccounts() failed.", LOG_TAG);
        }
    }

    public UserAccount getCurrentUserAccount() {
        if(mCurrentUserAccount == null && mUserAccounts.size() > 0) {
            mCurrentUserAccount = getFirst();
            if(mCurrentUserAccount != null) {
                mDefaultAccountUid.set(mCurrentUserAccount.getUserId());
                mAuthObject = getAuthObject(mCurrentUserAccount);
            }
            return mCurrentUserAccount;
        }
        if(mCurrentUserAccount != null) return mCurrentUserAccount;
        throw new NeedSignInException("You should sign in before you do this.");
    }

    public LKAuthObject getAuthObject() {
        if(mAuthObject == null && mCurrentUserAccount != null) {
            mAuthObject = getAuthObject(mCurrentUserAccount);
        }
        return mAuthObject;
    }

    public boolean isSignedIn() {
        if(mUserAccounts.size() > 0)
            return true;
        return false;
    }

    public List<UserAccount> getUserAccounts() {
        return new ArrayList<UserAccount>(mUserAccounts.values());
    }

    public long getCurrentUserId() {
        return this.mDefaultAccountUid.get();
    }

    public void setCurrentUserAccount(long userId) {
        this.mCurrentUserAccount = mUserAccounts.get(userId);
        this.mAuthObject = getAuthObject(mCurrentUserAccount);
        this.mDefaultAccountUid.set(userId);
    }

    public UserAccount getUserAccount(long uid) {
        return mUserAccounts.get(uid);
    }

    public void signOut(long uid) throws Exception {
        // mLKongDatabase.removeUserAccount(uid);
        try {
            UserAccount userAccount = mUserAccounts.get(uid);
            Account account = userAccount.getAccount();
            mAccountManager.removeAccountExplicitly(account);
        } catch (Exception ex) {
            Timber.d(ex, ex.getMessage(), LOG_TAG);
        }
    }

    public static LKAuthObject getAuthObject(UserAccount userAccount) {
        return new LKAuthObject(
                userAccount.getUserId(),
                userAccount.getAuthURI(),
                userAccount.getAuthCookie(),
                userAccount.getDzsbheyURI(),
                userAccount.getDzsbheyCookie()
        );
    }

    private UserAccount getFirst() {
        Iterator<UserAccount> iter = mUserAccounts.values().iterator();
        if(iter.hasNext()) {
            return iter.next();
        } else {
            return  null;
        }
    }

    public static UserAccount getUserAccountFromAccountManager(Account account, AccountManager accountManager) {
        String idString = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_ID);
        long userId = Long.valueOf(TextUtils.isEmpty(idString) ? "0" : idString);
        String userEmail = account.name;
        String userName = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_NAME);
        String userAvatar = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_AVATAR);
        String userAuth = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_AUTH);
        String userDzsbhey = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_DZSBHEY);
        return new UserAccount(
                account,
                userId,
                userName,
                userEmail,
                userAvatar,
                userAuth,
                userDzsbhey
        );
    }
}
